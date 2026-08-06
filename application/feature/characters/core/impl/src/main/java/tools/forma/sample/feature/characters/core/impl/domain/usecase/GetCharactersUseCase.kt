package tools.forma.sample.feature.characters.core.impl.domain.usecase

import tools.forma.sample.feature.characters.core.api.domain.model.ICharacter
import tools.forma.sample.feature.characters.core.api.domain.repository.MarvelRepository
import tools.forma.sample.feature.characters.core.api.domain.usecase.IGetCharactersUseCase
import javax.inject.Inject

class GetCharactersUseCase @Inject constructor(
    private val repository: MarvelRepository,
) : IGetCharactersUseCase {

    override suspend fun invoke(offset: Int, limit: Int): List<ICharacter> =
        repository.getCharacters(offset, limit)
}
