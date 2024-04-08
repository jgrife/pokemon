package com.example.pokemon.domain

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.pokemon.network.PokemonService
import com.example.pokemon.network.PokemonService.Companion.toResult
import com.example.pokemon.network.models.Pokemon
import com.example.pokemon.network.models.PokemonDetails
import com.example.pokemon.paging.POKEMON_LIMIT
import com.example.pokemon.paging.PokemonPagingSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PokemonRepository @Inject constructor(private val service: PokemonService) {

    fun getPokemons(): Flow<PagingData<Pokemon>> {
        return Pager(
            config = PagingConfig(
                pageSize = POKEMON_LIMIT,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { PokemonPagingSource(service) }
        ).flow
    }

    suspend fun getPokemonDetails(pokemonId: String?): Result<PokemonDetails> {
        if (pokemonId == null) return Result.failure(IllegalArgumentException("Missing Pokemon ID"))
        return withContext(Dispatchers.IO) {
            try {
                service.getPokemonDetails(pokemonId).toResult()
            } catch (ex: Exception) {
                Result.failure(ex)
            }
        }
    }
}