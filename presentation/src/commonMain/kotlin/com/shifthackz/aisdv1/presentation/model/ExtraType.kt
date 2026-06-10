package com.shifthackz.aisdv1.presentation.model

/**
 * Coordinates `ExtraType` behavior in the SDAI presentation layer.
 *
 * @param raw raw value consumed by the API.
 * @author Dmitriy Moroz
 */
enum class ExtraType(val raw: String) {
    Lora("lora"),
    HyperNet("hyper_net");
}
