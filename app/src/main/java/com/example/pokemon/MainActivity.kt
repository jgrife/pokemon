package com.example.pokemon

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.pokemon.PokemonDetailsActivity.Companion.POKEMON_ID_KEY
import com.example.pokemon.ui.PokemonScreen
import com.example.pokemon.ui.theme.PokemonTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainActivityVM>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PokemonTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PokemonScreen(
                        pagingItems = viewModel.pagingData.collectAsLazyPagingItems(),
                        uiState = viewModel.state,
                        uiAction = viewModel.action,
                        onItemClick = { pokemonId ->
                            val intent = Intent(applicationContext, PokemonDetailsActivity::class.java)
                            intent.putExtra(POKEMON_ID_KEY, pokemonId)
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}