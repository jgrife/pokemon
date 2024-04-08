package com.example.pokemon.network.models

import org.junit.Assert.assertEquals
import org.junit.Test

class PokemonDTOTest {

    @Test
    fun getIndex() {
        val model = Pokemon("Charizard", "https://pokeapi.co/api/v2/ability/1/")
        assertEquals("1", model.id)
    }

    @Test
    fun getImageUrl() {
        val model = Pokemon("Charizard", "https://pokeapi.co/api/v2/ability/1/")
        assertEquals("http://pokeapi.co/media/sprites/pokemon/1.png", model.imageUrl)
    }
}