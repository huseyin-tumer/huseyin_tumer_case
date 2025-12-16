package org.tumer.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Category(
    val id: Long? = 0,
    val name: String? = null
)
