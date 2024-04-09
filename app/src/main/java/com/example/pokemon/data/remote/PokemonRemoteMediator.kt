package com.example.pokemon.data.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.pokemon.data.local.PokemonDatabase
import com.example.pokemon.data.local.models.PokemonEntity
import com.example.pokemon.data.mappers.toPokemonEntity
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class PokemonRemoteMediator(
    private val local: PokemonDatabase,
    private val remote: PokemonService
) : RemoteMediator<Int, PokemonEntity>() {

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
//          //
            // The downside to this is that everytime the UI performs a Search (even a previously executed search)
            // We are making another request to the Pokemon API for ALL Pokemon.
            //
            // Might consider skipping the network request, if we already have the data in database and
            // the data isn't considered stale.
            val response = remote.getPokemons(
                limit = 1500,
                offset = 0
            )

            local.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    local.dao.clearAll()
                }
                val pokemonEntity = response.results.map { it.toPokemonEntity() }
                local.dao.upsertAll(pokemonEntity)
            }
            MediatorResult.Success(endOfPaginationReached = true)
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}