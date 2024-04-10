package com.example.pokemon.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.pokemon.data.local.models.LastUpdateEntity
import com.example.pokemon.data.local.models.PokemonEntity

@Dao
interface PokemonDao {

    @Transaction
    suspend fun upsertAllPokemon(pokemon: List<PokemonEntity>, timeStamp: Long) {
        upsertAllPokemon(pokemon)
        upsertLastUpdate(LastUpdateEntity(timestamp = timeStamp))
    }

    @Upsert
    suspend fun upsertAllPokemon(pokemon: List<PokemonEntity>)

    @Query("SELECT * FROM pokemonentity WHERE LOWER(name) LIKE LOWER(:query)")
    fun pagingSource(query: String): PagingSource<Int, PokemonEntity>

    @Upsert
    suspend fun upsertLastUpdate(lastUpdate: LastUpdateEntity)

    @Query("SELECT * FROM lastupdateentity WHERE id = 0")
    fun getLastUpdate(): LastUpdateEntity?

    @Transaction
    suspend fun clearAll() {
        clearPokemonEntity()
        clearLastUpdateEntity()
    }

    @Query("DELETE FROM pokemonentity")
    suspend fun clearPokemonEntity()

    @Query("DELETE FROM lastupdateentity")
    suspend fun clearLastUpdateEntity()
}