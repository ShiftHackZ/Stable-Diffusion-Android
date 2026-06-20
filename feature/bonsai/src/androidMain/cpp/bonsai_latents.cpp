#include "bonsai_latents.h"

#include <limits>
#include <random>
#include <stdexcept>
#include <string>

namespace {

uint64_t checked_multiply(uint64_t left, uint64_t right, const char* label) {
    if (left != 0 && right > std::numeric_limits<uint64_t>::max() / left) {
        throw std::runtime_error(std::string("Bonsai latent shape is too large: ") + label);
    }
    return left * right;
}

uint64_t checked_size(
    uint64_t first,
    uint64_t second,
    uint64_t third,
    uint64_t fourth,
    const char* label
) {
    return checked_multiply(
        checked_multiply(checked_multiply(first, second, label), third, label),
        fourth,
        label
    );
}

void require_positive(uint64_t value, const char* label) {
    if (value == 0) {
        throw std::runtime_error(std::string("Bonsai latent ") + label + " must be positive.");
    }
}

} // namespace

BonsaiLatentShape bonsai_packed_latent_shape(
    uint64_t image_height,
    uint64_t image_width,
    uint64_t batch_size,
    uint64_t channels,
    uint64_t vae_scale_factor
) {
    require_positive(image_height, "image height");
    require_positive(image_width, "image width");
    require_positive(batch_size, "batch size");
    require_positive(channels, "channels");
    require_positive(vae_scale_factor, "VAE scale factor");

    const uint64_t divisor = checked_multiply(vae_scale_factor, 2, "vae scale divisor");
    const uint64_t normalized_height = 2 * (image_height / divisor);
    const uint64_t normalized_width = 2 * (image_width / divisor);
    const uint64_t latent_height = normalized_height / 2;
    const uint64_t latent_width = normalized_width / 2;
    require_positive(latent_height, "height");
    require_positive(latent_width, "width");

    return BonsaiLatentShape {
        batch_size,
        channels,
        latent_height,
        latent_width,
        checked_multiply(latent_height, latent_width, "sequence length"),
    };
}

std::vector<int32_t> bonsai_latent_grid_ids(
    uint64_t batch_size,
    uint64_t latent_height,
    uint64_t latent_width
) {
    require_positive(batch_size, "batch size");
    require_positive(latent_height, "height");
    require_positive(latent_width, "width");

    const uint64_t value_count = checked_size(
        batch_size,
        latent_height,
        latent_width,
        4,
        "grid ids"
    );
    if (latent_height > static_cast<uint64_t>(std::numeric_limits<int32_t>::max()) ||
        latent_width > static_cast<uint64_t>(std::numeric_limits<int32_t>::max())) {
        throw std::runtime_error("Bonsai latent grid is too large.");
    }

    std::vector<int32_t> values;
    values.reserve(static_cast<size_t>(value_count));
    for (uint64_t batch = 0; batch < batch_size; batch++) {
        for (uint64_t row = 0; row < latent_height; row++) {
            for (uint64_t column = 0; column < latent_width; column++) {
                values.push_back(0);
                values.push_back(static_cast<int32_t>(row));
                values.push_back(static_cast<int32_t>(column));
                values.push_back(0);
            }
        }
    }
    return values;
}

std::vector<float> bonsai_random_latents_nchw(
    uint64_t batch_size,
    uint64_t channels,
    uint64_t latent_height,
    uint64_t latent_width,
    int64_t seed
) {
    require_positive(batch_size, "batch size");
    require_positive(channels, "channels");
    require_positive(latent_height, "height");
    require_positive(latent_width, "width");
    const uint64_t value_count = checked_size(
        batch_size,
        channels,
        latent_height,
        latent_width,
        "random latents"
    );

    std::mt19937 generator(static_cast<uint32_t>(seed));
    std::normal_distribution<float> distribution(0.0F, 1.0F);
    std::vector<float> output;
    output.reserve(static_cast<size_t>(value_count));
    for (uint64_t index = 0; index < value_count; index++) {
        output.push_back(distribution(generator));
    }
    return output;
}

std::vector<float> bonsai_pack_latents_nchw(
    const std::vector<float>& latents,
    uint64_t batch_size,
    uint64_t channels,
    uint64_t latent_height,
    uint64_t latent_width
) {
    const uint64_t input_count = checked_size(
        batch_size,
        channels,
        latent_height,
        latent_width,
        "pack"
    );
    if (latents.size() != static_cast<size_t>(input_count)) {
        throw std::runtime_error("Bonsai latent pack input size mismatch.");
    }

    std::vector<float> output;
    output.reserve(static_cast<size_t>(input_count));
    const uint64_t sequence_length = checked_multiply(latent_height, latent_width, "pack seq");
    for (uint64_t batch = 0; batch < batch_size; batch++) {
        for (uint64_t position = 0; position < sequence_length; position++) {
            const uint64_t row = position / latent_width;
            const uint64_t column = position % latent_width;
            for (uint64_t channel = 0; channel < channels; channel++) {
                const uint64_t input_index =
                    ((batch * channels + channel) * latent_height + row) * latent_width + column;
                output.push_back(latents[static_cast<size_t>(input_index)]);
            }
        }
    }
    return output;
}

std::vector<float> bonsai_unpack_packed_latents(
    const std::vector<float>& latents,
    uint64_t batch_size,
    uint64_t sequence_length,
    uint64_t channels,
    uint64_t image_height,
    uint64_t image_width,
    uint64_t vae_scale_factor
) {
    const BonsaiLatentShape shape = bonsai_packed_latent_shape(
        image_height,
        image_width,
        batch_size,
        channels,
        vae_scale_factor
    );
    if (sequence_length != shape.sequence_length) {
        throw std::runtime_error("Bonsai packed latent sequence length mismatch.");
    }

    const uint64_t input_count = checked_multiply(
        checked_multiply(batch_size, sequence_length, "unpack"),
        channels,
        "unpack"
    );
    if (latents.size() != static_cast<size_t>(input_count)) {
        throw std::runtime_error("Bonsai latent unpack input size mismatch.");
    }

    std::vector<float> output(static_cast<size_t>(input_count), 0.0F);
    for (uint64_t batch = 0; batch < batch_size; batch++) {
        for (uint64_t position = 0; position < sequence_length; position++) {
            const uint64_t row = position / shape.latent_width;
            const uint64_t column = position % shape.latent_width;
            for (uint64_t channel = 0; channel < channels; channel++) {
                const uint64_t input_index = (batch * sequence_length + position) * channels + channel;
                const uint64_t output_index =
                    ((batch * channels + channel) * shape.latent_height + row) *
                        shape.latent_width +
                    column;
                output[static_cast<size_t>(output_index)] =
                    latents[static_cast<size_t>(input_index)];
            }
        }
    }
    return output;
}
