package com.example.pokemon

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokemon.domain.PokemonRepository
import com.example.pokemon.domain.Result
import com.example.pokemon.network.models.PokemonDetailsDTO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonDetailsVM @Inject constructor(private val repository: PokemonRepository) : ViewModel() {

    private val _pokemonDetailsData: MutableLiveData<Result<PokemonDetailsDTO>> = MutableLiveData(Result.Idle)
    val pokemonDetailsData: LiveData<Result<PokemonDetailsDTO>> = _pokemonDetailsData

    fun getPokemonDetails(pokemonId: String?) {
        _pokemonDetailsData.value = Result.Loading
        viewModelScope.launch {
            val pokemonData = repository.getPokemonDetails(pokemonId)
            _pokemonDetailsData.postValue(pokemonData)
        }
    }
}