package com.example.pokemon.network

import com.example.pokemon.network.models.PokemonsDTO
import retrofit2.http.GET

interface PokemonService {
    @GET("v2/pokemon")
    suspend fun getPokemons(): PokemonsDTO

}