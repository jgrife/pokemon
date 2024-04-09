package com.example.pokemon

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.pokemon.PokemonDetailsActivity.Companion.POKEMON_ID_KEY
import com.example.pokemon.utils.hideKeyboard
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainActivityVM>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindAdapter(
            list = findViewById(R.id.pokemon_recycler_view),
            search = findViewById(R.id.pokemon_search_view),
            uiState = viewModel.state,
            pagingData = viewModel.pagingData,
            uiAction = viewModel.action
        )
    }

    private fun bindAdapter(
        list: RecyclerView,
        search: EditText,
        uiState: StateFlow<UIState>,
        pagingData: Flow<PagingData<UIModel>>,
        uiAction: (UISearchAction) -> Unit
    ) {
        val adapter = PokemonAdapter { pokemonId ->
            val intent = Intent(applicationContext, PokemonDetailsActivity::class.java)
            intent.putExtra(POKEMON_ID_KEY, pokemonId)
            startActivity(intent)
        }
        list.adapter = adapter
        bindSearch(
            search = search,
            list = list,
            uiState = uiState,
            onQueryChanged = uiAction
        )
        bindList(
            adapter = adapter,
            pagingData = pagingData,
        )
    }

    private fun bindSearch(
        search: EditText,
        list: RecyclerView,
        uiState: StateFlow<UIState>,
        onQueryChanged: (UISearchAction) -> Unit
    ) {
        search.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                updateListFromInput(search, list, onQueryChanged)
                true
            } else {
                false
            }
        }
        search.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updateListFromInput(search, list, onQueryChanged)
                true
            } else {
                false
            }
        }

        lifecycleScope.launch {
            uiState
                .map { it.query }
                .distinctUntilChanged()
                .collect(search::setText)
        }
    }

    private fun bindList(
        adapter: PokemonAdapter,
        pagingData: Flow<PagingData<UIModel>>
    ) {
        lifecycleScope.launch {
            pagingData.collectLatest(adapter::submitData)
        }
    }

    private fun updateListFromInput(
        search: EditText,
        list: RecyclerView,
        onQueryChanged: (UISearchAction) -> Unit
    ) {
        search.text.trim().let {
            hideKeyboard()
            list.scrollToPosition(0)
            onQueryChanged(UISearchAction(query = it.toString()))
        }
    }

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
}