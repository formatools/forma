package com.stepango.blockme.feature.characters.detail.impl.presentation

import androidx.lifecycle.LiveData
import com.stepango.blockme.feature.characters.core.api.domain.model.ICharacter

interface ICharacterDetailViewModel {

    val data: LiveData<ICharacter>
    val state: LiveData<ICharacterDetailViewState>

    fun loadCharacterDetail(characterId: Long)

    fun addCharacterToFavorite()

    fun dismissCharacterDetail()
}

interface ICharacterDetailViewState {
    fun isError(): Boolean
    fun isLoading(): Boolean
    fun isDismiss(): Boolean
    fun isAddToFavorite(): Boolean
    fun isAddedToFavorite(): Boolean
    fun isAlreadyAddedToFavorite(): Boolean
}