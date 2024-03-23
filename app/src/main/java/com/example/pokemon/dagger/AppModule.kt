package com.example.pokemon.dagger

import android.app.Application
import com.example.pokemon.PokemonApp
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule constructor(private val appContext: PokemonApp) {
    @Provides
    @Singleton
    fun getAppContext(): Application {
        return appContext
    }
}