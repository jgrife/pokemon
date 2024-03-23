package com.example.pokemon

import android.app.Application
import com.example.pokemon.dagger.AppComponent
import com.example.pokemon.dagger.AppModule
import com.example.pokemon.dagger.DaggerAppComponent

class PokemonApp : Application() {
    companion object {
        private lateinit var appComponent: AppComponent

        fun getAppComponent() = appComponent
    }

    override fun onCreate() {
        super.onCreate()
        initDaggerAppComponent()
    }

    private fun initDaggerAppComponent() {
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }
}