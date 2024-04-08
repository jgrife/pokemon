package com.example.pokemon.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.pokemon.network.PokemonService
import com.example.pokemon.network.PokemonService.Companion.toResult
import com.example.pokemon.network.models.Pokemon
import com.example.pokemon.paging.PokemonPagingSource.PagingKey
import javax.inject.Inject

const val POKEMON_STARTING_OFFSET = 0
const val POKEMON_LIMIT = 20

class PokemonPagingSource @Inject constructor(private val service: PokemonService) : PagingSource<PagingKey, Pokemon>() {

    data class PagingKey(val limit: Int, val offset: Int) {
        /**
         * Get the previous or next key of the page.
         * For the Pokemon API, this key is derived from the offset +/- the limit
         */
        val nextKey get() = this.copy(offset = offset.plus(POKEMON_LIMIT))
        val prevKey get() = this.copy(offset = offset.minus(POKEMON_LIMIT))
    }

    private fun String?.getPagingKeyFromQueryParams(): PagingKey? {
        if (this == null) return null

        fun String.parseQueryParam(paramName: String): Int? {
            val paramValue = this.substringAfter("$paramName=")
            return paramValue.substringBefore("&").toIntOrNull()
        }

        val limit = this.parseQueryParam("limit")
        val offset = this.parseQueryParam("offset")

        return if (limit != null && offset != null) {
            PagingKey(limit, offset)
        } else {
            null
        }
    }

    override fun getRefreshKey(state: PagingState<PagingKey, Pokemon>): PagingKey? {
        return state.anchorPosition?.let { anchorPosition ->
            // To get the current PagingKey around the `anchorPosition`, we need to get either the preKey.nextKey
            // or vice-versa nextKey.prevKey
            state.closestPageToPosition(anchorPosition)?.prevKey?.nextKey
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.prevKey
        }
    }

    /**
     * Loads the data from service via the repository, ensuring that multiple requests aren't triggered at the same time.
     * Keeps an in-memory cache of the retrieved data.
     * Keeps track of the page to be requested.
     */
    override suspend fun load(params: LoadParams<PagingKey>): LoadResult<PagingKey, Pokemon> {
        return try {
            val key = params.key ?: PagingKey(POKEMON_LIMIT, POKEMON_STARTING_OFFSET)
            val response = service.getPokemons(
                limit = key.limit,
                offset = key.offset
            ).toResult()
            val data = response.getOrThrow()
            LoadResult.Page(
                data = data.results,
                prevKey = data.previous.getPagingKeyFromQueryParams(),
                nextKey = data.next.getPagingKeyFromQueryParams()
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}