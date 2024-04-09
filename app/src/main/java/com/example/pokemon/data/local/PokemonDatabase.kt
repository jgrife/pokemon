package com.example.pokemon.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.pokemon.data.local.models.PokemonEntity

@Database(
    entities = [PokemonEntity::class],
    version = 1
)
abstract class PokemonDatabase: RoomDatabase() {
    abstract val dao: PokemonDao
}