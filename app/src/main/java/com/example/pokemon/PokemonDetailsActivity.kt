package com.example.pokemon

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import com.example.pokemon.ui.UIResult
import com.example.pokemon.data.remote.models.PokemonDetails
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PokemonDetailsActivity : ComponentActivity() {
    companion object {
        const val POKEMON_ID_KEY = "pokemonId"
    }

    private val viewModel by viewModels<PokemonDetailsVM>()

    private lateinit var heroImageView: ImageView
    private lateinit var nameView: TextView
    private lateinit var weightView: TextView
    private lateinit var heightView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pokemon_details)

        heroImageView = findViewById(R.id.pokemon_details_image_view)
        nameView = findViewById(R.id.pokemon_details_name_view)
        weightView = findViewById(R.id.pokemon_details_weight_view)
        heightView = findViewById(R.id.pokemon_details_height_view)

        viewModel.getPokemonDetails(intent.getStringExtra(POKEMON_ID_KEY))

        setupObservers()
    }

    private fun setupObservers() {
        viewModel.pokemonDetailsData.observe(this) { result ->
            if (result is UIResult.Success) {
                bindData(result.data)
            }
        }
    }

    private fun bindData(pokemonDetails: PokemonDetails) {
        Picasso.get()
            .load(pokemonDetails.imageUrl)
            .placeholder(R.drawable.image_placeholder)
            .into(heroImageView)
        nameView.text = pokemonDetails.name
        weightView.text = pokemonDetails.weight.toString()
        heightView.text = pokemonDetails.height.toString()
    }
}