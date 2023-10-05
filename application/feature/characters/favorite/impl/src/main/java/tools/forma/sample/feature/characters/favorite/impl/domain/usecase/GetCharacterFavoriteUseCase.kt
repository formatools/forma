package tools.forma.sample.feature.characters.favorite.impl.domain.usecase

import tools.forma.sample.feature.characters.core.api.domain.model.ICharacter
import tools.forma.sample.feature.characters.favorite.api.domain.repository.ICharacterFavoriteRepository
import tools.forma.sample.feature.characters.favorite.api.domain.usecase.IGetCharacterFavoriteUseCase
import javax.inject.Inject

class GetCharacterFavoriteUseCase @Inject constructor(private val repository: ICharacterFavoriteRepository) : IGetCharacterFavoriteUseCase {

    override suspend operator fun invoke(characterFavoriteId: Long): ICharacter? =
        repository.getCharacterFavorite(characterFavoriteId)
}