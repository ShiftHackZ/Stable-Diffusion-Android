package com.shifthackz.aisdv1.network.response

import com.google.gson.annotations.SerializedName

data class SdGenerationResponse(
    @SerializedName("images")
    val images: List<String>,
    @SerializedName("info")
    val info: String,
) {
    data class Info(
        @SerializedName("seed")
        val seed: Long,
    )
}

/* EXAMPLE INFO FIELD
{

  "info": "{\"prompt\": \"opel car\", \"all_prompts\": [\"opel car\"], \"negative_prompt\": \"red\", \"all_negative_prompts\": [\"red\"], \"seed\": 104773192, \"all_seeds\": [104773192], \"subseed\": 993352529, \"all_subseeds\": [993352529], \"subseed_strength\": 0, \"width\": 512, \"height\": 512, \"sampler_name\": \"Euler\", \"cfg_scale\": 7.0, \"steps\": 20, \"batch_size\": 1, \"restore_faces\": true, \"face_restoration_model\": \"CodeFormer\", \"sd_model_hash\": \"44f90a0972\", \"seed_resize_from_w\": -1, \"seed_resize_from_h\": -1, \"denoising_strength\": 0, \"extra_generation_params\": {}, \"index_of_first_image\": 0, \"infotexts\": [\"opel car\\nNegative prompt: red\\nSteps: 20, Sampler: Euler, CFG scale: 7.0, Seed: 104773192, Face restoration: CodeFormer, Size: 512x512, Model hash: 44f90a0972, Model: protogenX34Photorealism_1, Seed resize from: -1x-1, Denoising strength: 0\"], \"styles\": [], \"job_timestamp\": \"20230305173806\", \"clip_skip\": 1, \"is_using_inpainting_conditioning\": false}"
}
 */
