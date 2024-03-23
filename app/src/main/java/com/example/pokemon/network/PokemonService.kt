package com.example.pokemon.network

import com.example.pokemon.network.models.PokemonsDTO
import retrofit2.http.GET

interface PokemonService {

    companion object {
        const val BASE_URL = "https://pokeapi.co/api/"
    }

    // TODO add query params for pagination i.e. limit and offset
    @GET("v2/pokemon")
    suspend fun getPokemons(): PokemonsDTO

}