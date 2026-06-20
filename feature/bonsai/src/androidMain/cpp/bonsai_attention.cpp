#include "bonsai_attention.h"

#include <algorithm>
#include <cmath>
#include <limits>
#include <stdexcept>

namespace {

uint64_t checked_multiply(uint64_t left, uint64_t right, const char* label) {
    if (left != 0 && right > std::numeric_limits<uint64_t>::max() / left) {
        throw std::runtime_error(label);
    }
    return left * right;
}

uint64_t attention_size(uint64_t heads, uint64_t length, uint64_t head_dimension) {
    return checked_multiply(
        checked_multiply(heads, length, "Bonsai attention shape is too large."),
        head_dimension,
        "Bonsai attention shape is too large."
    );
}

size_t attention_index(
    uint64_t head,
    uint64_t row,
    uint64_t column,
    uint64_t length,
    uint64_t head_dimension
) {
    return static_cast<size_t>(((head * length) + row) * head_dimension + column);
}

float dot_product(
    const std::vector<float>& left,
    const std::vector<float>& right,
    uint64_t left_head,
    uint64_t right_head,
    uint64_t left_row,
    uint64_t right_row,
    uint64_t length,
    uint64_t head_dimension
) {
    float sum = 0.0F;
    for (uint64_t column = 0; column < head_dimension; column++) {
        sum += left[attention_index(left_head, left_row, column, length, head_dimension)] *
            right[attention_index(right_head, right_row, column, length, head_dimension)];
    }
    return sum;
}

} // namespace

std::vector<float> bonsai_repeat_kv_heads(
    const std::vector<float>& input,
    uint64_t key_value_heads,
    uint64_t repeats,
    uint64_t length,
    uint64_t head_dimension
) {
    if (repeats == 0) {
        throw std::runtime_error("Bonsai repeat-KV repeat count must be positive.");
    }
    const uint64_t expected_input = attention_size(key_value_heads, length, head_dimension);
    if (input.size() != static_cast<size_t>(expected_input)) {
        throw std::runtime_error("Bonsai repeat-KV input size mismatch.");
    }

    std::vector<float> output;
    output.reserve(static_cast<size_t>(checked_multiply(
        expected_input,
        repeats,
        "Bonsai repeat-KV output shape is too large."
    )));
    for (uint64_t head = 0; head < key_value_heads; head++) {
        for (uint64_t repeat = 0; repeat < repeats; repeat++) {
            for (uint64_t row = 0; row < length; row++) {
                for (uint64_t column = 0; column < head_dimension; column++) {
                    output.push_back(input[attention_index(
                        head,
                        row,
                        column,
                        length,
                        head_dimension
                    )]);
                }
            }
        }
    }
    return output;
}

std::vector<float> bonsai_scaled_dot_product_attention(
    const std::vector<float>& queries,
    const std::vector<float>& keys,
    const std::vector<float>& values,
    const std::vector<float>& additive_mask,
    uint64_t heads,
    uint64_t length,
    uint64_t head_dimension,
    float scale
) {
    const uint64_t expected_size = attention_size(heads, length, head_dimension);
    if (queries.size() != static_cast<size_t>(expected_size) ||
        keys.size() != static_cast<size_t>(expected_size) ||
        values.size() != static_cast<size_t>(expected_size)
    ) {
        throw std::runtime_error("Bonsai attention input size mismatch.");
    }
    if (!additive_mask.empty() && additive_mask.size() != static_cast<size_t>(length * length)) {
        throw std::runtime_error("Bonsai attention mask size mismatch.");
    }

    std::vector<float> output;
    output.resize(static_cast<size_t>(expected_size), 0.0F);
    std::vector<float> scores;
    scores.resize(static_cast<size_t>(length));

    for (uint64_t head = 0; head < heads; head++) {
        for (uint64_t row = 0; row < length; row++) {
            float max_score = -std::numeric_limits<float>::infinity();
            for (uint64_t key_row = 0; key_row < length; key_row++) {
                float score = dot_product(
                    queries,
                    keys,
                    head,
                    head,
                    row,
                    key_row,
                    length,
                    head_dimension
                ) * scale;
                if (!additive_mask.empty()) {
                    score += additive_mask[static_cast<size_t>(row * length + key_row)];
                }
                scores[static_cast<size_t>(key_row)] = score;
                max_score = std::max(max_score, score);
            }

            float denominator = 0.0F;
            for (uint64_t key_row = 0; key_row < length; key_row++) {
                const float weight = std::exp(scores[static_cast<size_t>(key_row)] - max_score);
                scores[static_cast<size_t>(key_row)] = weight;
                denominator += weight;
            }

            if (denominator == 0.0F) {
                throw std::runtime_error("Bonsai attention softmax denominator is zero.");
            }

            for (uint64_t column = 0; column < head_dimension; column++) {
                float value = 0.0F;
                for (uint64_t key_row = 0; key_row < length; key_row++) {
                    const float weight = scores[static_cast<size_t>(key_row)] / denominator;
                    value += weight * values[attention_index(
                        head,
                        key_row,
                        column,
                        length,
                        head_dimension
                    )];
                }
                output[attention_index(head, row, column, length, head_dimension)] = value;
            }
        }
    }
    return output;
}
