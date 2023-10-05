package tools.forma.sample.feature.characters.core.impl.domain.model

import tools.forma.sample.feature.characters.core.api.domain.model.ICharacter

data class Character(
    override val id: Long,
    override val name: String,
    override val description: String,
    override val imageUrl: String
) : ICharacter