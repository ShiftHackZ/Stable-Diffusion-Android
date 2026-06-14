package com.shifthackz.aisdv1.domain.entity

/**
 * Lists ArliAI sampler names supported by the setup and generation forms.
 *
 * @property key sampler name sent to ArliAI.
 *
 * @author Dmitriy Moroz
 */
enum class ArliAiSampler(
    val key: String,
) {
    DPM_PLUS_PLUS_2M_KARRAS("DPM++ 2M Karras"),
    EULER_A("Euler a"),
    UNIPC("UniPC"),
    DDIM("DDIM"),
    EULER("Euler"),
    EULER_SGM("Euler SGM"),
    EULER_EDM("Euler EDM"),
    DPM_PLUS_PLUS_2M("DPM++ 2M"),
    DPM_PLUS_PLUS_3M("DPM++ 3M"),
    DPM_PLUS_PLUS_1S("DPM++ 1S"),
    DPM_PLUS_PLUS_SDE("DPM++ SDE"),
    DPM_PLUS_PLUS_2M_SDE("DPM++ 2M SDE"),
    DPM_PLUS_PLUS_2M_EDM("DPM++ 2M EDM"),
    DPM_PLUS_PLUS_COSINE("DPM++ Cosine"),
    DPM_SDE("DPM SDE"),
    DPM_PLUS_PLUS_INVERSE("DPM++ Inverse"),
    DPM_PLUS_PLUS_2M_INVERSE("DPM++ 2M Inverse"),
    DPM_PLUS_PLUS_3M_INVERSE("DPM++ 3M Inverse"),
    HEUN("Heun"),
    DEIS("DEIS"),
    LCM("LCM"),
    ;

    companion object {
        /**
         * Default sampler used when the payload does not specify one.
         *
         * @author Dmitriy Moroz
         */
        val default: ArliAiSampler = DPM_PLUS_PLUS_2M_KARRAS

        /**
         * Provider-facing sampler names shown in UI selectors.
         *
         * @author Dmitriy Moroz
         */
        val supported: List<String> = entries.map(ArliAiSampler::key)
    }
}
