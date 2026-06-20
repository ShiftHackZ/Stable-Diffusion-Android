#include <jni.h>

#include "benchmark_vulkan_smoke_spv.h"

#include <android/log.h>
#include <vulkan/vulkan.h>

#include <algorithm>
#include <chrono>
#include <cstdint>
#include <cmath>
#include <cstring>
#include <sstream>
#include <string>
#include <vector>

namespace {

constexpr const char* LOG_TAG = "SDAI-Benchmark";
constexpr uint64_t BYTES_IN_MB = 1024ULL * 1024ULL;
constexpr uint32_t MIN_STORAGE_BUFFER_RANGE = 64U * 1024U * 1024U;
constexpr uint32_t SMOKE_VALUE_COUNT = 256U;
constexpr uint32_t SMOKE_LOCAL_SIZE = 64U;

struct VulkanSmokeResult {
    bool ok = false;
    double elapsed_ms = 0.0;
    std::string reason;
};

struct VulkanSmokeBuffer {
    VkBuffer buffer = VK_NULL_HANDLE;
    VkDeviceMemory memory = VK_NULL_HANDLE;
    VkDeviceSize size = 0;
    bool coherent = false;
};

struct VulkanSmokeResources {
    VkDevice device = VK_NULL_HANDLE;
    VkDescriptorSetLayout descriptor_set_layout = VK_NULL_HANDLE;
    VkPipelineLayout pipeline_layout = VK_NULL_HANDLE;
    VkShaderModule shader_module = VK_NULL_HANDLE;
    VkPipeline pipeline = VK_NULL_HANDLE;
    VkDescriptorPool descriptor_pool = VK_NULL_HANDLE;
    VkCommandPool command_pool = VK_NULL_HANDLE;
    VkFence fence = VK_NULL_HANDLE;
    VulkanSmokeBuffer input;
    VulkanSmokeBuffer output;

    ~VulkanSmokeResources() {
        if (device == VK_NULL_HANDLE) {
            return;
        }
        if (fence != VK_NULL_HANDLE) {
            vkDestroyFence(device, fence, nullptr);
        }
        if (command_pool != VK_NULL_HANDLE) {
            vkDestroyCommandPool(device, command_pool, nullptr);
        }
        if (descriptor_pool != VK_NULL_HANDLE) {
            vkDestroyDescriptorPool(device, descriptor_pool, nullptr);
        }
        if (pipeline != VK_NULL_HANDLE) {
            vkDestroyPipeline(device, pipeline, nullptr);
        }
        if (shader_module != VK_NULL_HANDLE) {
            vkDestroyShaderModule(device, shader_module, nullptr);
        }
        if (pipeline_layout != VK_NULL_HANDLE) {
            vkDestroyPipelineLayout(device, pipeline_layout, nullptr);
        }
        if (descriptor_set_layout != VK_NULL_HANDLE) {
            vkDestroyDescriptorSetLayout(device, descriptor_set_layout, nullptr);
        }
        if (input.buffer != VK_NULL_HANDLE) {
            vkDestroyBuffer(device, input.buffer, nullptr);
        }
        if (input.memory != VK_NULL_HANDLE) {
            vkFreeMemory(device, input.memory, nullptr);
        }
        if (output.buffer != VK_NULL_HANDLE) {
            vkDestroyBuffer(device, output.buffer, nullptr);
        }
        if (output.memory != VK_NULL_HANDLE) {
            vkFreeMemory(device, output.memory, nullptr);
        }
        vkDestroyDevice(device, nullptr);
    }
};

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
        case VK_NOT_READY:
            return "VK_NOT_READY";
        case VK_TIMEOUT:
            return "VK_TIMEOUT";
        case VK_EVENT_SET:
            return "VK_EVENT_SET";
        case VK_EVENT_RESET:
            return "VK_EVENT_RESET";
        case VK_INCOMPLETE:
            return "VK_INCOMPLETE";
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
        case VK_ERROR_LAYER_NOT_PRESENT:
            return "VK_ERROR_LAYER_NOT_PRESENT";
        case VK_ERROR_EXTENSION_NOT_PRESENT:
            return "VK_ERROR_EXTENSION_NOT_PRESENT";
        case VK_ERROR_FEATURE_NOT_PRESENT:
            return "VK_ERROR_FEATURE_NOT_PRESENT";
        case VK_ERROR_INCOMPATIBLE_DRIVER:
            return "VK_ERROR_INCOMPATIBLE_DRIVER";
        case VK_ERROR_TOO_MANY_OBJECTS:
            return "VK_ERROR_TOO_MANY_OBJECTS";
        case VK_ERROR_FORMAT_NOT_SUPPORTED:
            return "VK_ERROR_FORMAT_NOT_SUPPORTED";
        case VK_ERROR_FRAGMENTED_POOL:
            return "VK_ERROR_FRAGMENTED_POOL";
        default:
            return "VK_RESULT_" + std::to_string(static_cast<int>(result));
    }
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

bool find_compute_queue_family(VkPhysicalDevice physical_device, uint32_t& queue_family_index) {
    uint32_t queue_count = 0;
    vkGetPhysicalDeviceQueueFamilyProperties(physical_device, &queue_count, nullptr);
    if (queue_count == 0) {
        return false;
    }

    std::vector<VkQueueFamilyProperties> queues(queue_count);
    vkGetPhysicalDeviceQueueFamilyProperties(physical_device, &queue_count, queues.data());
    for (uint32_t index = 0; index < queue_count; ++index) {
        const VkQueueFamilyProperties& queue = queues[index];
        if ((queue.queueFlags & VK_QUEUE_COMPUTE_BIT) != 0 && queue.queueCount > 0) {
            queue_family_index = index;
            return true;
        }
    }
    return false;
}

uint64_t device_local_heap_mb(VkPhysicalDevice physical_device) {
    VkPhysicalDeviceMemoryProperties memory_properties {};
    vkGetPhysicalDeviceMemoryProperties(physical_device, &memory_properties);
    uint64_t total = 0;
    for (uint32_t index = 0; index < memory_properties.memoryHeapCount; ++index) {
        const VkMemoryHeap& heap = memory_properties.memoryHeaps[index];
        if ((heap.flags & VK_MEMORY_HEAP_DEVICE_LOCAL_BIT) != 0) {
            total += heap.size;
        }
    }
    return total / BYTES_IN_MB;
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

VulkanSmokeResult smoke_error(const std::string& reason) {
    return VulkanSmokeResult { false, 0.0, reason };
}

bool create_host_storage_buffer(
    VkPhysicalDevice physical_device,
    VulkanSmokeResources& resources,
    VkDeviceSize size,
    VulkanSmokeBuffer& output,
    std::string& reason
) {
    output.size = size;

    VkBufferCreateInfo buffer_info {};
    buffer_info.sType = VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO;
    buffer_info.size = size;
    buffer_info.usage = VK_BUFFER_USAGE_STORAGE_BUFFER_BIT;
    buffer_info.sharingMode = VK_SHARING_MODE_EXCLUSIVE;

    VkResult result = vkCreateBuffer(resources.device, &buffer_info, nullptr, &output.buffer);
    if (result != VK_SUCCESS) {
        reason = "create_buffer_failed:" + vk_result_name(result);
        return false;
    }

    VkMemoryRequirements requirements {};
    vkGetBufferMemoryRequirements(resources.device, output.buffer, &requirements);
    int32_t memory_type = find_memory_type(
        physical_device,
        requirements.memoryTypeBits,
        VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT
    );
    output.coherent = memory_type >= 0;
    if (memory_type < 0) {
        memory_type = find_memory_type(
            physical_device,
            requirements.memoryTypeBits,
            VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT
        );
    }
    if (memory_type < 0) {
        reason = "missing_host_visible_storage_memory";
        return false;
    }

    VkMemoryAllocateInfo allocate_info {};
    allocate_info.sType = VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO;
    allocate_info.allocationSize = requirements.size;
    allocate_info.memoryTypeIndex = static_cast<uint32_t>(memory_type);
    result = vkAllocateMemory(resources.device, &allocate_info, nullptr, &output.memory);
    if (result != VK_SUCCESS) {
        reason = "allocate_memory_failed:" + vk_result_name(result);
        return false;
    }

    result = vkBindBufferMemory(resources.device, output.buffer, output.memory, 0);
    if (result != VK_SUCCESS) {
        reason = "bind_buffer_failed:" + vk_result_name(result);
        return false;
    }
    return true;
}

bool write_buffer(
    const VulkanSmokeResources& resources,
    const VulkanSmokeBuffer& buffer,
    const void* source,
    size_t byte_count,
    std::string& reason
) {
    void* mapped = nullptr;
    VkResult result = vkMapMemory(resources.device, buffer.memory, 0, byte_count, 0, &mapped);
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
        result = vkFlushMappedMemoryRanges(resources.device, 1, &range);
        if (result != VK_SUCCESS) {
            vkUnmapMemory(resources.device, buffer.memory);
            reason = "flush_write_failed:" + vk_result_name(result);
            return false;
        }
    }
    vkUnmapMemory(resources.device, buffer.memory);
    return true;
}

bool read_buffer(
    const VulkanSmokeResources& resources,
    const VulkanSmokeBuffer& buffer,
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
        VkResult result = vkInvalidateMappedMemoryRanges(resources.device, 1, &range);
        if (result != VK_SUCCESS) {
            reason = "invalidate_read_failed:" + vk_result_name(result);
            return false;
        }
    }

    void* mapped = nullptr;
    VkResult result = vkMapMemory(resources.device, buffer.memory, 0, byte_count, 0, &mapped);
    if (result != VK_SUCCESS) {
        reason = "map_read_failed:" + vk_result_name(result);
        return false;
    }
    std::memcpy(destination, mapped, byte_count);
    vkUnmapMemory(resources.device, buffer.memory);
    return true;
}

bool create_smoke_pipeline(VulkanSmokeResources& resources, std::string& reason) {
    VkDescriptorSetLayoutBinding bindings[2] {};
    bindings[0].binding = 0;
    bindings[0].descriptorType = VK_DESCRIPTOR_TYPE_STORAGE_BUFFER;
    bindings[0].descriptorCount = 1;
    bindings[0].stageFlags = VK_SHADER_STAGE_COMPUTE_BIT;
    bindings[1].binding = 1;
    bindings[1].descriptorType = VK_DESCRIPTOR_TYPE_STORAGE_BUFFER;
    bindings[1].descriptorCount = 1;
    bindings[1].stageFlags = VK_SHADER_STAGE_COMPUTE_BIT;

    VkDescriptorSetLayoutCreateInfo layout_info {};
    layout_info.sType = VK_STRUCTURE_TYPE_DESCRIPTOR_SET_LAYOUT_CREATE_INFO;
    layout_info.bindingCount = 2;
    layout_info.pBindings = bindings;
    VkResult result = vkCreateDescriptorSetLayout(
        resources.device,
        &layout_info,
        nullptr,
        &resources.descriptor_set_layout
    );
    if (result != VK_SUCCESS) {
        reason = "create_descriptor_set_layout_failed:" + vk_result_name(result);
        return false;
    }

    VkPushConstantRange push_constant {};
    push_constant.stageFlags = VK_SHADER_STAGE_COMPUTE_BIT;
    push_constant.offset = 0;
    push_constant.size = sizeof(uint32_t);

    VkPipelineLayoutCreateInfo pipeline_layout_info {};
    pipeline_layout_info.sType = VK_STRUCTURE_TYPE_PIPELINE_LAYOUT_CREATE_INFO;
    pipeline_layout_info.setLayoutCount = 1;
    pipeline_layout_info.pSetLayouts = &resources.descriptor_set_layout;
    pipeline_layout_info.pushConstantRangeCount = 1;
    pipeline_layout_info.pPushConstantRanges = &push_constant;
    result = vkCreatePipelineLayout(
        resources.device,
        &pipeline_layout_info,
        nullptr,
        &resources.pipeline_layout
    );
    if (result != VK_SUCCESS) {
        reason = "create_pipeline_layout_failed:" + vk_result_name(result);
        return false;
    }

    VkShaderModuleCreateInfo shader_info {};
    shader_info.sType = VK_STRUCTURE_TYPE_SHADER_MODULE_CREATE_INFO;
    shader_info.codeSize = kBenchmarkVulkanSmokeSpvSize;
    shader_info.pCode = reinterpret_cast<const uint32_t*>(kBenchmarkVulkanSmokeSpv);
    result = vkCreateShaderModule(
        resources.device,
        &shader_info,
        nullptr,
        &resources.shader_module
    );
    if (result != VK_SUCCESS) {
        reason = "create_shader_module_failed:" + vk_result_name(result);
        return false;
    }

    VkComputePipelineCreateInfo pipeline_info {};
    pipeline_info.sType = VK_STRUCTURE_TYPE_COMPUTE_PIPELINE_CREATE_INFO;
    pipeline_info.stage.sType = VK_STRUCTURE_TYPE_PIPELINE_SHADER_STAGE_CREATE_INFO;
    pipeline_info.stage.stage = VK_SHADER_STAGE_COMPUTE_BIT;
    pipeline_info.stage.module = resources.shader_module;
    pipeline_info.stage.pName = "main";
    pipeline_info.layout = resources.pipeline_layout;
    result = vkCreateComputePipelines(
        resources.device,
        VK_NULL_HANDLE,
        1,
        &pipeline_info,
        nullptr,
        &resources.pipeline
    );
    if (result != VK_SUCCESS) {
        reason = "create_compute_pipeline_failed:" + vk_result_name(result);
        return false;
    }
    return true;
}

VulkanSmokeResult run_compute_smoke(
    VkPhysicalDevice physical_device,
    uint32_t queue_family_index
) {
    VulkanSmokeResources resources;

    const float priority = 1.0F;
    VkDeviceQueueCreateInfo queue_info {};
    queue_info.sType = VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO;
    queue_info.queueFamilyIndex = queue_family_index;
    queue_info.queueCount = 1;
    queue_info.pQueuePriorities = &priority;

    VkDeviceCreateInfo device_info {};
    device_info.sType = VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO;
    device_info.queueCreateInfoCount = 1;
    device_info.pQueueCreateInfos = &queue_info;

    VkResult result = vkCreateDevice(physical_device, &device_info, nullptr, &resources.device);
    if (result != VK_SUCCESS) {
        return smoke_error("create_device_failed:" + vk_result_name(result));
    }

    VkQueue queue = VK_NULL_HANDLE;
    vkGetDeviceQueue(resources.device, queue_family_index, 0, &queue);
    if (queue == VK_NULL_HANDLE) {
        return smoke_error("get_compute_queue_failed");
    }

    const VkDeviceSize byte_count = SMOKE_VALUE_COUNT * sizeof(float);
    std::string reason;
    if (!create_host_storage_buffer(physical_device, resources, byte_count, resources.input, reason) ||
        !create_host_storage_buffer(physical_device, resources, byte_count, resources.output, reason)) {
        return smoke_error(reason);
    }

    std::vector<float> input(SMOKE_VALUE_COUNT);
    std::vector<float> zeroes(SMOKE_VALUE_COUNT, 0.0F);
    for (uint32_t index = 0; index < SMOKE_VALUE_COUNT; ++index) {
        input[index] = static_cast<float>(index) * 0.25F - 7.0F;
    }
    if (!write_buffer(resources, resources.input, input.data(), static_cast<size_t>(byte_count), reason) ||
        !write_buffer(resources, resources.output, zeroes.data(), static_cast<size_t>(byte_count), reason)) {
        return smoke_error(reason);
    }

    if (!create_smoke_pipeline(resources, reason)) {
        return smoke_error(reason);
    }

    VkDescriptorPoolSize pool_size {};
    pool_size.type = VK_DESCRIPTOR_TYPE_STORAGE_BUFFER;
    pool_size.descriptorCount = 2;

    VkDescriptorPoolCreateInfo pool_info {};
    pool_info.sType = VK_STRUCTURE_TYPE_DESCRIPTOR_POOL_CREATE_INFO;
    pool_info.maxSets = 1;
    pool_info.poolSizeCount = 1;
    pool_info.pPoolSizes = &pool_size;
    result = vkCreateDescriptorPool(resources.device, &pool_info, nullptr, &resources.descriptor_pool);
    if (result != VK_SUCCESS) {
        return smoke_error("create_descriptor_pool_failed:" + vk_result_name(result));
    }

    VkDescriptorSet descriptor_set = VK_NULL_HANDLE;
    VkDescriptorSetAllocateInfo descriptor_allocate {};
    descriptor_allocate.sType = VK_STRUCTURE_TYPE_DESCRIPTOR_SET_ALLOCATE_INFO;
    descriptor_allocate.descriptorPool = resources.descriptor_pool;
    descriptor_allocate.descriptorSetCount = 1;
    descriptor_allocate.pSetLayouts = &resources.descriptor_set_layout;
    result = vkAllocateDescriptorSets(resources.device, &descriptor_allocate, &descriptor_set);
    if (result != VK_SUCCESS) {
        return smoke_error("allocate_descriptor_set_failed:" + vk_result_name(result));
    }

    VkDescriptorBufferInfo input_info {};
    input_info.buffer = resources.input.buffer;
    input_info.offset = 0;
    input_info.range = byte_count;
    VkDescriptorBufferInfo output_info {};
    output_info.buffer = resources.output.buffer;
    output_info.offset = 0;
    output_info.range = byte_count;

    VkWriteDescriptorSet descriptor_writes[2] {};
    descriptor_writes[0].sType = VK_STRUCTURE_TYPE_WRITE_DESCRIPTOR_SET;
    descriptor_writes[0].dstSet = descriptor_set;
    descriptor_writes[0].dstBinding = 0;
    descriptor_writes[0].descriptorCount = 1;
    descriptor_writes[0].descriptorType = VK_DESCRIPTOR_TYPE_STORAGE_BUFFER;
    descriptor_writes[0].pBufferInfo = &input_info;
    descriptor_writes[1].sType = VK_STRUCTURE_TYPE_WRITE_DESCRIPTOR_SET;
    descriptor_writes[1].dstSet = descriptor_set;
    descriptor_writes[1].dstBinding = 1;
    descriptor_writes[1].descriptorCount = 1;
    descriptor_writes[1].descriptorType = VK_DESCRIPTOR_TYPE_STORAGE_BUFFER;
    descriptor_writes[1].pBufferInfo = &output_info;
    vkUpdateDescriptorSets(resources.device, 2, descriptor_writes, 0, nullptr);

    VkCommandPoolCreateInfo command_pool_info {};
    command_pool_info.sType = VK_STRUCTURE_TYPE_COMMAND_POOL_CREATE_INFO;
    command_pool_info.queueFamilyIndex = queue_family_index;
    result = vkCreateCommandPool(resources.device, &command_pool_info, nullptr, &resources.command_pool);
    if (result != VK_SUCCESS) {
        return smoke_error("create_command_pool_failed:" + vk_result_name(result));
    }

    VkCommandBuffer command_buffer = VK_NULL_HANDLE;
    VkCommandBufferAllocateInfo command_allocate {};
    command_allocate.sType = VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO;
    command_allocate.commandPool = resources.command_pool;
    command_allocate.level = VK_COMMAND_BUFFER_LEVEL_PRIMARY;
    command_allocate.commandBufferCount = 1;
    result = vkAllocateCommandBuffers(resources.device, &command_allocate, &command_buffer);
    if (result != VK_SUCCESS) {
        return smoke_error("allocate_command_buffer_failed:" + vk_result_name(result));
    }

    VkCommandBufferBeginInfo begin_info {};
    begin_info.sType = VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO;
    begin_info.flags = VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT;
    result = vkBeginCommandBuffer(command_buffer, &begin_info);
    if (result != VK_SUCCESS) {
        return smoke_error("begin_command_buffer_failed:" + vk_result_name(result));
    }

    vkCmdBindPipeline(command_buffer, VK_PIPELINE_BIND_POINT_COMPUTE, resources.pipeline);
    vkCmdBindDescriptorSets(
        command_buffer,
        VK_PIPELINE_BIND_POINT_COMPUTE,
        resources.pipeline_layout,
        0,
        1,
        &descriptor_set,
        0,
        nullptr
    );
    const uint32_t value_count = SMOKE_VALUE_COUNT;
    vkCmdPushConstants(
        command_buffer,
        resources.pipeline_layout,
        VK_SHADER_STAGE_COMPUTE_BIT,
        0,
        sizeof(uint32_t),
        &value_count
    );
    vkCmdDispatch(
        command_buffer,
        (SMOKE_VALUE_COUNT + SMOKE_LOCAL_SIZE - 1U) / SMOKE_LOCAL_SIZE,
        1,
        1
    );

    VkBufferMemoryBarrier barrier {};
    barrier.sType = VK_STRUCTURE_TYPE_BUFFER_MEMORY_BARRIER;
    barrier.srcAccessMask = VK_ACCESS_SHADER_WRITE_BIT;
    barrier.dstAccessMask = VK_ACCESS_HOST_READ_BIT;
    barrier.srcQueueFamilyIndex = VK_QUEUE_FAMILY_IGNORED;
    barrier.dstQueueFamilyIndex = VK_QUEUE_FAMILY_IGNORED;
    barrier.buffer = resources.output.buffer;
    barrier.offset = 0;
    barrier.size = byte_count;
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
        return smoke_error("end_command_buffer_failed:" + vk_result_name(result));
    }

    VkFenceCreateInfo fence_info {};
    fence_info.sType = VK_STRUCTURE_TYPE_FENCE_CREATE_INFO;
    result = vkCreateFence(resources.device, &fence_info, nullptr, &resources.fence);
    if (result != VK_SUCCESS) {
        return smoke_error("create_fence_failed:" + vk_result_name(result));
    }

    VkSubmitInfo submit_info {};
    submit_info.sType = VK_STRUCTURE_TYPE_SUBMIT_INFO;
    submit_info.commandBufferCount = 1;
    submit_info.pCommandBuffers = &command_buffer;
    const auto start = std::chrono::steady_clock::now();
    result = vkQueueSubmit(queue, 1, &submit_info, resources.fence);
    if (result != VK_SUCCESS) {
        return smoke_error("queue_submit_failed:" + vk_result_name(result));
    }
    result = vkWaitForFences(resources.device, 1, &resources.fence, VK_TRUE, 5'000'000'000ULL);
    const auto end = std::chrono::steady_clock::now();
    if (result != VK_SUCCESS) {
        return smoke_error("wait_fence_failed:" + vk_result_name(result));
    }

    std::vector<float> output(SMOKE_VALUE_COUNT);
    if (!read_buffer(resources, resources.output, output.data(), static_cast<size_t>(byte_count), reason)) {
        return smoke_error(reason);
    }

    for (uint32_t index = 0; index < SMOKE_VALUE_COUNT; ++index) {
        const float expected = input[index] * 2.0F + 1.0F;
        if (std::fabs(output[index] - expected) > 0.0001F) {
            std::ostringstream mismatch;
            mismatch << "compute_mismatch:index=" << index
                << ",expected=" << expected
                << ",actual=" << output[index];
            return smoke_error(mismatch.str());
        }
    }

    const double elapsed_ms =
        std::chrono::duration<double, std::milli>(end - start).count();
    return VulkanSmokeResult { true, elapsed_ms, "ok" };
}

std::string probe_physical_device(VkPhysicalDevice physical_device) {
    VkPhysicalDeviceProperties properties {};
    vkGetPhysicalDeviceProperties(physical_device, &properties);

    VkPhysicalDeviceFeatures features {};
    vkGetPhysicalDeviceFeatures(physical_device, &features);

    const bool api_1_1 = properties.apiVersion >= VK_API_VERSION_1_1;
    uint32_t queue_family_index = 0;
    const bool compute_queue = find_compute_queue_family(physical_device, queue_family_index);
    const bool storage_range_ok =
        properties.limits.maxStorageBufferRange >= MIN_STORAGE_BUFFER_RANGE;
    const bool baseline_usable = api_1_1 && compute_queue && storage_range_ok;
    const VulkanSmokeResult smoke_result = baseline_usable
        ? run_compute_smoke(physical_device, queue_family_index)
        : VulkanSmokeResult {};
    const bool usable = baseline_usable && smoke_result.ok;

    std::ostringstream output;
    output << "usable=" << bool_value(usable)
        << ";apiDetected=true"
        << ";device=" << properties.deviceName
        << ";api=" << version_string(properties.apiVersion)
        << ";driver=" << version_string(properties.driverVersion)
        << ";computeQueue=" << bool_value(compute_queue)
        << ";maxStorageBufferRangeMb="
        << (properties.limits.maxStorageBufferRange / BYTES_IN_MB)
        << ";maxComputeWorkGroupInvocations="
        << properties.limits.maxComputeWorkGroupInvocations
        << ";maxComputeWorkGroupSize="
        << properties.limits.maxComputeWorkGroupSize[0] << "x"
        << properties.limits.maxComputeWorkGroupSize[1] << "x"
        << properties.limits.maxComputeWorkGroupSize[2]
        << ";maxPushConstantsSize=" << properties.limits.maxPushConstantsSize
        << ";deviceLocalHeapMb=" << device_local_heap_mb(physical_device)
        << ";shaderInt16=" << bool_value(features.shaderInt16)
        << ";computeSmoke=" << bool_value(smoke_result.ok)
        << ";computeSmokeMs=" << smoke_result.elapsed_ms
        << ";reason=";

    if (usable) {
        output << smoke_result.reason;
    } else if (!api_1_1) {
        output << "requires_vulkan_1_1";
    } else if (!compute_queue) {
        output << "missing_compute_queue";
    } else if (!storage_range_ok) {
        output << "small_storage_buffer_range";
    } else if (!smoke_result.ok) {
        output << smoke_result.reason;
    } else {
        output << "unknown";
    }
    return output.str();
}

std::string probe_vulkan() {
    const uint32_t loader_version = loader_api_version();
    VkApplicationInfo app_info {};
    app_info.sType = VK_STRUCTURE_TYPE_APPLICATION_INFO;
    app_info.pApplicationName = "SDAI Bonsai Vulkan Probe";
    app_info.applicationVersion = 1;
    app_info.pEngineName = "SDAI";
    app_info.engineVersion = 1;
    app_info.apiVersion = loader_version >= VK_API_VERSION_1_1
        ? VK_API_VERSION_1_1
        : VK_API_VERSION_1_0;

    VkInstanceCreateInfo instance_info {};
    instance_info.sType = VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO;
    instance_info.pApplicationInfo = &app_info;

    VkInstance instance = VK_NULL_HANDLE;
    const VkResult create_result = vkCreateInstance(&instance_info, nullptr, &instance);
    if (create_result != VK_SUCCESS) {
        std::ostringstream output;
        output << "usable=false;apiDetected=false;loaderApi="
            << version_string(loader_version)
            << ";reason=create_instance_failed:"
            << vk_result_name(create_result);
        return output.str();
    }

    uint32_t device_count = 0;
    VkResult enumerate_result =
        vkEnumeratePhysicalDevices(instance, &device_count, nullptr);
    if (enumerate_result != VK_SUCCESS || device_count == 0) {
        vkDestroyInstance(instance, nullptr);
        std::ostringstream output;
        output << "usable=false;apiDetected=false;loaderApi="
            << version_string(loader_version)
            << ";reason=no_physical_devices:"
            << vk_result_name(enumerate_result);
        return output.str();
    }

    std::vector<VkPhysicalDevice> devices(device_count);
    enumerate_result =
        vkEnumeratePhysicalDevices(instance, &device_count, devices.data());
    if (enumerate_result != VK_SUCCESS) {
        vkDestroyInstance(instance, nullptr);
        std::ostringstream output;
        output << "usable=false;apiDetected=false;loaderApi="
            << version_string(loader_version)
            << ";reason=enumerate_devices_failed:"
            << vk_result_name(enumerate_result);
        return output.str();
    }

    std::string best_summary;
    for (VkPhysicalDevice device : devices) {
        const std::string summary = probe_physical_device(device);
        if (best_summary.empty() ||
            summary.find("usable=true") != std::string::npos) {
            best_summary = summary;
        }
        if (summary.find("usable=true") != std::string::npos) {
            break;
        }
    }

    vkDestroyInstance(instance, nullptr);
    return best_summary.empty()
        ? "usable=false;apiDetected=false;reason=no_probe_result"
        : best_summary;
}

} // namespace

extern "C" JNIEXPORT jstring JNICALL
Java_com_shifthackz_aisdv1_feature_benchmark_AndroidBenchmarkVulkanProbe_probeVulkan(
    JNIEnv* env,
    jobject
) {
    const std::string summary = probe_vulkan();
    __android_log_print(ANDROID_LOG_INFO, LOG_TAG, "bonsai_vulkan_probe %s", summary.c_str());
    return env->NewStringUTF(summary.c_str());
}
