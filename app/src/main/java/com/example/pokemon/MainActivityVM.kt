package com.example.pokemon

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokemon.domain.PokemonRepository
import com.example.pokemon.ui.UIResult
import com.example.pokemon.network.models.Pokemons
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityVM @Inject constructor(private val repository: PokemonRepository) : ViewModel() {

    private val _pokemonData: MutableLiveData<UIResult<Pokemons>> = MutableLiveData(UIResult.Idle)
    val pokemonData: LiveData<UIResult<Pokemons>> = _pokemonData

    init {
        getPokemons()
    }

    private fun getPokemons() {
        _pokemonData.value = UIResult.Loading
        viewModelScope.launch {
            val pokemonData = repository.getPokemons()
            _pokemonData.postValue(pokemonData)
        }
    }
}