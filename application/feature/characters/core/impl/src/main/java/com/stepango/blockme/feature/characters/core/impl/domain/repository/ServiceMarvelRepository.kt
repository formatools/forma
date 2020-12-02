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

package com.stepango.blockme.feature.characters.core.impl.domain.repository

import com.stepango.blockme.common.extensions.util.toMD5
import com.stepango.blockme.core.network.library.response.BaseResponse
import com.stepango.blockme.feature.characters.core.api.data.response.CharacterResponse
import com.stepango.blockme.feature.characters.core.api.domain.repository.MarvelRepository
import com.stepango.blockme.feature.characters.core.api.data.service.MarvelService
import javax.inject.Inject

// TODO https://github.com/formatools/forma/issues/45
// Move out from here API keys into some config
private const val API_PUBLIC_KEY = "34fc91a3d879f19895b515d8273965f9"
private const val API_PRIVATE_KEY = "d66d90c45623d9a1daeb9a398a7ded8268a9628f"
private const val HASH_FORMAT = "%s%s%s"

/**
 * Repository module for handling marvel API network operations [MarvelService].
 */
internal class ServiceMarvelRepository @Inject constructor(
    private val service: MarvelService
) : MarvelRepository {

    /**
     * Get all info of Marvel character.
     *
     * @param id A single character id.
     * @return Response for single character resource.
     */
    override suspend fun getCharacter(id: Long): BaseResponse<CharacterResponse> {
        val timestamp = System.currentTimeMillis().toString()
        return service.getCharacter(
            id = id,
            apiKey = API_PUBLIC_KEY,
            hash = generateApiHash(timestamp),
            timestamp = timestamp)
    }

    /**
     * Get all Marvel characters by paged.
     *
     * @param offset Skip the specified number of resources in the result set.
     * @param limit Limit the result set to the specified number of resources.
     * @return Response for comic characters resource.
     */
    override suspend fun getCharacters(offset: Int, limit: Int): BaseResponse<CharacterResponse> {
        val timestamp = System.currentTimeMillis().toString()
        return service.getCharacters(
            apiKey = API_PUBLIC_KEY,
            hash = generateApiHash(timestamp),
            timestamp = timestamp,
            offset = offset,
            limit = limit)
    }

    /**
     * Generate a md5 digest of the timestamp parameter, private API key and public.
     *
     * @param timestamp A digital current record of the time.
     * @return The MD5 Hash
     */
    private fun generateApiHash(timestamp: String) =
        HASH_FORMAT.format(timestamp, API_PRIVATE_KEY, API_PUBLIC_KEY).toMD5()
}
