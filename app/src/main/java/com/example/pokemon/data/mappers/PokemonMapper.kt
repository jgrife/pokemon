package com.example.pokemon.data.mappers

import com.example.pokemon.data.local.models.PokemonEntity
import com.example.pokemon.data.remote.models.Pokemon

fun Pokemon.toPokemonEntity(): PokemonEntity {
    return PokemonEntity(
        id = id.toInt(),
        name = name,
        url = url
    )
}

fun PokemonEntity.toPokemon(): Pokemon {
    return Pokemon(
        name = name,
        url = url
    )
}