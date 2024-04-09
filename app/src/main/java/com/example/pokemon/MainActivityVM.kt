package com.example.pokemon

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.pokemon.data.mappers.toPokemon
import com.example.pokemon.data.remote.models.Pokemon
import com.example.pokemon.domain.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val LAST_SEARCH_QUERY_KEY: String = "last_search_query"
private const val DEFAULT_QUERY = ""

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MainActivityVM @Inject constructor(
    private val repository: PokemonRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val pagingData: Flow<PagingData<UIModel>>
    val state: StateFlow<UIState>
    val action: (UISearchAction) -> Unit

    init {
        val initialQuery: String = savedStateHandle[LAST_SEARCH_QUERY_KEY] ?: DEFAULT_QUERY
        val actionStateFlow = MutableSharedFlow<UISearchAction>()
        val searches = actionStateFlow
            .filterIsInstance<UISearchAction>()
            .distinctUntilChanged()
            .onStart { emit(UISearchAction(query = initialQuery)) }

        pagingData = searches
            .flatMapLatest { searchPokemon(query = it.query) }
            .cachedIn(viewModelScope)

        state = searches
            .map { search -> UIState(query = search.query) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                initialValue = UIState()
            )

        action = { action ->
            viewModelScope.launch { actionStateFlow.emit(action) }
        }
    }

    override fun onCleared() {
        // NOTE: if app process is killed and restarted, this will reset to last values
        savedStateHandle[LAST_SEARCH_QUERY_KEY] = state.value.query
        super.onCleared()
    }

    private fun searchPokemon(query: String): Flow<PagingData<UIModel>> {
        return repository.getPokemon(query)
            .map { pagingData ->
                pagingData.map {
                    UIModel(it.toPokemon())
                }
            }
    }
}

/**
 * Action performed by submitting a search query
 */
data class UISearchAction(val query: String)

/**
 * States representative of the UI
 * @param query searched term from inside search box
 * */
data class UIState(val query: String = DEFAULT_QUERY)

/** Data used to populate UI ViewHolder items in RecyclerView */
data class UIModel(val pokemon: Pokemon)