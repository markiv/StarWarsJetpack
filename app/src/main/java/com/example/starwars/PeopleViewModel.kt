package com.example.starwars

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.starwars.data.Film
import com.example.starwars.data.Person
import com.example.starwars.data.StarWarsService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

class PersonViewModel : ViewModel() {
    val person = MutableLiveData<Person>()
    private val _films = MutableStateFlow<List<Film>>(emptyList())
    val films = _films.asStateFlow()

    private val service = StarWarsService()

    fun fetch(id: Int) {
        viewModelScope.launch {
            val _person = service.getPerson(id)
            if (_person != null) {
                person.postValue(_person)

                // Fetch all the films
                _films.value = emptyList()
                _person.films.forEach {
                    val film = service.getFilm(it.toHttpUrlOrNull()!!)
                    if (film != null) {
                        _films.value += film
                        Log.d("PersonViewModel", "Film: ${film.title}")
                    }
                }
            }
        }
    }
}