#pragma once

#include "bonsai_vae_ops.h"

#include <string>

std::string bonsai_encode_nchw_tensor_as_png_base64(
    const BonsaiNchwTensor& decoded
);
