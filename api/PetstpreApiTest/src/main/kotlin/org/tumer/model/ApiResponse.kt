package org.tumer.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class ApiResponse(
    val code: Int? = 0,
    val type: String? = null,
    val message: String? = null
)
