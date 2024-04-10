package com.example.pokemon.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.pokemon.R
import com.example.pokemon.UIModel
import com.squareup.picasso.Picasso

class PokemonAdapter(
    private val onItemClick: (pokemonId: String) -> Unit
) : PagingDataAdapter<UIModel, PokemonViewHolder>(DIFF_UTIL_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_pokemon, parent, false)
        return PokemonViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        val pokemonData = getItem(position)?.pokemon ?: return
        holder.itemView.setOnClickListener { onItemClick(pokemonData.id) }
        Picasso.get()
            .load(pokemonData.imageUrl)
            .placeholder(R.drawable.image_placeholder)
            .into(holder.imageView)
        holder.nameView.text = pokemonData.capitalizeName
        holder.idView.text = "ID: ${pokemonData.id}"
    }

    companion object {
        private val DIFF_UTIL_COMPARATOR = object : DiffUtil.ItemCallback<UIModel>() {
            override fun areItemsTheSame(oldItem: UIModel, newItem: UIModel): Boolean {
                return oldItem.pokemon.id == newItem.pokemon.id
            }

            override fun areContentsTheSame(oldItem: UIModel, newItem: UIModel): Boolean =
                oldItem == newItem
        }
    }
}

class PokemonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val imageView: ImageView = itemView.findViewById(R.id.pokemon_item_image_view)
    val nameView: TextView = itemView.findViewById(R.id.pokemon_item_name_view)
    val idView: TextView = itemView.findViewById(R.id.pokemon_item_id_view)
}
