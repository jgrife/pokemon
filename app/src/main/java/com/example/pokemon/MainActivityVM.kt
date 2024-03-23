package com.example.pokemon

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokemon.domain.PokemonRepository
import com.example.pokemon.domain.Result
import com.example.pokemon.network.models.PokemonsDTO
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivityVM @Inject constructor(private val repository: PokemonRepository) : ViewModel() {

    private val _pokemonData: MutableLiveData<Result<PokemonsDTO>> = MutableLiveData()
    val pokemonData: LiveData<Result<PokemonsDTO>> = _pokemonData

    init {
        getPokemons()
    }

    fun getPokemons() {
        _pokemonData.value = Result.Loading
        viewModelScope.launch {
            val pokemonData = repository.getPokemons()
            _pokemonData.postValue(pokemonData)
        }
    }
}