package com.shifthackz.aisdv1.domain.entity

import java.io.Serializable

enum class StabilityAiStylePreset(val key: String) : Serializable {
    NONE(""),
    MODEL_3D("3d-model"),
    ANALOG_FILM("analog-film"),
    ANIME("anime"),
    CINEMATIC("cinematic"),
    COMIC_BOOK("comic-book"),
    DIGITAL_ART("digital-art"),
    ENHANCE("enhance"),
    FANTASY_ART("fantasy-art"),
    ISOMETRIC("isometric"),
    LINE_ART("line-art"),
    LOW_POLY("low-poly"),
    MODELING_COMPOUND("modeling-compound"),
    NEON_PUNK("neon-punk"),
    ORIGAMI("origami"),
    PHOTOGRAPHIC("photographic"),
    PIXEL_ART("pixel-art"),
    TILE_TEXTURE("tile-texture");
}
