package tools.forma.sample.feature.characters.favorite.impl.domain.usecase

import tools.forma.sample.feature.characters.core.api.domain.model.ICharacter
import tools.forma.sample.feature.characters.favorite.api.domain.repository.ICharacterFavoriteRepository
import tools.forma.sample.feature.characters.favorite.api.domain.usecase.IDeleteCharacterFavoriteUseCase
import javax.inject.Inject

class DeleteCharacterFavoriteUseCase @Inject constructor(private val repository: ICharacterFavoriteRepository) : IDeleteCharacterFavoriteUseCase {

    override suspend operator fun invoke(character: ICharacter) {
        repository.deleteCharacterFavorite(character)
    }
}