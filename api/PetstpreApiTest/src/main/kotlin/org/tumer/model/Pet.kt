package org.tumer.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Pet(
    val id: Long? = 0,
    val category: Category? = null,
    val name: String? = null,
    val photoUrls: List<String> = emptyList(),
    val tags: List<Tag> = emptyList(),
    val status: String? = null
)
