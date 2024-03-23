package com.example.pokemon.network.models

import com.google.gson.annotations.SerializedName

data class PokemonsDTO(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<PokemonDTO>,
)

data class PokemonDTO(
    val name: String,
    val url: String
) {
    val id: String get() = url.split('/').dropLast(1).last()
    val imageUrl: String get() = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${id}.png"
}

data class PokemonDetailsDTO(
    val name: String,
    val id: Int,
    val height: Int,
    val weight: Int,
    val sprites: SpritesDTO,
    val states: List<StatDTO>,
    val types: List<TypeDTO>
) {
    val imageUrl: String get() = sprites.frontImageUrl
}

data class SpritesDTO(
    @SerializedName("front_default")
    val frontImageUrl: String
)

data class StatDTO(
    val stats: StatDetailsDTO
)

data class StatDetailsDTO(
    val name: String,
    val url: String
)

data class TypeDTO(
    val types: TypeDetailsDTO
)

data class TypeDetailsDTO(
    val name: String,
    val url: String
)