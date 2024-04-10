package com.example.pokemon.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LastUpdateEntity(
    @PrimaryKey
    val id: Int = 0,
    val timestamp: Long
)