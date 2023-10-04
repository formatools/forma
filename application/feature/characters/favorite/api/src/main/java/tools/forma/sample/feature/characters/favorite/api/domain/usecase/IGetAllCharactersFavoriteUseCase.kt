package tools.forma.sample.feature.characters.favorite.api.domain.usecase

import tools.forma.sample.feature.characters.core.api.domain.model.ICharacter

interface IGetAllCharactersFavoriteUseCase {

    suspend operator fun invoke(): List<ICharacter>
}