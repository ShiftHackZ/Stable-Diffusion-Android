#pragma once

#include <cstdint>
#include <string>
#include <vector>

enum class BonsaiDType {
    Bool,
    U8,
    I8,
    U16,
    I16,
    U32,
    I32,
    U64,
    I64,
    F16,
    BF16,
    F32,
    F64,
};

BonsaiDType bonsai_dtype_from_safetensors(const std::string& dtype);
std::string bonsai_dtype_name(BonsaiDType dtype);
bool bonsai_dtype_is_floating_point(BonsaiDType dtype);
uint64_t bonsai_dtype_byte_count(BonsaiDType dtype);
uint64_t bonsai_shape_element_count(
    const std::vector<uint64_t>& shape,
    const std::string& tensor_key
);
float bonsai_read_scalar_as_f32(const uint8_t* data, BonsaiDType dtype);
