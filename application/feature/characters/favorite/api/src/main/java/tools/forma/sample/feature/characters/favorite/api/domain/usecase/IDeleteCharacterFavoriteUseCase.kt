package tools.forma.sample.feature.characters.favorite.api.domain.usecase

import tools.forma.sample.feature.characters.core.api.domain.model.ICharacter

interface IDeleteCharacterFavoriteUseCase {

    suspend operator fun invoke(character: ICharacter)
}