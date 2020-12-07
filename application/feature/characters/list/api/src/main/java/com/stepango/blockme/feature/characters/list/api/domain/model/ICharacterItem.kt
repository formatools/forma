package com.stepango.blockme.feature.characters.list.api.domain.model

interface ICharacterItem {
    val id: Long
    val name: String
    val description: String
    val imageUrl: String
}