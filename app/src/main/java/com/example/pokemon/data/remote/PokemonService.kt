package com.example.pokemon.data.remote

import com.example.pokemon.data.remote.models.PokemonDetails
import com.example.pokemon.data.remote.models.Pokemons
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokemonService {

    companion object {
        const val BASE_URL = "https://pokeapi.co/api/"

        fun <T> Response<T>.toResult(): Result<T> {
            val body = this.body()
            return if (this.isSuccessful && body != null) {
                Result.success(body)
            } else {
                if (body == null) {
                    Result.failure(IllegalStateException("Response body was null"))
                } else {
                    Result.failure(IllegalStateException("${this.code()}"))
                }
            }
        }
    }

    // TODO add query params for pagination i.e. limit and offset
    @GET("v2/pokemon")
    suspend fun getPokemons(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
    ): Pokemons

    @GET("v2/pokemon/{id}")
    suspend fun getPokemonDetails(
        @Path("id") pokemonId: String
    ): Response<PokemonDetails>
}