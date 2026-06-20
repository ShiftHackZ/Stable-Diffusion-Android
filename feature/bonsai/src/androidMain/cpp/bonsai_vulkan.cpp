#include "bonsai_vulkan.h"

#include "bonsai_tensor.h"
#include "bonsai_tensor_storage.h"
#include "bonsai_vulkan_quantized_matvec_spv.h"

#include <android/log.h>
#include <vulkan/vulkan.h>

#include <algorithm>
#include <atomic>
#include <cctype>
#include <cstdint>
#include <cstring>
#include <exception>
#include <limits>
#include <memory>
#include <mutex>
#include <sstream>
#include <string>
#include <vector>

namespace {

constexpr const char* LOG_TAG = "SDAI-Bonsai";
constexpr uint64_t BYTES_IN_MB = 1024ULL * 1024ULL;
constexpr uint32_t MIN_STORAGE_BUFFER_RANGE = 64U * 1024U * 1024U;
constexpr uint64_t STATIC_CACHE_MAX_BYTES = 96ULL * BYTES_IN_MB;
constexpr uint64_t SHARED_QUEUE_MAX_SEQUENCE_TOKENS = 16U;
constexpr float COMPUTE_QUEUE_PRIORITY = 0.35F;

struct BonsaiVulkanBuffer {
    VkBuffer buffer = VK_NULL_HANDLE;
    VkDeviceMemory memory = VK_NULL_HANDLE;
    VkDeviceSize size = 0;
    bool coherent = false;
};

struct BonsaiVulkanParams {
    uint32_t input_values = 0;
    uint32_t packed_columns = 0;
    uint32_t scale_groups = 0;
    uint32_t bits = 0;
    uint32_t group_size = 0;
    uint32_t output_rows = 0;
};

enum class BonsaiVulkanStaticBufferKind {
    RawView,
    F32View,
};

struct BonsaiVulkanStaticBufferKey {
    const uint8_t* data = nullptr;
    uint64_t source_byte_count = 0;
    uint64_t element_count = 0;
    BonsaiDType dtype = BonsaiDType::Bool;
    BonsaiVulkanStaticBufferKind kind = BonsaiVulkanStaticBufferKind::RawView;
};

struct BonsaiVulkanStaticBufferEntry {
    BonsaiVulkanStaticBufferKey key;
    BonsaiVulkanBuffer buffer;
    VkDeviceSize byte_count = 0;
    uint64_t last_used = 0;
};

std::atomic_bool g_logged_available(false);
std::atomic_bool g_logged_cache(false);
std::atomic_bool g_logged_success(false);
std::atomic_bool g_logged_fallback(false);
std::atomic_bool g_logged_runtime_disabled(false);
std::atomic<int> g_backend_mode(static_cast<int>(BonsaiVulkanBackendMode::Auto));

std::string bool_value(bool value) {
    return value ? "true" : "false";
}

std::string version_string(uint32_t version) {
    std::ostringstream output;
    output << VK_VERSION_MAJOR(version) << "."
        << VK_VERSION_MINOR(version) << "."
        << VK_VERSION_PATCH(version);
    return output.str();
}

std::string vk_result_name(VkResult result) {
    switch (result) {
        case VK_SUCCESS:
            return "VK_SUCCESS";
        case VK_TIMEOUT:
            return "VK_TIMEOUT";
        case VK_ERROR_OUT_OF_HOST_MEMORY:
            return "VK_ERROR_OUT_OF_HOST_MEMORY";
        case VK_ERROR_OUT_OF_DEVICE_MEMORY:
            return "VK_ERROR_OUT_OF_DEVICE_MEMORY";
        case VK_ERROR_INITIALIZATION_FAILED:
            return "VK_ERROR_INITIALIZATION_FAILED";
        case VK_ERROR_DEVICE_LOST:
            return "VK_ERROR_DEVICE_LOST";
        case VK_ERROR_MEMORY_MAP_FAILED:
            return "VK_ERROR_MEMORY_MAP_FAILED";
        case VK_ERROR_FEATURE_NOT_PRESENT:
            return "VK_ERROR_FEATURE_NOT_PRESENT";
        case VK_ERROR_INCOMPATIBLE_DRIVER:
            return "VK_ERROR_INCOMPATIBLE_DRIVER";
        case VK_ERROR_FORMAT_NOT_SUPPORTED:
            return "VK_ERROR_FORMAT_NOT_SUPPORTED";
        default:
            return "VK_RESULT_" + std::to_string(static_cast<int>(result));
    }
}

std::string lowercase_ascii(std::string value) {
    std::transform(value.begin(), value.end(), value.begin(), [](unsigned char character) {
        return static_cast<char>(std::tolower(character));
    });
    return value;
}

bool is_software_vulkan_device(const VkPhysicalDeviceProperties& properties) {
    const std::string device_name = lowercase_ascii(properties.deviceName);
    return properties.deviceType == VK_PHYSICAL_DEVICE_TYPE_CPU ||
        device_name.find("llvmpipe") != std::string::npos ||
        device_name.find("swiftshader") != std::string::npos ||
        device_name.find("software") != std::string::npos;
}

BonsaiVulkanBackendMode current_backend_mode() {
    return static_cast<BonsaiVulkanBackendMode>(g_backend_mode.load());
}

const char* backend_mode_name(BonsaiVulkanBackendMode mode) {
    switch (mode) {
        case BonsaiVulkanBackendMode::Auto:
            return "auto";
        case BonsaiVulkanBackendMode::Cpu:
            return "cpu";
        case BonsaiVulkanBackendMode::Vulkan:
            return "vulkan";
    }
    return "auto";
}

uint32_t loader_api_version() {
    uint32_t version = VK_API_VERSION_1_0;
    const auto enumerate_instance_version =
        reinterpret_cast<PFN_vkEnumerateInstanceVersion>(
            vkGetInstanceProcAddr(nullptr, "vkEnumerateInstanceVersion")
        );
    if (enumerate_instance_version != nullptr &&
        enumerate_instance_version(&version) != VK_SUCCESS) {
        return VK_API_VERSION_1_0;
    }
    return version;
}

bool find_compute_queue_family(
    VkPhysicalDevice physical_device,
    uint32_t& queue_family_index,
    bool& compute_only
) {
    uint32_t queue_count = 0;
    vkGetPhysicalDeviceQueueFamilyProperties(physical_device, &queue_count, nullptr);
    if (queue_count == 0) {
        return false;
    }

    std::vector<VkQueueFamilyProperties> queues(queue_count);
    vkGetPhysicalDeviceQueueFamilyProperties(physical_device, &queue_count, queues.data());
    int32_t fallback_index = -1;
    for (uint32_t index = 0; index < queue_count; ++index) {
        const VkQueueFamilyProperties& queue = queues[index];
        if ((queue.queueFlags & VK_QUEUE_COMPUTE_BIT) != 0 && queue.queueCount > 0) {
            if ((queue.queueFlags & VK_QUEUE_GRAPHICS_BIT) == 0) {
                queue_family_index = index;
                compute_only = true;
                return true;
            }
            if (fallback_index < 0) {
                fallback_index = static_cast<int32_t>(index);
            }
        }
    }
    if (fallback_index >= 0) {
        queue_family_index = static_cast<uint32_t>(fallback_index);
        compute_only = false;
        return true;
    }
    return false;
}

int32_t find_memory_type(
    VkPhysicalDevice physical_device,
    uint32_t type_bits,
    VkMemoryPropertyFlags required_properties
) {
    VkPhysicalDeviceMemoryProperties memory_properties {};
    vkGetPhysicalDeviceMemoryProperties(physical_device, &memory_properties);
    for (uint32_t index = 0; index < memory_properties.memoryTypeCount; ++index) {
        const bool type_supported = (type_bits & (1U << index)) != 0;
        const bool properties_supported =
            (memory_properties.memoryTypes[index].propertyFlags & required_properties) ==
                required_properties;
        if (type_supported && properties_supported) {
            return static_cast<int32_t>(index);
        }
    }
    return -1;
}

uint64_t last_dimension(const BonsaiTensorView& view) {
    if (view.descriptor == nullptr || view.descriptor->shape.empty()) {
        return 0;
    }
    return view.descriptor->shape.back();
}

bool fits_u32(uint64_t value) {
    return value <= static_cast<uint64_t>(std::numeric_limits<uint32_t>::max());
}

bool same_static_buffer_key(
    const BonsaiVulkanStaticBufferKey& left,
    const BonsaiVulkanStaticBufferKey& right
) {
    return left.data == right.data &&
        left.source_byte_count == right.source_byte_count &&
        left.element_count == right.element_count &&
        left.dtype == right.dtype &&
        left.kind == right.kind;
}

void destroy_buffer(VkDevice device, BonsaiVulkanBuffer& buffer) {
    if (buffer.buffer != VK_NULL_HANDLE) {
        vkDestroyBuffer(device, buffer.buffer, nullptr);
        buffer.buffer = VK_NULL_HANDLE;
    }
    if (buffer.memory != VK_NULL_HANDLE) {
        vkFreeMemory(device, buffer.memory, nullptr);
        buffer.memory = VK_NULL_HANDLE;
    }
}

class BonsaiVulkanRuntime {
public:
    ~BonsaiVulkanRuntime() {
        if (device_ != VK_NULL_HANDLE) {
            clear_static_cache();
            if (command_pool_ != VK_NULL_HANDLE) {
                vkDestroyCommandPool(device_, command_pool_, nullptr);
            }
            if (pipeline_ != VK_NULL_HANDLE) {
                vkDestroyPipeline(device_, pipeline_, nullptr);
            }
            if (shader_module_ != VK_NULL_HANDLE) {
                vkDestroyShaderModule(device_, shader_module_, nullptr);
            }
            if (pipeline_layout_ != VK_NULL_HANDLE) {
                vkDestroyPipelineLayout(device_, pipeline_layout_, nullptr);
            }
            if (descriptor_set_layout_ != VK_NULL_HANDLE) {
                vkDestroyDescriptorSetLayout(device_, descriptor_set_layout_, nullptr);
            }
            vkDestroyDevice(device_, nullptr);
        }
        if (instance_ != VK_NULL_HANDLE) {
            vkDestroyInstance(instance_, nullptr);
        }
    }

    bool available() {
        std::call_once(init_once_, [this]() {
            available_ = initialize();
            __android_log_print(
                ANDROID_LOG_INFO,
                LOG_TAG,
                "phase=vulkan_runtime available=%s reason=%s device=%s api=%s maxStorageBufferRangeMb=%llu queueFamily=%u computeOnly=%s queuePriority=%.2f",
                bool_value(available_).c_str(),
                init_reason_.c_str(),
                device_name_.c_str(),
                api_version_.c_str(),
                static_cast<unsigned long long>(max_storage_buffer_range_ / BYTES_IN_MB),
                queue_family_index_,
                bool_value(queue_family_compute_only_).c_str(),
                COMPUTE_QUEUE_PRIORITY
            );
        });
        return available_;
    }

    bool quantized_matvec_into(
        const BonsaiPackedWeightViews& views,
        const float* input,
        float* output,
        std::string& reason
    ) {
        return quantized_matvec_sequence_into(views, input, output, 1, reason);
    }

    bool quantized_matvec_sequence_into(
        const BonsaiPackedWeightViews& views,
        const float* input,
        float* output,
        uint64_t token_count,
        std::string& reason
    ) {
        if (current_backend_mode() == BonsaiVulkanBackendMode::Cpu) {
            reason = "backend_cpu";
            return false;
        }
        if (!available()) {
            reason = init_reason_;
            return false;
        }
        if (disabled_after_device_loss_.load()) {
            reason = "vulkan_disabled_after_device_lost";
            return false;
        }
        if (!supports_views(views, reason)) {
            return false;
        }
        if (!supports_sequence(views, token_count, reason)) {
            return false;
        }

        const uint64_t scale_groups = last_dimension(views.scales);
        const BonsaiVulkanParams params {
            static_cast<uint32_t>(views.input_values),
            static_cast<uint32_t>(last_dimension(views.weight)),
            static_cast<uint32_t>(scale_groups),
            static_cast<uint32_t>(views.bits),
            static_cast<uint32_t>(views.group_size),
            static_cast<uint32_t>(views.leading_rows),
        };

        std::lock_guard<std::mutex> lock(queue_mutex_);
        return dispatch_quantized_matvec(
            views,
            input,
            output,
            token_count,
            params,
            reason
        );
    }

private:
    bool initialize() {
        const uint32_t loader_version = loader_api_version();
        VkApplicationInfo app_info {};
        app_info.sType = VK_STRUCTURE_TYPE_APPLICATION_INFO;
        app_info.pApplicationName = "SDAI Bonsai Runtime";
        app_info.applicationVersion = 1;
        app_info.pEngineName = "SDAI";
        app_info.engineVersion = 1;
        app_info.apiVersion = loader_version >= VK_API_VERSION_1_1
            ? VK_API_VERSION_1_1
            : VK_API_VERSION_1_0;

        VkInstanceCreateInfo instance_info {};
        instance_info.sType = VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO;
        instance_info.pApplicationInfo = &app_info;
        VkResult result = vkCreateInstance(&instance_info, nullptr, &instance_);
        if (result != VK_SUCCESS) {
            init_reason_ = "create_instance_failed:" + vk_result_name(result);
            return false;
        }

        uint32_t device_count = 0;
        result = vkEnumeratePhysicalDevices(instance_, &device_count, nullptr);
        if (result != VK_SUCCESS || device_count == 0) {
            init_reason_ = "no_physical_devices:" + vk_result_name(result);
            return false;
        }

        std::vector<VkPhysicalDevice> devices(device_count);
        result = vkEnumeratePhysicalDevices(instance_, &device_count, devices.data());
        if (result != VK_SUCCESS) {
            init_reason_ = "enumerate_devices_failed:" + vk_result_name(result);
            return false;
        }

        for (VkPhysicalDevice candidate : devices) {
            if (select_physical_device(candidate)) {
                break;
            }
        }
        if (physical_device_ == VK_NULL_HANDLE) {
            init_reason_ = last_candidate_rejection_.empty()
                ? "no_usable_compute_device"
                : last_candidate_rejection_;
            return false;
        }

        if (!create_device()) {
            return false;
        }
        if (!create_pipeline()) {
            return false;
        }
        init_reason_ = "ok";
        return true;
    }

    bool select_physical_device(VkPhysicalDevice candidate) {
        VkPhysicalDeviceProperties properties {};
        vkGetPhysicalDeviceProperties(candidate, &properties);
        if (is_software_vulkan_device(properties)) {
            last_candidate_rejection_ = "software_vulkan_device:";
            last_candidate_rejection_ += properties.deviceName;
            return false;
        }
        uint32_t candidate_queue_family = 0;
        bool candidate_compute_only = false;
        if (properties.apiVersion < VK_API_VERSION_1_1 ||
            properties.limits.maxStorageBufferRange < MIN_STORAGE_BUFFER_RANGE ||
            !find_compute_queue_family(
                candidate,
                candidate_queue_family,
                candidate_compute_only
            )) {
            return false;
        }
        physical_device_ = candidate;
        queue_family_index_ = candidate_queue_family;
        queue_family_compute_only_ = candidate_compute_only;
        max_storage_buffer_range_ = properties.limits.maxStorageBufferRange;
        max_workgroup_count_x_ = properties.limits.maxComputeWorkGroupCount[0];
        max_workgroup_count_y_ = properties.limits.maxComputeWorkGroupCount[1];
        device_name_ = properties.deviceName;
        api_version_ = version_string(properties.apiVersion);
        return true;
    }

    bool create_device() {
        const float priority = COMPUTE_QUEUE_PRIORITY;
        VkDeviceQueueCreateInfo queue_info {};
        queue_info.sType = VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO;
        queue_info.queueFamilyIndex = queue_family_index_;
        queue_info.queueCount = 1;
        queue_info.pQueuePriorities = &priority;

        VkDeviceCreateInfo device_info {};
        device_info.sType = VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO;
        device_info.queueCreateInfoCount = 1;
        device_info.pQueueCreateInfos = &queue_info;
        VkResult result = vkCreateDevice(physical_device_, &device_info, nullptr, &device_);
        if (result != VK_SUCCESS) {
            init_reason_ = "create_device_failed:" + vk_result_name(result);
            return false;
        }
        vkGetDeviceQueue(device_, queue_family_index_, 0, &queue_);
        if (queue_ == VK_NULL_HANDLE) {
            init_reason_ = "get_queue_failed";
            return false;
        }

        VkCommandPoolCreateInfo command_pool_info {};
        command_pool_info.sType = VK_STRUCTURE_TYPE_COMMAND_POOL_CREATE_INFO;
        command_pool_info.flags = VK_COMMAND_POOL_CREATE_RESET_COMMAND_BUFFER_BIT;
        command_pool_info.queueFamilyIndex = queue_family_index_;
        result = vkCreateCommandPool(device_, &command_pool_info, nullptr, &command_pool_);
        if (result != VK_SUCCESS) {
            init_reason_ = "create_command_pool_failed:" + vk_result_name(result);
            return false;
        }
        return true;
    }

    bool create_pipeline() {
        VkDescriptorSetLayoutBinding bindings[5] {};
        for (uint32_t index = 0; index < 5U; ++index) {
            bindings[index].binding = index;
            bindings[index].descriptorType = VK_DESCRIPTOR_TYPE_STORAGE_BUFFER;
            bindings[index].descriptorCount = 1;
            bindings[index].stageFlags = VK_SHADER_STAGE_COMPUTE_BIT;
        }

        VkDescriptorSetLayoutCreateInfo set_layout_info {};
        set_layout_info.sType = VK_STRUCTURE_TYPE_DESCRIPTOR_SET_LAYOUT_CREATE_INFO;
        set_layout_info.bindingCount = 5;
        set_layout_info.pBindings = bindings;
        VkResult result = vkCreateDescriptorSetLayout(
            device_,
            &set_layout_info,
            nullptr,
            &descriptor_set_layout_
        );
        if (result != VK_SUCCESS) {
            init_reason_ = "create_descriptor_set_layout_failed:" + vk_result_name(result);
            return false;
        }

        VkPushConstantRange push_constant {};
        push_constant.stageFlags = VK_SHADER_STAGE_COMPUTE_BIT;
        push_constant.offset = 0;
        push_constant.size = sizeof(BonsaiVulkanParams);

        VkPipelineLayoutCreateInfo pipeline_layout_info {};
        pipeline_layout_info.sType = VK_STRUCTURE_TYPE_PIPELINE_LAYOUT_CREATE_INFO;
        pipeline_layout_info.setLayoutCount = 1;
        pipeline_layout_info.pSetLayouts = &descriptor_set_layout_;
        pipeline_layout_info.pushConstantRangeCount = 1;
        pipeline_layout_info.pPushConstantRanges = &push_constant;
        result = vkCreatePipelineLayout(
            device_,
            &pipeline_layout_info,
            nullptr,
            &pipeline_layout_
        );
        if (result != VK_SUCCESS) {
            init_reason_ = "create_pipeline_layout_failed:" + vk_result_name(result);
            return false;
        }

        VkShaderModuleCreateInfo shader_info {};
        shader_info.sType = VK_STRUCTURE_TYPE_SHADER_MODULE_CREATE_INFO;
        shader_info.codeSize = kBonsaiVulkanQuantizedMatvecSpvSize;
        shader_info.pCode = reinterpret_cast<const uint32_t*>(kBonsaiVulkanQuantizedMatvecSpv);
        result = vkCreateShaderModule(device_, &shader_info, nullptr, &shader_module_);
        if (result != VK_SUCCESS) {
            init_reason_ = "create_shader_module_failed:" + vk_result_name(result);
            return false;
        }

        VkComputePipelineCreateInfo pipeline_info {};
        pipeline_info.sType = VK_STRUCTURE_TYPE_COMPUTE_PIPELINE_CREATE_INFO;
        pipeline_info.stage.sType = VK_STRUCTURE_TYPE_PIPELINE_SHADER_STAGE_CREATE_INFO;
        pipeline_info.stage.stage = VK_SHADER_STAGE_COMPUTE_BIT;
        pipeline_info.stage.module = shader_module_;
        pipeline_info.stage.pName = "main";
        pipeline_info.layout = pipeline_layout_;
        result = vkCreateComputePipelines(
            device_,
            VK_NULL_HANDLE,
            1,
            &pipeline_info,
            nullptr,
            &pipeline_
        );
        if (result != VK_SUCCESS) {
            init_reason_ = "create_compute_pipeline_failed:" + vk_result_name(result);
            return false;
        }
        return true;
    }

    bool supports_views(const BonsaiPackedWeightViews& views, std::string& reason) const {
        if (!views.packed || views.weight.dtype != BonsaiDType::U32) {
            reason = "unsupported_weight_layout";
            return false;
        }
        if (views.bits != 1 && views.bits != 2 && views.bits != 4) {
            reason = "unsupported_bits";
            return false;
        }
        if (views.group_size <= 0 || views.input_values == 0 || views.leading_rows == 0) {
            reason = "invalid_shape";
            return false;
        }
        if (!fits_u32(views.input_values) ||
            !fits_u32(views.leading_rows) ||
            !fits_u32(last_dimension(views.weight)) ||
            !fits_u32(last_dimension(views.scales)) ||
            !fits_u32(static_cast<uint64_t>(views.group_size))) {
            reason = "shape_too_large";
            return false;
        }
        if (views.leading_rows > max_workgroup_count_x_) {
            reason = "too_many_rows_for_dispatch";
            return false;
        }
        if (views.weight.byte_count > max_storage_buffer_range_ ||
            views.scales.element_count * sizeof(float) > max_storage_buffer_range_ ||
            views.biases.element_count * sizeof(float) > max_storage_buffer_range_ ||
            views.input_values * sizeof(float) > max_storage_buffer_range_ ||
            views.leading_rows * sizeof(float) > max_storage_buffer_range_) {
            reason = "buffer_range_too_large";
            return false;
        }
        return true;
    }

    bool supports_sequence(
        const BonsaiPackedWeightViews& views,
        uint64_t token_count,
        std::string& reason
    ) const {
        if (token_count == 0 || !fits_u32(token_count)) {
            reason = "invalid_token_count";
            return false;
        }
        if (token_count > max_workgroup_count_y_) {
            reason = "too_many_tokens_for_dispatch";
            return false;
        }
        if (!queue_family_compute_only_ && token_count > SHARED_QUEUE_MAX_SEQUENCE_TOKENS) {
            reason = "shared_queue_token_limit";
            return false;
        }
        if (views.input_values > std::numeric_limits<uint64_t>::max() / token_count ||
            views.leading_rows > std::numeric_limits<uint64_t>::max() / token_count) {
            reason = "sequence_shape_too_large";
            return false;
        }

        const uint64_t input_elements = views.input_values * token_count;
        const uint64_t output_elements = views.leading_rows * token_count;
        if (input_elements > max_storage_buffer_range_ / sizeof(float) ||
            output_elements > max_storage_buffer_range_ / sizeof(float)) {
            reason = "sequence_buffer_range_too_large";
            return false;
        }
        return true;
    }

    bool create_host_storage_buffer(
        VkDeviceSize size,
        BonsaiVulkanBuffer& output,
        std::string& reason
    ) {
        output.size = size;

        VkBufferCreateInfo buffer_info {};
        buffer_info.sType = VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO;
        buffer_info.size = size;
        buffer_info.usage = VK_BUFFER_USAGE_STORAGE_BUFFER_BIT;
        buffer_info.sharingMode = VK_SHARING_MODE_EXCLUSIVE;
        VkResult result = vkCreateBuffer(device_, &buffer_info, nullptr, &output.buffer);
        if (result != VK_SUCCESS) {
            reason = "create_buffer_failed:" + vk_result_name(result);
            return false;
        }

        VkMemoryRequirements requirements {};
        vkGetBufferMemoryRequirements(device_, output.buffer, &requirements);
        int32_t memory_type = find_memory_type(
            physical_device_,
            requirements.memoryTypeBits,
            VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT
        );
        output.coherent = memory_type >= 0;
        if (memory_type < 0) {
            memory_type = find_memory_type(
                physical_device_,
                requirements.memoryTypeBits,
                VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT
            );
        }
        if (memory_type < 0) {
            reason = "missing_host_visible_memory";
            return false;
        }

        VkMemoryAllocateInfo allocate_info {};
        allocate_info.sType = VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO;
        allocate_info.allocationSize = requirements.size;
        allocate_info.memoryTypeIndex = static_cast<uint32_t>(memory_type);
        result = vkAllocateMemory(device_, &allocate_info, nullptr, &output.memory);
        if (result != VK_SUCCESS) {
            reason = "allocate_memory_failed:" + vk_result_name(result);
            return false;
        }

        result = vkBindBufferMemory(device_, output.buffer, output.memory, 0);
        if (result != VK_SUCCESS) {
            reason = "bind_buffer_failed:" + vk_result_name(result);
            return false;
        }
        return true;
    }

    bool write_buffer(
        const BonsaiVulkanBuffer& buffer,
        const void* source,
        size_t byte_count,
        std::string& reason
    ) {
        void* mapped = nullptr;
        VkResult result = vkMapMemory(device_, buffer.memory, 0, byte_count, 0, &mapped);
        if (result != VK_SUCCESS) {
            reason = "map_write_failed:" + vk_result_name(result);
            return false;
        }
        std::memcpy(mapped, source, byte_count);
        if (!buffer.coherent) {
            VkMappedMemoryRange range {};
            range.sType = VK_STRUCTURE_TYPE_MAPPED_MEMORY_RANGE;
            range.memory = buffer.memory;
            range.offset = 0;
            range.size = VK_WHOLE_SIZE;
            result = vkFlushMappedMemoryRanges(device_, 1, &range);
            if (result != VK_SUCCESS) {
                vkUnmapMemory(device_, buffer.memory);
                reason = "flush_write_failed:" + vk_result_name(result);
                return false;
            }
        }
        vkUnmapMemory(device_, buffer.memory);
        return true;
    }

    bool read_buffer(
        const BonsaiVulkanBuffer& buffer,
        void* destination,
        size_t byte_count,
        std::string& reason
    ) {
        if (!buffer.coherent) {
            VkMappedMemoryRange range {};
            range.sType = VK_STRUCTURE_TYPE_MAPPED_MEMORY_RANGE;
            range.memory = buffer.memory;
            range.offset = 0;
            range.size = VK_WHOLE_SIZE;
            VkResult result = vkInvalidateMappedMemoryRanges(device_, 1, &range);
            if (result != VK_SUCCESS) {
                reason = "invalidate_read_failed:" + vk_result_name(result);
                return false;
            }
        }

        void* mapped = nullptr;
        VkResult result = vkMapMemory(device_, buffer.memory, 0, byte_count, 0, &mapped);
        if (result != VK_SUCCESS) {
            reason = "map_read_failed:" + vk_result_name(result);
            return false;
        }
        std::memcpy(destination, mapped, byte_count);
        vkUnmapMemory(device_, buffer.memory);
        return true;
    }

    BonsaiVulkanStaticBufferEntry* find_static_buffer(
        const BonsaiVulkanStaticBufferKey& key
    ) {
        for (const std::unique_ptr<BonsaiVulkanStaticBufferEntry>& entry : static_cache_) {
            if (same_static_buffer_key(entry->key, key)) {
                entry->last_used = ++static_cache_tick_;
                return entry.get();
            }
        }
        return nullptr;
    }

    BonsaiVulkanStaticBufferEntry* cached_static_buffer(
        const BonsaiVulkanStaticBufferKey& key,
        VkDeviceSize byte_count,
        const void* source,
        std::string& reason
    ) {
        if (byte_count == 0 || source == nullptr) {
            reason = "empty_static_buffer";
            return nullptr;
        }

        BonsaiVulkanStaticBufferEntry* cached = find_static_buffer(key);
        if (cached != nullptr) {
            return cached;
        }

        auto entry = std::make_unique<BonsaiVulkanStaticBufferEntry>();
        entry->key = key;
        entry->byte_count = byte_count;
        entry->last_used = ++static_cache_tick_;
        if (!create_host_storage_buffer(byte_count, entry->buffer, reason) ||
            !write_buffer(entry->buffer, source, static_cast<size_t>(byte_count), reason)) {
            destroy_buffer(device_, entry->buffer);
            return nullptr;
        }

        BonsaiVulkanStaticBufferEntry* pointer = entry.get();
        static_cache_bytes_ += static_cast<uint64_t>(byte_count);
        static_cache_.push_back(std::move(entry));
        log_static_cache_once();
        return pointer;
    }

    BonsaiVulkanStaticBufferEntry* cached_raw_view_buffer(
        const BonsaiTensorView& view,
        std::string& reason
    ) {
        BonsaiVulkanStaticBufferKey key {
            view.data,
            view.byte_count,
            view.element_count,
            view.dtype,
            BonsaiVulkanStaticBufferKind::RawView,
        };
        return cached_static_buffer(
            key,
            static_cast<VkDeviceSize>(view.byte_count),
            view.data,
            reason
        );
    }

    BonsaiVulkanStaticBufferEntry* cached_f32_view_buffer(
        const BonsaiTensorView& view,
        std::string& reason
    ) {
        BonsaiVulkanStaticBufferKey key {
            view.data,
            view.byte_count,
            view.element_count,
            view.dtype,
            BonsaiVulkanStaticBufferKind::F32View,
        };
        BonsaiVulkanStaticBufferEntry* cached = find_static_buffer(key);
        if (cached != nullptr) {
            return cached;
        }

        std::vector<float> values;
        try {
            values = bonsai_tensor_view_to_f32_vector(view);
        } catch (const std::exception& error) {
            reason = "f32_static_buffer_conversion_failed:";
            reason += error.what();
            return nullptr;
        } catch (...) {
            reason = "f32_static_buffer_conversion_failed";
            return nullptr;
        }
        return cached_static_buffer(
            key,
            static_cast<VkDeviceSize>(values.size() * sizeof(float)),
            values.data(),
            reason
        );
    }

    void trim_static_cache() {
        while (static_cache_bytes_ > STATIC_CACHE_MAX_BYTES && static_cache_.size() > 1U) {
            auto victim = std::min_element(
                static_cache_.begin(),
                static_cache_.end(),
                [](const auto& left, const auto& right) {
                    return left->last_used < right->last_used;
                }
            );
            if (victim == static_cache_.end()) {
                return;
            }
            static_cache_bytes_ -= static_cast<uint64_t>((*victim)->byte_count);
            destroy_buffer(device_, (*victim)->buffer);
            static_cache_.erase(victim);
        }
    }

    void clear_static_cache() {
        for (const std::unique_ptr<BonsaiVulkanStaticBufferEntry>& entry : static_cache_) {
            destroy_buffer(device_, entry->buffer);
        }
        static_cache_.clear();
        static_cache_bytes_ = 0;
    }

    void log_static_cache_once() const {
        bool expected = false;
        if (g_logged_cache.compare_exchange_strong(expected, true)) {
            __android_log_print(
                ANDROID_LOG_INFO,
                LOG_TAG,
                "phase=vulkan_static_cache enabled=true maxMb=%llu",
                static_cast<unsigned long long>(STATIC_CACHE_MAX_BYTES / BYTES_IN_MB)
            );
        }
    }

    bool dispatch_quantized_matvec(
        const BonsaiPackedWeightViews& views,
        const float* input,
        float* output,
        uint64_t token_count,
        const BonsaiVulkanParams& params,
        std::string& reason
    ) {
        BonsaiVulkanBuffer input_buffer;
        BonsaiVulkanBuffer output_buffer;
        VkDescriptorPool descriptor_pool = VK_NULL_HANDLE;
        VkCommandBuffer command_buffer = VK_NULL_HANDLE;
        VkFence fence = VK_NULL_HANDLE;

        const auto cleanup = [&]() {
            if (fence != VK_NULL_HANDLE) {
                vkDestroyFence(device_, fence, nullptr);
            }
            if (command_buffer != VK_NULL_HANDLE) {
                vkFreeCommandBuffers(device_, command_pool_, 1, &command_buffer);
            }
            if (descriptor_pool != VK_NULL_HANDLE) {
                vkDestroyDescriptorPool(device_, descriptor_pool, nullptr);
            }
            destroy_buffer(device_, input_buffer);
            destroy_buffer(device_, output_buffer);
        };

        BonsaiVulkanStaticBufferEntry* weight_entry = cached_raw_view_buffer(views.weight, reason);
        BonsaiVulkanStaticBufferEntry* scale_entry = cached_f32_view_buffer(views.scales, reason);
        BonsaiVulkanStaticBufferEntry* bias_entry = cached_f32_view_buffer(views.biases, reason);
        if (weight_entry == nullptr || scale_entry == nullptr || bias_entry == nullptr) {
            trim_static_cache();
            return false;
        }

        const VkDeviceSize weight_bytes = weight_entry->byte_count;
        const VkDeviceSize scale_bytes = scale_entry->byte_count;
        const VkDeviceSize bias_bytes = bias_entry->byte_count;
        const VkDeviceSize input_bytes = views.input_values * token_count * sizeof(float);
        const VkDeviceSize output_bytes = views.leading_rows * token_count * sizeof(float);

        bool ok = create_host_storage_buffer(input_bytes, input_buffer, reason) &&
            create_host_storage_buffer(output_bytes, output_buffer, reason);
        if (!ok) {
            cleanup();
            trim_static_cache();
            return false;
        }

        ok = write_buffer(input_buffer, input, static_cast<size_t>(input_bytes), reason);
        if (!ok) {
            cleanup();
            trim_static_cache();
            return false;
        }

        VkDescriptorPoolSize pool_size {};
        pool_size.type = VK_DESCRIPTOR_TYPE_STORAGE_BUFFER;
        pool_size.descriptorCount = 5;
        VkDescriptorPoolCreateInfo pool_info {};
        pool_info.sType = VK_STRUCTURE_TYPE_DESCRIPTOR_POOL_CREATE_INFO;
        pool_info.maxSets = 1;
        pool_info.poolSizeCount = 1;
        pool_info.pPoolSizes = &pool_size;
        VkResult result = vkCreateDescriptorPool(device_, &pool_info, nullptr, &descriptor_pool);
        if (result != VK_SUCCESS) {
            reason = "create_descriptor_pool_failed:" + vk_result_name(result);
            cleanup();
            trim_static_cache();
            return false;
        }

        VkDescriptorSet descriptor_set = VK_NULL_HANDLE;
        VkDescriptorSetAllocateInfo descriptor_allocate {};
        descriptor_allocate.sType = VK_STRUCTURE_TYPE_DESCRIPTOR_SET_ALLOCATE_INFO;
        descriptor_allocate.descriptorPool = descriptor_pool;
        descriptor_allocate.descriptorSetCount = 1;
        descriptor_allocate.pSetLayouts = &descriptor_set_layout_;
        result = vkAllocateDescriptorSets(device_, &descriptor_allocate, &descriptor_set);
        if (result != VK_SUCCESS) {
            reason = "allocate_descriptor_set_failed:" + vk_result_name(result);
            cleanup();
            trim_static_cache();
            return false;
        }

        VkDescriptorBufferInfo buffer_infos[5] {};
        buffer_infos[0] = { weight_entry->buffer.buffer, 0, weight_bytes };
        buffer_infos[1] = { scale_entry->buffer.buffer, 0, scale_bytes };
        buffer_infos[2] = { bias_entry->buffer.buffer, 0, bias_bytes };
        buffer_infos[3] = { input_buffer.buffer, 0, input_bytes };
        buffer_infos[4] = { output_buffer.buffer, 0, output_bytes };

        VkWriteDescriptorSet descriptor_writes[5] {};
        for (uint32_t index = 0; index < 5U; ++index) {
            descriptor_writes[index].sType = VK_STRUCTURE_TYPE_WRITE_DESCRIPTOR_SET;
            descriptor_writes[index].dstSet = descriptor_set;
            descriptor_writes[index].dstBinding = index;
            descriptor_writes[index].descriptorCount = 1;
            descriptor_writes[index].descriptorType = VK_DESCRIPTOR_TYPE_STORAGE_BUFFER;
            descriptor_writes[index].pBufferInfo = &buffer_infos[index];
        }
        vkUpdateDescriptorSets(device_, 5, descriptor_writes, 0, nullptr);

        VkCommandBufferAllocateInfo command_allocate {};
        command_allocate.sType = VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO;
        command_allocate.commandPool = command_pool_;
        command_allocate.level = VK_COMMAND_BUFFER_LEVEL_PRIMARY;
        command_allocate.commandBufferCount = 1;
        result = vkAllocateCommandBuffers(device_, &command_allocate, &command_buffer);
        if (result != VK_SUCCESS) {
            reason = "allocate_command_buffer_failed:" + vk_result_name(result);
            cleanup();
            trim_static_cache();
            return false;
        }

        VkCommandBufferBeginInfo begin_info {};
        begin_info.sType = VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO;
        begin_info.flags = VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT;
        result = vkBeginCommandBuffer(command_buffer, &begin_info);
        if (result != VK_SUCCESS) {
            reason = "begin_command_buffer_failed:" + vk_result_name(result);
            cleanup();
            trim_static_cache();
            return false;
        }

        vkCmdBindPipeline(command_buffer, VK_PIPELINE_BIND_POINT_COMPUTE, pipeline_);
        vkCmdBindDescriptorSets(
            command_buffer,
            VK_PIPELINE_BIND_POINT_COMPUTE,
            pipeline_layout_,
            0,
            1,
            &descriptor_set,
            0,
            nullptr
        );
        vkCmdPushConstants(
            command_buffer,
            pipeline_layout_,
            VK_SHADER_STAGE_COMPUTE_BIT,
            0,
            sizeof(BonsaiVulkanParams),
            &params
        );
        vkCmdDispatch(
            command_buffer,
            static_cast<uint32_t>(views.leading_rows),
            static_cast<uint32_t>(token_count),
            1
        );

        VkBufferMemoryBarrier barrier {};
        barrier.sType = VK_STRUCTURE_TYPE_BUFFER_MEMORY_BARRIER;
        barrier.srcAccessMask = VK_ACCESS_SHADER_WRITE_BIT;
        barrier.dstAccessMask = VK_ACCESS_HOST_READ_BIT;
        barrier.srcQueueFamilyIndex = VK_QUEUE_FAMILY_IGNORED;
        barrier.dstQueueFamilyIndex = VK_QUEUE_FAMILY_IGNORED;
        barrier.buffer = output_buffer.buffer;
        barrier.offset = 0;
        barrier.size = output_bytes;
        vkCmdPipelineBarrier(
            command_buffer,
            VK_PIPELINE_STAGE_COMPUTE_SHADER_BIT,
            VK_PIPELINE_STAGE_HOST_BIT,
            0,
            0,
            nullptr,
            1,
            &barrier,
            0,
            nullptr
        );

        result = vkEndCommandBuffer(command_buffer);
        if (result != VK_SUCCESS) {
            reason = "end_command_buffer_failed:" + vk_result_name(result);
            cleanup();
            trim_static_cache();
            return false;
        }

        VkFenceCreateInfo fence_info {};
        fence_info.sType = VK_STRUCTURE_TYPE_FENCE_CREATE_INFO;
        result = vkCreateFence(device_, &fence_info, nullptr, &fence);
        if (result != VK_SUCCESS) {
            reason = "create_fence_failed:" + vk_result_name(result);
            cleanup();
            trim_static_cache();
            return false;
        }

        VkSubmitInfo submit_info {};
        submit_info.sType = VK_STRUCTURE_TYPE_SUBMIT_INFO;
        submit_info.commandBufferCount = 1;
        submit_info.pCommandBuffers = &command_buffer;
        result = vkQueueSubmit(queue_, 1, &submit_info, fence);
        if (result != VK_SUCCESS) {
            reason = "queue_submit_failed:" + vk_result_name(result);
            disable_after_device_loss(result, reason);
            cleanup();
            trim_static_cache();
            return false;
        }

        result = vkWaitForFences(device_, 1, &fence, VK_TRUE, 30'000'000'000ULL);
        if (result != VK_SUCCESS) {
            reason = "wait_fence_failed:" + vk_result_name(result);
            disable_after_device_loss(result, reason);
            if (result != VK_ERROR_DEVICE_LOST) {
                vkDeviceWaitIdle(device_);
            }
            cleanup();
            trim_static_cache();
            return false;
        }

        ok = read_buffer(output_buffer, output, static_cast<size_t>(output_bytes), reason);
        cleanup();
        trim_static_cache();
        return ok;
    }

    void disable_after_device_loss(VkResult result, const std::string& reason) {
        if (result != VK_ERROR_DEVICE_LOST) {
            return;
        }
        disabled_after_device_loss_.store(true);
        bool expected = false;
        if (g_logged_runtime_disabled.compare_exchange_strong(expected, true)) {
            __android_log_print(
                ANDROID_LOG_INFO,
                LOG_TAG,
                "phase=vulkan_runtime_disabled reason=%s",
                reason.c_str()
            );
        }
    }

    std::once_flag init_once_;
    bool available_ = false;
    std::atomic_bool disabled_after_device_loss_ { false };
    std::string init_reason_ = "not_initialized";
    std::string last_candidate_rejection_;
    std::string device_name_ = "unknown";
    std::string api_version_ = "0.0.0";
    uint64_t max_storage_buffer_range_ = 0;
    uint32_t max_workgroup_count_x_ = 0;
    uint32_t max_workgroup_count_y_ = 0;
    uint32_t queue_family_index_ = 0;
    bool queue_family_compute_only_ = false;
    VkInstance instance_ = VK_NULL_HANDLE;
    VkPhysicalDevice physical_device_ = VK_NULL_HANDLE;
    VkDevice device_ = VK_NULL_HANDLE;
    VkQueue queue_ = VK_NULL_HANDLE;
    VkDescriptorSetLayout descriptor_set_layout_ = VK_NULL_HANDLE;
    VkPipelineLayout pipeline_layout_ = VK_NULL_HANDLE;
    VkShaderModule shader_module_ = VK_NULL_HANDLE;
    VkPipeline pipeline_ = VK_NULL_HANDLE;
    VkCommandPool command_pool_ = VK_NULL_HANDLE;
    std::vector<std::unique_ptr<BonsaiVulkanStaticBufferEntry>> static_cache_;
    uint64_t static_cache_bytes_ = 0;
    uint64_t static_cache_tick_ = 0;
    std::mutex queue_mutex_;
};

BonsaiVulkanRuntime& runtime() {
    static BonsaiVulkanRuntime value;
    return value;
}

void log_dispatch_success_once(const BonsaiPackedWeightViews& views, uint64_t token_count) {
    bool expected = false;
    if (g_logged_success.compare_exchange_strong(expected, true)) {
        __android_log_print(
            ANDROID_LOG_INFO,
            LOG_TAG,
            "phase=vulkan_matvec first_dispatch=true rows=%llu input_values=%llu tokens=%llu bits=%d group_size=%d",
            static_cast<unsigned long long>(views.leading_rows),
            static_cast<unsigned long long>(views.input_values),
            static_cast<unsigned long long>(token_count),
            views.bits,
            views.group_size
        );
    }
}

void log_fallback_once(const std::string& reason) {
    bool expected = false;
    if (g_logged_fallback.compare_exchange_strong(expected, true)) {
        __android_log_print(
            ANDROID_LOG_INFO,
            LOG_TAG,
            "phase=vulkan_matvec fallback=true reason=%s",
            reason.c_str()
        );
    }
}

} // namespace

void bonsai_vulkan_set_backend_mode(BonsaiVulkanBackendMode mode) {
    g_backend_mode.store(static_cast<int>(mode));
    __android_log_print(
        ANDROID_LOG_INFO,
        LOG_TAG,
        "phase=vulkan_backend mode=%s",
        backend_mode_name(mode)
    );
}

bool bonsai_vulkan_runtime_available() {
    const bool available = runtime().available();
    bool expected = false;
    if (g_logged_available.compare_exchange_strong(expected, true)) {
        __android_log_print(
            ANDROID_LOG_INFO,
            LOG_TAG,
            "phase=vulkan_runtime_checked available=%s",
            bool_value(available).c_str()
        );
    }
    return available;
}

bool bonsai_vulkan_quantized_matvec_into(
    const BonsaiPackedWeightViews& views,
    const float* input,
    float* output
) {
    return bonsai_vulkan_quantized_matvec_sequence_into(views, input, output, 1);
}

bool bonsai_vulkan_quantized_matvec_sequence_into(
    const BonsaiPackedWeightViews& views,
    const float* input,
    float* output,
    uint64_t token_count
) {
    std::string reason;
    const bool ok = runtime().quantized_matvec_sequence_into(
        views,
        input,
        output,
        token_count,
        reason
    );
    if (ok) {
        log_dispatch_success_once(views, token_count);
        return true;
    }
    if (!reason.empty() &&
        reason != "unsupported_weight_layout" &&
        reason != "unsupported_bits" &&
        reason != "invalid_shape") {
        log_fallback_once(reason);
    }
    return false;
}
