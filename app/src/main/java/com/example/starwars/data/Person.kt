package com.example.starwars.data

import java.net.URL

// swapi.dev
data class Person(
    val birth_year: String,
    val created: String,
    val edited: String,
    val eye_color: String,
    val films: List<URL>,
    val gender: String,
    val hair_color: String,
    val height: String,
    val homeworld: URL,
    val mass: String,
    val name: String,
    val skin_color: String,
    val species: List<URL>,
    val starships: List<URL>,
    val url: URL,
    val vehicles: List<URL>
)
