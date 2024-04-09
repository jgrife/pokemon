package com.example.pokemon.domain

import com.example.pokemon.data.local.PokemonDatabase
import com.example.pokemon.data.remote.PokemonService
import com.example.pokemon.data.remote.models.PokemonDetails
import com.example.pokemon.data.remote.models.Sprites
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import retrofit2.Response

class PokemonRepositoryTest {

    private val service = mock(PokemonService::class.java)
    private val db = mock(PokemonDatabase::class.java)
    private val repository = PokemonRepository(service, db)

    @Test
    fun `getPokemonDetails Success`() = runTest {
        val pokemonId = "1"
        val mockPokemonDetails = PokemonDetails(
            name = "Bulbasaur",
            id = 1,
            height = 10,
            weight = 10,
            sprites = Sprites(""),
            states = emptyList(),
            types = emptyList()
        )
        val response = Response.success(mockPokemonDetails)
        `when`(service.getPokemonDetails(pokemonId)).thenReturn(response)

        val result = repository.getPokemonDetails(pokemonId)

        assertEquals(Result.success(mockPokemonDetails), result)
    }

    @Test
    fun `getPokemonDetails Failure - missing ID`() = runTest {
        val result = repository.getPokemonDetails(null)

        assertEquals("Missing Pokemon ID", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getPokemonDetails Failure - service error`() = runTest {
        val pokemonId = "1"
        val mockException = Exception("Service error")
        `when`(service.getPokemonDetails(pokemonId)).thenAnswer { throw mockException }

        val result = repository.getPokemonDetails(pokemonId)

        assertTrue(result.isFailure)
        assertEquals(mockException, result.exceptionOrNull())
    }
}