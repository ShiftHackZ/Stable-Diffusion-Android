#pragma once

#include <cstdint>
#include <vector>

struct BonsaiFlowMatchEulerSchedule {
    std::vector<float> timesteps;
    std::vector<float> sigmas;
};

BonsaiFlowMatchEulerSchedule bonsai_flow_match_euler_schedule(
    uint64_t image_sequence_length,
    uint64_t steps
);

std::vector<float> bonsai_flow_match_euler_step(
    const std::vector<float>& noise,
    uint64_t timestep_index,
    const std::vector<float>& latents,
    const BonsaiFlowMatchEulerSchedule& schedule
);
