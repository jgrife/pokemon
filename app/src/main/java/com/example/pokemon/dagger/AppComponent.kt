package com.example.pokemon.dagger

import com.example.pokemon.MainActivity
import com.example.pokemon.PokemonApp
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, NetworkModule::class])
interface AppComponent {

    @Component.Builder
    interface Builder {
        fun appModule(module: AppModule): Builder
        fun build(): AppComponent
    }

    fun inject(app: PokemonApp)
    fun inject(mainActivity: MainActivity)
}