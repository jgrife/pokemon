package com.example.pokemon.network

import com.example.pokemon.network.models.PokemonDetailsDTO
import com.example.pokemon.network.models.PokemonsDTO
import retrofit2.http.GET
import retrofit2.http.Path

interface PokemonService {

    companion object {
        const val BASE_URL = "https://pokeapi.co/api/"
    }

    // TODO add query params for pagination i.e. limit and offset
    @GET("v2/pokemon")
    suspend fun getPokemons(): PokemonsDTO

    @GET("v2/pokemon/{id}")
    suspend fun getPokemonDetails(
        @Path("id") pokemonId: String
    ): PokemonDetailsDTO

}