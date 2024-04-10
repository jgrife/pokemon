package com.example.pokemon

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.RecyclerView
import com.example.pokemon.PokemonDetailsActivity.Companion.POKEMON_ID_KEY
import com.example.pokemon.ui.PokemonAdapter
import com.example.pokemon.ui.PokemonLoadStateAdapter
import com.example.pokemon.utils.hideKeyboard
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
        val header = PokemonLoadStateAdapter(adapter::retry)
        list.adapter = adapter.withLoadStateHeader(header = header)

        lifecycleScope.launch {
            adapter.loadStateFlow.collect { loadState ->
                // Show a retry header if there was an error refreshing
                // Show a spinner header if it's the first time we are Loading data (which would be over the network)
                // OR default to the default prepend state
                header.loadState = loadState.mediator
                    ?.refresh
                    ?.takeIf {
                        it is LoadState.Loading && adapter.itemCount == 0 ||
                        it is LoadState.Error && adapter.itemCount == 0
                    }
                    ?: loadState.prepend
            }
        }

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
}