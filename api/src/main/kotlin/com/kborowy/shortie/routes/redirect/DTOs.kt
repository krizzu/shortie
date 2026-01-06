package com.kborowy.shortie.routes.redirect

import kotlinx.serialization.Serializable

@Serializable data class ShortiePasswordResponseDTO(val redirectUrl: String)
