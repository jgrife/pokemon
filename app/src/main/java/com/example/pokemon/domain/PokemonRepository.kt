package com.example.pokemon.domain

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.pokemon.data.local.PokemonDatabase
import com.example.pokemon.data.local.models.PokemonEntity
import com.example.pokemon.data.remote.PokemonRemoteMediator
import com.example.pokemon.data.remote.PokemonService
import com.example.pokemon.data.remote.PokemonService.Companion.toResult
import com.example.pokemon.data.remote.models.PokemonDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

const val POKEMON_LIMIT = 20

@OptIn(ExperimentalPagingApi::class)
class PokemonRepository @Inject constructor(
    private val service: PokemonService,
    private val db: PokemonDatabase
) {

    fun getPokemon(query: String): Flow<PagingData<PokemonEntity>> {
        // appending wildcard '%' so we can allow other characters to be before and after the query string in the search
        val dbQuery = "%${query.replace(' ', '%')}%"
        return Pager(
            config = PagingConfig(
                pageSize = POKEMON_LIMIT,
                enablePlaceholders = false
            ),
            remoteMediator = PokemonRemoteMediator(db, service),
            pagingSourceFactory = { db.dao.pagingSource(dbQuery) }
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