#pragma once

#include <cstdint>
#include <vector>

struct BonsaiFluxSingleModulation {
    uint64_t batch = 0;
    uint64_t dimensions = 0;
    std::vector<float> shift;
    std::vector<float> scale;
    std::vector<float> gate;
};

struct BonsaiFluxDoubleModulation {
    uint64_t batch = 0;
    uint64_t dimensions = 0;
    std::vector<float> shift_msa;
    std::vector<float> scale_msa;
    std::vector<float> gate_msa;
    std::vector<float> shift_mlp;
    std::vector<float> scale_mlp;
    std::vector<float> gate_mlp;
};

struct BonsaiFluxNormOutModulation {
    uint64_t batch = 0;
    uint64_t dimensions = 0;
    std::vector<float> scale;
    std::vector<float> shift;
};

BonsaiFluxSingleModulation bonsai_flux_split_single_modulation(
    const std::vector<float>& values,
    uint64_t batch,
    uint64_t dimensions
);

BonsaiFluxDoubleModulation bonsai_flux_split_double_modulation(
    const std::vector<float>& values,
    uint64_t batch,
    uint64_t dimensions
);

BonsaiFluxNormOutModulation bonsai_flux_split_norm_out_modulation(
    const std::vector<float>& values,
    uint64_t batch,
    uint64_t dimensions
);

std::vector<float> bonsai_flux_apply_modulated_layer_norm(
    const std::vector<float>& input,
    const std::vector<float>& shift,
    const std::vector<float>& scale,
    uint64_t batch,
    uint64_t sequence_length,
    uint64_t dimensions,
    float epsilon
);

std::vector<float> bonsai_flux_apply_gated_residual(
    const std::vector<float>& residual,
    const std::vector<float>& update,
    const std::vector<float>& gate,
    uint64_t batch,
    uint64_t sequence_length,
    uint64_t dimensions
);
