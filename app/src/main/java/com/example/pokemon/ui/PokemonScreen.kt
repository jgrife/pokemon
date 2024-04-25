package com.example.pokemon.ui

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.pokemon.R
import com.example.pokemon.UIModel
import com.example.pokemon.UISearchAction
import com.example.pokemon.UIState
import com.example.pokemon.data.remote.models.Pokemon
import com.example.pokemon.ui.theme.PokemonTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun PokemonScreen(
    pagingItems: LazyPagingItems<UIModel>,
    uiState: StateFlow<UIState>,
    uiAction: (UISearchAction) -> Unit,
    onItemClick: (pokemonId: String) -> Unit
) {
    val searchQuery by uiState.collectAsState()
    var localSearchQuery by rememberSaveable { mutableStateOf("") }
    val listState = rememberLazyListState()
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // runs any time the query has been updated by a search
    LaunchedEffect(key1 = searchQuery.query) {
        localSearchQuery = searchQuery.query
        // when a new search query is performed, we scroll to top of list to show new results
        listState.animateScrollToItem(0)
    }

    LaunchedEffect(key1 = pagingItems.loadState) {
        if (pagingItems.loadState.refresh is LoadState.Error) {
            Toast.makeText(
                context,
                "Error: ${(pagingItems.loadState.refresh as LoadState.Error).error.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        OutlinedTextField(
            modifier = Modifier.padding(16.dp),
            label = { Text(text = stringResource(id = R.string.search_hint)) },
            singleLine = true,
            value = localSearchQuery,
            onValueChange = { newQuery ->
                localSearchQuery = newQuery
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search,
                keyboardType = KeyboardType.Text
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    uiAction(UISearchAction(localSearchQuery))
                    keyboardController?.hide()
                }
            )
        )

        Box(modifier = Modifier.fillMaxSize()) {
            if (pagingItems.loadState.refresh is LoadState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(
                        count = pagingItems.itemCount,
                        key = { index ->
                            pagingItems[index]?.pokemon?.id ?: index
                        }) { index ->
                        pagingItems[index]?.run {
                            PokemonItem(
                                pokemon = pokemon,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onItemClick(pokemon.id) }
                            )
                        }
                    }
                    item {
                        if (pagingItems.loadState.append is LoadState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PokemonScreenPreview() {
    val previewData = listOf(
        UIModel(Pokemon("bulbasaur", "https://pokeapi.co/api/v2/pokemon/1/")),
        UIModel(Pokemon("ivysaur", "https://pokeapi.co/api/v2/pokemon/2/")),
        UIModel(Pokemon("venusaur", "https://pokeapi.co/api/v2/pokemon/3/")),
        // Add more fake data as needed
    )
    PokemonTheme {
        PokemonScreen(
            pagingItems = MutableStateFlow(PagingData.from(previewData)).collectAsLazyPagingItems(),
            uiState = MutableStateFlow(UIState(query = "Bul")),
            uiAction = {},
            onItemClick = {}
        )
    }
}