package tools.forma.sample.feature.characters.favorite.impl.domain.usecase

import tools.forma.sample.feature.characters.core.api.domain.model.ICharacter
import tools.forma.sample.feature.characters.favorite.api.domain.repository.ICharacterFavoriteRepository
import tools.forma.sample.feature.characters.favorite.api.domain.usecase.IGetAllCharactersFavoriteUseCase
import javax.inject.Inject

class GetAllCharactersFavoriteUseCase @Inject constructor(private val repository: ICharacterFavoriteRepository) : IGetAllCharactersFavoriteUseCase {

    override suspend operator fun invoke(): List<ICharacter> =
        repository.getAllCharactersFavorite()
}