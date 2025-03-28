package com.example.starwars.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Cache
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url
import java.io.File

interface StarWarsApi {
    @GET("people/{id}")
    suspend fun getPerson(@Path("id") id: Int): Person

    @GET("people")
    suspend fun getPersons(): List<Person>

    @GET("films/{id}")
    suspend fun getFilm(@Path("id") id: Int): Film

    @GET("films")
    suspend fun getFilms(): List<Film>

    @GET
    suspend fun getFilm(@Url url: HttpUrl): Film

    @GET
    suspend fun getPerson(@Url url: HttpUrl): Person
}

inline fun <T> tryOrNull(expression: () -> T): T? {
    return try {
        expression()
    } catch (e: Throwable) {
        Log.e("StarWarsService", "Error", e)
        null
    }
}

class StarWarsService {
    private val cache = Cache(directory = File("cache"), maxSize = 10L * 1024 * 1024)
    private val client = OkHttpClient.Builder()
        .cache(cache)
//        .addNetworkInterceptor { response ->
//            Log.d("OkHttp", "Network interceptor called.")
//            response.proceed(response.request())
//        }
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .header("Cache-Control", "public, max-stale=" + 60 * 60 * 24 * 7)
                .build()
            chain.proceed(request)
        }.build()
    val retrofit = Retrofit.Builder().baseUrl("https://swapi.dev/api/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create()).build()

    val api = retrofit.create(StarWarsApi::class.java)

    suspend fun getPerson(id: Int): Person? = tryOrNull {
        withContext(Dispatchers.IO) {
            api.getPerson(id)
        }
    }

    suspend fun getFilm(id: Int): Film? = tryOrNull {
        withContext(Dispatchers.IO) {
            api.getFilm(id)
        }
    }

    suspend fun getFilm(url: HttpUrl): Film? = tryOrNull {
        withContext(Dispatchers.IO) {
            api.getFilm(url)
        }
    }
}
