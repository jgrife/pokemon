package com.example.pokemon.domain

import com.example.pokemon.network.PokemonService
import com.example.pokemon.network.models.PokemonDetailsDTO
import com.example.pokemon.network.models.PokemonsDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.IllegalArgumentException
import javax.inject.Inject

class PokemonRepository @Inject constructor(private val service: PokemonService) {

    suspend fun getPokemons(): Result<PokemonsDTO> {
        return withContext(Dispatchers.IO) {
            try {
                val response = service.getPokemons()
                Result.Success(response)
            } catch (ex: Exception) {
                Result.Error(ex)
            }
        }
    }

    suspend fun getPokemonDetails(pokemonId: String?): Result<PokemonDetailsDTO> {
        if (pokemonId == null) return Result.Error(IllegalArgumentException("Missing Pokemon ID"))
        return withContext(Dispatchers.IO) {
            try {
                val response = service.getPokemonDetails(pokemonId)
                Result.Success(response)
            } catch (ex: Exception) {
                Result.Error(ex)
            }
        }
    }
}