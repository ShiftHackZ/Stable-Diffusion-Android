#include "bonsai_scheduler.h"

#include <algorithm>
#include <cmath>
#include <stdexcept>

namespace {

float empirical_mu(uint64_t image_sequence_length, uint64_t steps) {
    const float a1 = 8.73809524e-05F;
    const float b1 = 1.89833333F;
    const float a2 = 0.00016927F;
    const float b2 = 0.45666666F;
    const float sequence_length = static_cast<float>(image_sequence_length);
    if (image_sequence_length > 4300) {
        return a2 * sequence_length + b2;
    }

    const float m200 = a2 * sequence_length + b2;
    const float m10 = a1 * sequence_length + b1;
    const float a = (m200 - m10) / 190.0F;
    const float b = m200 - 200.0F * a;
    return a * static_cast<float>(steps) + b;
}

float time_shift(float mu, float sigma_power, float timestep) {
    const float numerator = std::exp(mu);
    return numerator / (numerator + std::pow(1.0F / timestep - 1.0F, sigma_power));
}

} // namespace

BonsaiFlowMatchEulerSchedule bonsai_flow_match_euler_schedule(
    uint64_t image_sequence_length,
    uint64_t steps
) {
    if (image_sequence_length == 0) {
        throw std::runtime_error("Bonsai scheduler image sequence length must be positive.");
    }

    const uint64_t step_count = std::max<uint64_t>(1, steps);
    const float mu = empirical_mu(image_sequence_length, step_count);

    BonsaiFlowMatchEulerSchedule output;
    output.timesteps.reserve(static_cast<size_t>(step_count));
    output.sigmas.reserve(static_cast<size_t>(step_count + 1));
    for (uint64_t index = 0; index < step_count; index++) {
        const float linear = 1.0F -
            static_cast<float>(index) *
                (1.0F - 1.0F / static_cast<float>(step_count)) /
                static_cast<float>(std::max<uint64_t>(1, step_count - 1));
        const float sigma = time_shift(mu, 1.0F, linear);
        output.sigmas.push_back(sigma);
        output.timesteps.push_back(sigma * 1000.0F);
    }
    output.sigmas.push_back(0.0F);
    return output;
}

std::vector<float> bonsai_flow_match_euler_step(
    const std::vector<float>& noise,
    uint64_t timestep_index,
    const std::vector<float>& latents,
    const BonsaiFlowMatchEulerSchedule& schedule
) {
    if (noise.size() != latents.size()) {
        throw std::runtime_error("Bonsai scheduler noise/latents size mismatch.");
    }
    if (schedule.sigmas.size() != schedule.timesteps.size() + 1) {
        throw std::runtime_error("Bonsai scheduler shape mismatch.");
    }
    if (timestep_index + 1 >= schedule.sigmas.size()) {
        throw std::runtime_error("Bonsai scheduler timestep is out of range.");
    }

    const float delta = schedule.sigmas[static_cast<size_t>(timestep_index + 1)] -
        schedule.sigmas[static_cast<size_t>(timestep_index)];
    std::vector<float> output;
    output.reserve(latents.size());
    for (size_t index = 0; index < latents.size(); index++) {
        output.push_back(latents[index] + delta * noise[index]);
    }
    return output;
}
