package com.example.starwars

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.starwars.data.Person
import com.example.starwars.data.StarWarsService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PersonViewModel : ViewModel() {
    val person = MutableLiveData<Person>()
    private val service = StarWarsService()

    fun fetch(id: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            person.postValue(service.getPerson(id))
        }
    }
}