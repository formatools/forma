package tools.forma.sample.feature.characters.core.api.domain.usecase

import tools.forma.sample.feature.characters.core.api.domain.model.ICharacter

interface IGetCharacterUseCase {

    suspend operator fun invoke(id: Long): ICharacter
}
