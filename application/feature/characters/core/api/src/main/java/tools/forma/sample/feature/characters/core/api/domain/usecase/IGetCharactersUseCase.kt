package tools.forma.sample.feature.characters.core.api.domain.usecase

import tools.forma.sample.feature.characters.core.api.domain.model.ICharacter

interface IGetCharactersUseCase {

    suspend operator fun invoke(offset: Int, limit: Int): List<ICharacter>
}
