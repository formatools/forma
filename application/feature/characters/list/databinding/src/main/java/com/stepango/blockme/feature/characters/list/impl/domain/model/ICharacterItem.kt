package com.stepango.blockme.feature.characters.list.impl.domain.model

// TODO https://github.com/formatools/forma/issues/47
// Move out into :api target
interface ICharacterItem {
    val id: Long
    val name: String
    val description: String
    val imageUrl: String
}