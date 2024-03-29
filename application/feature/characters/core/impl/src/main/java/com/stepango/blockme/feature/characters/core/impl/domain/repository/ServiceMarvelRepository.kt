/*
 * Copyright 2019 vmadalin.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tools.forma.sample.feature.characters.core.impl.domain.repository

import tools.forma.sample.common.extensions.util.toMD5
import tools.forma.sample.common.util.clock.Clock
import tools.forma.sample.common.util.mapper.Mapper
import tools.forma.sample.core.network.library.Config
import tools.forma.sample.core.network.library.response.BaseResponse
import tools.forma.sample.feature.characters.core.api.data.response.CharacterResponse
import tools.forma.sample.feature.characters.core.api.data.service.MarvelService
import tools.forma.sample.feature.characters.core.api.domain.model.ICharacter
import tools.forma.sample.feature.characters.core.api.domain.repository.MarvelRepository
import javax.inject.Inject

private const val HASH_FORMAT = "%s%s%s"

/**
 * Repository module for handling marvel API network operations [MarvelService].
 */
internal class ServiceMarvelRepository @Inject constructor(
	private val service: MarvelService,
	private val config: Config,
	private val clock: Clock,
	private val characterMapper: Mapper<BaseResponse<CharacterResponse>, List<ICharacter>>
) : MarvelRepository {

	/**
	 * Get all info of Marvel character.
	 *
	 * @param id A single character id.
	 * @return Response for single character resource.
	 */
	override suspend fun getCharacter(id: Long): ICharacter {
		val timestamp = clock.currentTimeMillis().toString()

		val result = service.getCharacter(
			id = id,
			apiKey = config.publicApiKey,
			hash = generateApiHash(timestamp),
			timestamp = timestamp
		)

		return characterMapper.map(result).first()
	}

	/**
	 * Get all Marvel characters by paged.
	 *
	 * @param offset Skip the specified number of resources in the result set.
	 * @param limit Limit the result set to the specified number of resources.
	 * @return Response for comic characters resource.
	 */
	override suspend fun getCharacters(offset: Int, limit: Int): List<ICharacter> {
		val timestamp = clock.currentTimeMillis().toString()
		val result = service.getCharacters(
			apiKey = config.publicApiKey,
			hash = generateApiHash(timestamp),
			timestamp = timestamp,
			offset = offset,
			limit = limit
		)

		return characterMapper.map(result)
	}

	/**
	 * Generate a md5 digest of the timestamp parameter, private API key and public.
	 *
	 * @param timestamp A digital current record of the time.
	 * @return The MD5 Hash
	 */
	private fun generateApiHash(timestamp: String) =
		HASH_FORMAT.format(timestamp, config.privateApiKey, config.publicApiKey).toMD5()
}
