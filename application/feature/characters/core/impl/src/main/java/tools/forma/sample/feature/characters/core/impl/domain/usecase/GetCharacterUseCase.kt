package tools.forma.sample.feature.characters.core.impl.domain.usecase

import tools.forma.sample.feature.characters.core.api.domain.model.ICharacter
import tools.forma.sample.feature.characters.core.api.domain.repository.MarvelRepository
import tools.forma.sample.feature.characters.core.api.domain.usecase.IGetCharacterUseCase
import javax.inject.Inject

class GetCharacterUseCase @Inject constructor(
    private val repository: MarvelRepository,
) : IGetCharacterUseCase {

    override suspend fun invoke(id: Long): ICharacter =
        repository.getCharacter(id)
}
