package com.example.pokemon

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokemon.domain.PokemonRepository
import com.example.pokemon.ui.UIResult
import com.example.pokemon.data.remote.models.PokemonDetails
import com.example.pokemon.ui.toUIResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonDetailsVM @Inject constructor(private val repository: PokemonRepository) : ViewModel() {

    private val _pokemonDetailsData: MutableLiveData<UIResult<PokemonDetails>> = MutableLiveData(UIResult.Idle)
    val pokemonDetailsData: LiveData<UIResult<PokemonDetails>> = _pokemonDetailsData

    fun getPokemonDetails(pokemonId: String?) {
        _pokemonDetailsData.value = UIResult.Loading
        viewModelScope.launch {
            val pokemonData = repository.getPokemonDetails(pokemonId)
            _pokemonDetailsData.postValue(pokemonData.toUIResult())
        }
    }
}