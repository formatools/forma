package tools.forma.sample.feature.characters.favorite.impl.domain.usecase

import tools.forma.sample.feature.characters.favorite.api.domain.repository.ICharacterFavoriteRepository
import tools.forma.sample.feature.characters.favorite.api.domain.usecase.ISetCharacterFavoriteUseCase
import javax.inject.Inject

class SetCharacterFavoriteUseCase @Inject constructor(private val repository: ICharacterFavoriteRepository) : ISetCharacterFavoriteUseCase {

    override suspend operator fun invoke(id: Long, name: String, description: String, imageUrl: String) {
        repository.insertCharacterFavorite(id, name, description, imageUrl)
    }
}