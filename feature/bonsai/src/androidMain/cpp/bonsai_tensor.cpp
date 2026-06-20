#include "bonsai_tensor.h"

#include <cstring>
#include <limits>
#include <stdexcept>

namespace {

uint64_t checked_multiply(uint64_t left, uint64_t right, const std::string& tensor_key) {
    if (left != 0 && right > std::numeric_limits<uint64_t>::max() / left) {
        throw std::runtime_error("Bonsai tensor shape is too large: " + tensor_key);
    }
    return left * right;
}

float f16_to_f32(uint16_t value) {
    const uint32_t sign = (static_cast<uint32_t>(value & 0x8000U)) << 16U;
    uint32_t exponent = (value >> 10U) & 0x1FU;
    uint32_t mantissa = value & 0x03FFU;

    uint32_t bits = 0;
    if (exponent == 0) {
        if (mantissa == 0) {
            bits = sign;
        } else {
            exponent = 1;
            while ((mantissa & 0x0400U) == 0) {
                mantissa <<= 1U;
                exponent--;
            }
            mantissa &= 0x03FFU;
            bits = sign | ((exponent + 112U) << 23U) | (mantissa << 13U);
        }
    } else if (exponent == 0x1FU) {
        bits = sign | 0x7F800000U | (mantissa << 13U);
    } else {
        bits = sign | ((exponent + 112U) << 23U) | (mantissa << 13U);
    }

    float output = 0.0F;
    std::memcpy(&output, &bits, sizeof(output));
    return output;
}

float bf16_to_f32(uint16_t value) {
    const uint32_t bits = static_cast<uint32_t>(value) << 16U;
    float output = 0.0F;
    std::memcpy(&output, &bits, sizeof(output));
    return output;
}

template <typename T>
T read_unaligned(const uint8_t* data) {
    T value {};
    std::memcpy(&value, data, sizeof(T));
    return value;
}

} // namespace

BonsaiDType bonsai_dtype_from_safetensors(const std::string& dtype) {
    if (dtype == "BOOL") return BonsaiDType::Bool;
    if (dtype == "U8") return BonsaiDType::U8;
    if (dtype == "I8") return BonsaiDType::I8;
    if (dtype == "U16") return BonsaiDType::U16;
    if (dtype == "I16") return BonsaiDType::I16;
    if (dtype == "U32") return BonsaiDType::U32;
    if (dtype == "I32") return BonsaiDType::I32;
    if (dtype == "U64") return BonsaiDType::U64;
    if (dtype == "I64") return BonsaiDType::I64;
    if (dtype == "F16") return BonsaiDType::F16;
    if (dtype == "BF16") return BonsaiDType::BF16;
    if (dtype == "F32") return BonsaiDType::F32;
    if (dtype == "F64") return BonsaiDType::F64;
    throw std::runtime_error("unsupported Bonsai tensor dtype: " + dtype);
}

std::string bonsai_dtype_name(BonsaiDType dtype) {
    switch (dtype) {
        case BonsaiDType::Bool: return "BOOL";
        case BonsaiDType::U8: return "U8";
        case BonsaiDType::I8: return "I8";
        case BonsaiDType::U16: return "U16";
        case BonsaiDType::I16: return "I16";
        case BonsaiDType::U32: return "U32";
        case BonsaiDType::I32: return "I32";
        case BonsaiDType::U64: return "U64";
        case BonsaiDType::I64: return "I64";
        case BonsaiDType::F16: return "F16";
        case BonsaiDType::BF16: return "BF16";
        case BonsaiDType::F32: return "F32";
        case BonsaiDType::F64: return "F64";
    }
}

bool bonsai_dtype_is_floating_point(BonsaiDType dtype) {
    return dtype == BonsaiDType::F16 ||
        dtype == BonsaiDType::BF16 ||
        dtype == BonsaiDType::F32 ||
        dtype == BonsaiDType::F64;
}

uint64_t bonsai_dtype_byte_count(BonsaiDType dtype) {
    switch (dtype) {
        case BonsaiDType::Bool:
        case BonsaiDType::U8:
        case BonsaiDType::I8:
            return 1;
        case BonsaiDType::U16:
        case BonsaiDType::I16:
        case BonsaiDType::F16:
        case BonsaiDType::BF16:
            return 2;
        case BonsaiDType::U32:
        case BonsaiDType::I32:
        case BonsaiDType::F32:
            return 4;
        case BonsaiDType::U64:
        case BonsaiDType::I64:
        case BonsaiDType::F64:
            return 8;
    }
}

uint64_t bonsai_shape_element_count(
    const std::vector<uint64_t>& shape,
    const std::string& tensor_key
) {
    uint64_t count = 1;
    for (uint64_t dimension : shape) {
        count = checked_multiply(count, dimension, tensor_key);
    }
    return count;
}

float bonsai_read_scalar_as_f32(const uint8_t* data, BonsaiDType dtype) {
    switch (dtype) {
        case BonsaiDType::Bool:
            return *data == 0 ? 0.0F : 1.0F;
        case BonsaiDType::U8:
            return static_cast<float>(read_unaligned<uint8_t>(data));
        case BonsaiDType::I8:
            return static_cast<float>(read_unaligned<int8_t>(data));
        case BonsaiDType::U16:
            return static_cast<float>(read_unaligned<uint16_t>(data));
        case BonsaiDType::I16:
            return static_cast<float>(read_unaligned<int16_t>(data));
        case BonsaiDType::U32:
            return static_cast<float>(read_unaligned<uint32_t>(data));
        case BonsaiDType::I32:
            return static_cast<float>(read_unaligned<int32_t>(data));
        case BonsaiDType::U64:
            return static_cast<float>(read_unaligned<uint64_t>(data));
        case BonsaiDType::I64:
            return static_cast<float>(read_unaligned<int64_t>(data));
        case BonsaiDType::F16:
            return f16_to_f32(read_unaligned<uint16_t>(data));
        case BonsaiDType::BF16:
            return bf16_to_f32(read_unaligned<uint16_t>(data));
        case BonsaiDType::F32:
            return read_unaligned<float>(data);
        case BonsaiDType::F64:
            return static_cast<float>(read_unaligned<double>(data));
    }
}
