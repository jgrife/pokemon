package com.example.pokemon

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.pokemon.PokemonDetailsActivity.Companion.POKEMON_ID_KEY
import com.example.pokemon.domain.Result
import com.example.pokemon.network.models.PokemonDTO
import com.example.pokemon.utils.ViewModelFactory
import com.squareup.picasso.Picasso
import javax.inject.Inject

class MainActivity : ComponentActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<MainActivityVM>

    private val viewModel: MainActivityVM by lazy {
        viewModelFactory.get<MainActivityVM>(this)
    }

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        PokemonApp.getAppComponent().inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.pokemon_recycler_view)
        recyclerView.adapter = PokemonAdapter { pokemonId ->
            val intent = Intent(applicationContext, PokemonDetailsActivity::class.java)
            intent.putExtra(POKEMON_ID_KEY, pokemonId)
            startActivity(intent)
        }

        setupObservers(recyclerView.adapter as PokemonAdapter)
    }

    private fun setupObservers(pokemonAdapter: PokemonAdapter) {
        viewModel.pokemonData.observe(this) { result ->
            if (result is Result.Success) {
                pokemonAdapter.setData(result.data.results)
            }
        }
    }

    class PokemonAdapter(
        private val onItemClick: (pokemonId: String) -> Unit
    ) : Adapter<PokemonViewHolder>() {

        private var pokemonList: List<PokemonDTO> = emptyList()

        @SuppressLint("NotifyDataSetChanged")
        fun setData(pokemonList: List<PokemonDTO>) {
            this.pokemonList = pokemonList
            // NOTE: could use something like DiffUtils to animate this list in here. Especially useful if we wanted to
            // update, remove, add pokemon to the list vs. using this heavy-handed `notifyDataSetChanged`, which will redraw
            // the whole list
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_pokemon, parent, false)
            return PokemonViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
            val pokemonData = pokemonList[position]
            holder.itemView.setOnClickListener { onItemClick(pokemonData.id) }
            Picasso.get()
                .load(pokemonData.imageUrl)
                .placeholder(R.drawable.image_placeholder)
                .into(holder.imageView)
            holder.nameView.text = pokemonData.name
            holder.idView.text = pokemonData.id
        }

        override fun getItemCount() = pokemonList.size
    }

    class PokemonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.pokemon_item_image_view)
        val nameView: TextView = itemView.findViewById(R.id.pokemon_item_name_view)
        val idView: TextView = itemView.findViewById(R.id.pokemon_item_id_view)
    }
}