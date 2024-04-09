package com.example.pokemon.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.pokemon.data.local.models.PokemonEntity

@Dao
interface PokemonDao {

    @Upsert
    suspend fun upsertAll(pokemon: List<PokemonEntity>)

    @Query("SELECT * FROM pokemonentity WHERE LOWER(name) LIKE LOWER(:query)")
    fun pagingSource(query: String): PagingSource<Int, PokemonEntity>

    @Query("DELETE FROM pokemonentity")
    suspend fun clearAll()
}