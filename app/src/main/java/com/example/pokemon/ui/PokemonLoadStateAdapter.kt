package com.example.pokemon.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pokemon.R

class PokemonLoadStateAdapter(
    private val retry: () -> Unit
) : LoadStateAdapter<PokemonLoadStateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): PokemonLoadStateViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pokemon_load_state, parent, false)
        return PokemonLoadStateViewHolder(view, retry)
    }

    override fun onBindViewHolder(holder: PokemonLoadStateViewHolder, loadState: LoadState) {
        if (loadState is LoadState.Error) {
            holder.errorMessage.text = loadState.error.localizedMessage
        }
        holder.progressBar.isVisible = loadState is LoadState.Loading
        holder.retryButton.isVisible = loadState is LoadState.Error
        holder.errorMessage.isVisible = loadState is LoadState.Error
    }
}

class PokemonLoadStateViewHolder(
    itemView: View,
    retry: () -> Unit
) : RecyclerView.ViewHolder(itemView) {
    val errorMessage: TextView = itemView.findViewById(R.id.pokemon_load_state_error_msg)
    val progressBar: ProgressBar = itemView.findViewById(R.id.pokemon_load_state_progress_bar)
    val retryButton: Button = itemView.findViewById(R.id.pokemon_load_state_retry_button)

    init {
        retryButton.setOnClickListener { retry.invoke() }
    }
}