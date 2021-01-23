package com.stepango.blockme.feature.characters.core.api.domain.model

interface ICharacter {
    val id: Long
    val name: String
    val description: String
    val imageUrl: String
}