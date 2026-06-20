#include "bonsai_image_encoder.h"

#include <algorithm>
#include <cmath>
#include <cstdint>
#include <limits>
#include <stdexcept>
#include <string>
#include <vector>
#include <zlib.h>

namespace {

constexpr uint8_t PNG_SIGNATURE[] = {137, 80, 78, 71, 13, 10, 26, 10};
constexpr char BASE64_ALPHABET[] =
    "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

struct OutputStats {
    float min_value = std::numeric_limits<float>::max();
    float max_value = -std::numeric_limits<float>::max();
    double sum = 0.0;
    uint64_t finite_count = 0;
    uint64_t non_finite_count = 0;
};

uint64_t checked_multiply(uint64_t left, uint64_t right, const char* label) {
    if (left != 0 && right > std::numeric_limits<uint64_t>::max() / left) {
        throw std::runtime_error(std::string("Bonsai image encoder shape overflow: ") + label);
    }
    return left * right;
}

size_t checked_size(uint64_t value, const char* label) {
    if (value > static_cast<uint64_t>(std::numeric_limits<size_t>::max())) {
        throw std::runtime_error(std::string("Bonsai image encoder shape overflow: ") + label);
    }
    return static_cast<size_t>(value);
}

void append_u32_be(std::vector<uint8_t>* output, uint32_t value) {
    output->push_back(static_cast<uint8_t>((value >> 24U) & 0xFFU));
    output->push_back(static_cast<uint8_t>((value >> 16U) & 0xFFU));
    output->push_back(static_cast<uint8_t>((value >> 8U) & 0xFFU));
    output->push_back(static_cast<uint8_t>(value & 0xFFU));
}

void append_chunk(
    std::vector<uint8_t>* output,
    const char type[4],
    const std::vector<uint8_t>& data
) {
    if (data.size() > static_cast<size_t>(std::numeric_limits<uint32_t>::max())) {
        throw std::runtime_error("Bonsai PNG chunk is too large.");
    }
    append_u32_be(output, static_cast<uint32_t>(data.size()));
    const size_t type_offset = output->size();
    output->insert(output->end(), type, type + 4);
    output->insert(output->end(), data.begin(), data.end());

    uLong crc = crc32(0L, Z_NULL, 0);
    crc = crc32(
        crc,
        reinterpret_cast<const Bytef*>(output->data() + type_offset),
        static_cast<uInt>(4 + data.size())
    );
    append_u32_be(output, static_cast<uint32_t>(crc));
}

uint8_t to_byte(float value) {
    const float scaled = std::round(value * 255.0F);
    const int integer = static_cast<int>(scaled);
    return static_cast<uint8_t>(std::clamp(integer, 0, 255));
}

float normalized_pixel(const BonsaiNchwTensor& decoded, uint64_t y, uint64_t x, uint64_t channel) {
    const uint64_t index = (
        (channel * decoded.height + y) * decoded.width + x
    );
    return std::clamp(
        decoded.values[checked_size(index, "pixel index")] / 2.0F + 0.5F,
        0.0F,
        1.0F
    );
}

void record(OutputStats* stats, float value) {
    if (!std::isfinite(value)) {
        stats->non_finite_count++;
        return;
    }
    stats->min_value = std::min(stats->min_value, value);
    stats->max_value = std::max(stats->max_value, value);
    stats->sum += static_cast<double>(value);
    stats->finite_count++;
}

void validate_stats(const OutputStats& stats) {
    if (stats.finite_count == 0) {
        throw std::runtime_error("Bonsai output image is invalid: all RGB values are non-finite.");
    }
    if (stats.non_finite_count != 0) {
        throw std::runtime_error("Bonsai output image is invalid: non-finite RGB values.");
    }
    const double mean = stats.sum / static_cast<double>(stats.finite_count);
    if (mean <= 0.02 || (stats.max_value <= 0.08F && mean <= 0.03)) {
        throw std::runtime_error("Bonsai output image is invalid: nearly black output.");
    }
}

std::vector<uint8_t> rgb_scanlines(const BonsaiNchwTensor& decoded) {
    if (decoded.batch_size != 1 || decoded.channels < 3 || decoded.height == 0 || decoded.width == 0) {
        throw std::runtime_error("Bonsai decoded image tensor shape is unsupported.");
    }
    const uint64_t expected = checked_multiply(
        checked_multiply(
            checked_multiply(decoded.batch_size, decoded.channels, "decoded tensor"),
            decoded.height,
            "decoded tensor"
        ),
        decoded.width,
        "decoded tensor"
    );
    if (decoded.values.size() != checked_size(expected, "decoded tensor")) {
        throw std::runtime_error("Bonsai decoded image tensor value count mismatch.");
    }

    const uint64_t row_bytes = checked_multiply(decoded.width, 3U, "png row");
    const uint64_t scanline_bytes = checked_multiply(
        checked_multiply(decoded.height, row_bytes + 1U, "png scanlines"),
        1U,
        "png scanlines"
    );
    std::vector<uint8_t> output;
    output.reserve(checked_size(scanline_bytes, "png scanlines"));

    OutputStats stats;
    for (uint64_t y = 0; y < decoded.height; y++) {
        output.push_back(0);
        for (uint64_t x = 0; x < decoded.width; x++) {
            const float red = normalized_pixel(decoded, y, x, 0);
            const float green = normalized_pixel(decoded, y, x, 1);
            const float blue = normalized_pixel(decoded, y, x, 2);
            record(&stats, red);
            record(&stats, green);
            record(&stats, blue);
            output.push_back(std::isfinite(red) ? to_byte(red) : 0);
            output.push_back(std::isfinite(green) ? to_byte(green) : 0);
            output.push_back(std::isfinite(blue) ? to_byte(blue) : 0);
        }
    }
    validate_stats(stats);
    return output;
}

std::vector<uint8_t> zlib_compress(const std::vector<uint8_t>& input) {
    uLongf compressed_size = compressBound(static_cast<uLong>(input.size()));
    std::vector<uint8_t> output(static_cast<size_t>(compressed_size), 0);
    const int result = compress2(
        output.data(),
        &compressed_size,
        input.data(),
        static_cast<uLong>(input.size()),
        Z_BEST_SPEED
    );
    if (result != Z_OK) {
        throw std::runtime_error("Bonsai PNG compression failed.");
    }
    output.resize(static_cast<size_t>(compressed_size));
    return output;
}

std::vector<uint8_t> png_bytes(const BonsaiNchwTensor& decoded) {
    if (decoded.width > std::numeric_limits<uint32_t>::max() ||
        decoded.height > std::numeric_limits<uint32_t>::max()) {
        throw std::runtime_error("Bonsai decoded image is too large for PNG.");
    }

    std::vector<uint8_t> png;
    png.insert(png.end(), std::begin(PNG_SIGNATURE), std::end(PNG_SIGNATURE));

    std::vector<uint8_t> ihdr;
    append_u32_be(&ihdr, static_cast<uint32_t>(decoded.width));
    append_u32_be(&ihdr, static_cast<uint32_t>(decoded.height));
    ihdr.push_back(8);
    ihdr.push_back(2);
    ihdr.push_back(0);
    ihdr.push_back(0);
    ihdr.push_back(0);
    append_chunk(&png, "IHDR", ihdr);
    append_chunk(&png, "IDAT", zlib_compress(rgb_scanlines(decoded)));
    append_chunk(&png, "IEND", {});
    return png;
}

std::string base64_encode(const std::vector<uint8_t>& input) {
    std::string output;
    output.reserve(((input.size() + 2U) / 3U) * 4U);
    for (size_t index = 0; index < input.size(); index += 3U) {
        const uint32_t octet_a = input[index];
        const uint32_t octet_b = index + 1U < input.size() ? input[index + 1U] : 0U;
        const uint32_t octet_c = index + 2U < input.size() ? input[index + 2U] : 0U;
        const uint32_t triple = (octet_a << 16U) | (octet_b << 8U) | octet_c;
        output.push_back(BASE64_ALPHABET[(triple >> 18U) & 0x3FU]);
        output.push_back(BASE64_ALPHABET[(triple >> 12U) & 0x3FU]);
        output.push_back(index + 1U < input.size() ? BASE64_ALPHABET[(triple >> 6U) & 0x3FU] : '=');
        output.push_back(index + 2U < input.size() ? BASE64_ALPHABET[triple & 0x3FU] : '=');
    }
    return output;
}

} // namespace

std::string bonsai_encode_nchw_tensor_as_png_base64(
    const BonsaiNchwTensor& decoded
) {
    return base64_encode(png_bytes(decoded));
}
