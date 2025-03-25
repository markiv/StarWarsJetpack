package com.example.starwars

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import com.example.starwars.data.Film
import com.example.starwars.data.Person
import com.example.starwars.data.StarWarsService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

class PersonViewModel : ViewModel() {
    val person = MutableLiveData<Person>()
    val films = MutableLiveData<List<Film>>()
    private val service = StarWarsService()

    fun fetch(id: Int) {
        films.value = emptyList<Film>()

        CoroutineScope(Dispatchers.IO).launch {
            val p = service.getPerson(id)
            person.postValue(p)

            val f = mutableListOf<Film>()

            p?.films?.forEach {
                val film = service.getFilm(it.toHttpUrlOrNull()!!)
                if (film != null) {
                    f.add(film)
                    Log.d("PersonViewModel", "Film: ${film.title}")
                }
                films.postValue(f)
            }
        }
    }
}