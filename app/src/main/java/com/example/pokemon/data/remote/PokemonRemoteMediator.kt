package com.example.pokemon.data.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.pokemon.data.local.PokemonDatabase
import com.example.pokemon.data.local.models.PokemonEntity
import com.example.pokemon.data.mappers.toPokemonEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalPagingApi::class)
class PokemonRemoteMediator(
    private val local: PokemonDatabase,
    private val remote: PokemonService
) : RemoteMediator<Int, PokemonEntity>() {

    /**
     * Since the Pokemon API doesn't allow for a search by name, below in the [load] function
     * I am calling to get ALL Pokemon and storing tht to the db.
     *
     * Now here in [initialize] I am utilizing a lastUpdate timestamp in the db. And for
     * one hour we SKIP_INITIAL_REFRESH, since we have the Pokemon data in stored in db.
     * This prevents extra unnecessary calls to the Pokemon API.
     */
    override suspend fun initialize(): InitializeAction {
        return withContext(Dispatchers.IO) {
            val cacheTimeout = TimeUnit.MILLISECONDS.convert(1, TimeUnit.HOURS)
            val lastUpdate = local.dao.getLastUpdate()?.timestamp ?: 0
            if (System.currentTimeMillis() - lastUpdate <= cacheTimeout) {
                InitializeAction.SKIP_INITIAL_REFRESH
            } else {
                InitializeAction.LAUNCH_INITIAL_REFRESH
            }
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PokemonEntity>
    ): MediatorResult {
        return try {
            if (loadType == LoadType.PREPEND) {
                return MediatorResult.Success(endOfPaginationReached = true)
            }

            // NOTE: the Pokemon API doesn't let you query for a Pokemon name.
            // So, in order to support both Search and Pagination, I am retrieving ALL Pokemon
            // then loading into DB and then allowing the DB PagingSource to deal with local pagination from the DB.
            val response = remote.getPokemons(
                limit = 1500,
                offset = 0
            )

            local.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    local.dao.clearAll()
                }
                val pokemonEntity = response.results.map { it.toPokemonEntity() }
                local.dao.upsertAllPokemon(
                    pokemon = pokemonEntity,
                    timeStamp = System.currentTimeMillis()
                )
            }
            MediatorResult.Success(endOfPaginationReached = true)
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}