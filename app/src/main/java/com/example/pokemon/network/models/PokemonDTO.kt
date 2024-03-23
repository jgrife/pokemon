package com.example.pokemon.network.models

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
    val index: String get() = url.split('/').dropLast(1).last()
    val imageUrl: String get() = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${index}.png"
}