package tools.forma.sample.feature.characters.detail.api.presentation

interface ICharacterDetailViewModel {

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