package com.example.pokemon.network.models

import com.google.gson.annotations.SerializedName

data class Pokemons(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<Pokemon>,
)

data class Pokemon(
    val name: String,
    val url: String
) {
    val id: String get() = url.split('/').dropLast(1).last()
    val imageUrl: String get() = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${id}.png"
}

data class PokemonDetails(
    val name: String,
    val id: Int,
    val height: Int,
    val weight: Int,
    val sprites: Sprites,
    val states: List<Stat>,
    val types: List<Type>
) {
    val imageUrl: String get() = sprites.frontImageUrl
}

data class Sprites(
    @SerializedName("front_default")
    val frontImageUrl: String
)

data class Stat(
    val stats: StatDetails
)

data class StatDetails(
    val name: String,
    val url: String
)

data class Type(
    val types: TypeDetails
)

data class TypeDetails(
    val name: String,
    val url: String
)