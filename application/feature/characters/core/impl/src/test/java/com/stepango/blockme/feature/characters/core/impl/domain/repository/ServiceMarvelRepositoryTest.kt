package com.stepango.blockme.feature.characters.core.impl.domain.repository

import com.stepango.blockme.common.extensions.util.toMD5
import com.stepango.blockme.common.util.clock.Clock
import com.stepango.blockme.core.network.library.Config
import com.stepango.blockme.core.network.library.response.BaseResponse
import com.stepango.blockme.feature.characters.core.api.data.response.CharacterResponse
import com.stepango.blockme.feature.characters.core.api.data.service.MarvelService
import com.stepango.blockme.feature.characters.core.api.domain.repository.MarvelRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

internal class ServiceMarvelRepositoryTest {

	private companion object {

		const val HASH_FORMAT = "%s%s%s"
		const val ID = 235235L
		const val API_PRIVATE_KEY = "TheApiKey"
		const val API_PUBLIC_KEY = "TheApiPublicKey"
		const val TIMESTAMP = 123456789L

		val HASH = HASH_FORMAT.format(TIMESTAMP, API_PRIVATE_KEY, API_PUBLIC_KEY).toMD5()
	}

	private val config: Config = mockk()
	private val marvelService: MarvelService = mockk()
	private val clock: Clock = mockk()

	private val repository: MarvelRepository = ServiceMarvelRepository(marvelService, config, clock)

	@Test
	fun `get a character EXPECT the base response`() = runBlocking {
		val expectedResponse: BaseResponse<CharacterResponse> = mockk()

		every { clock.currentTimeMillis() } returns TIMESTAMP
		every { config.privateApiKey } returns API_PRIVATE_KEY
		every { config.publicApiKey } returns API_PUBLIC_KEY
		coEvery { marvelService.getCharacter(ID, API_PUBLIC_KEY, HASH, TIMESTAMP.toString()) } returns expectedResponse

		val result = repository.getCharacter(ID)

		assertEquals(expectedResponse, result)
	}

	@Test
	fun `get characters EXPECT the base response`() = runBlocking {
		val offset = 10
		val limit = 20

		val expectedResponse: BaseResponse<CharacterResponse> = mockk()

		every { clock.currentTimeMillis() } returns TIMESTAMP
		every { config.privateApiKey } returns API_PRIVATE_KEY
		every { config.publicApiKey } returns API_PUBLIC_KEY
		coEvery { marvelService.getCharacters(API_PUBLIC_KEY, HASH, TIMESTAMP.toString(), offset, limit) } returns expectedResponse

		val result = repository.getCharacters(offset, limit)

		assertEquals(expectedResponse, result)
	}
}