package com.example.starwars

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.starwars.data.Film
import com.example.starwars.data.Person
import com.example.starwars.data.StarWarsService
import com.example.starwars.ui.theme.StarWarsTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StarWarsTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                titleContentColor = MaterialTheme.colorScheme.primary,
                            ),
                            title = { Text("Star Wars", fontWeight = FontWeight.Bold) }
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    val navController = rememberNavController()
                    NavigationGraph(navController, modifier = Modifier.padding(innerPadding))
//                    FilmDetails(
//                        1, modifier = Modifier.padding(innerPadding)
//                    )
//                    PersonDetails(
//                        2, modifier = Modifier.padding(innerPadding)
//                    )
                }
            }
        }
    }
}

sealed class Screen(val route: String) {
    object Film : Screen("film")
    object Person : Screen("person")
}

@Composable
fun NavigationGraph(navController: NavHostController, modifier: Modifier) {
    NavHost(navController, startDestination = Screen.Person.route) {
        composable(Screen.Film.route) { FilmDetails(1) }
        composable(Screen.Person.route) { PersonDetails(1) }
    }
}

@Composable
fun FilmDetails(id: Int, modifier: Modifier = Modifier) {
    val scope = CoroutineScope(Dispatchers.Default)
    val service = StarWarsService()
    var film by remember { mutableStateOf<Film?>(null) }

    LaunchedEffect(id) {
        scope.launch {
            film = service.getFilm(id)
        }
    }

    if (film == null) {
        Text("Loading...")
    } else {
        val it = film!!
        Column(modifier = modifier) {
            Text(text = it.title)
            Text(text = it.opening_crawl)
        }
    }
}

@Composable
fun PersonDetails(id: Int, modifier: Modifier = Modifier) {
    val scope = CoroutineScope(Dispatchers.Default)
    val service = StarWarsService()
    var person by remember { mutableStateOf<Person?>(null) }

    LaunchedEffect(id) {
        scope.launch {
            person = service.getPerson(id)
        }
    }

    if (person != null) {
        val it = person!!
        Column(
            modifier = modifier.verticalScroll(rememberScrollState())
        ) {
            Detail("Name", it.name)
            Detail("Gender", it.gender)
            Detail("Eye Color", it.eye_color)
            Detail("Hair Color", it.hair_color)
            Detail("Skin Color", it.skin_color)
            Detail("Height", it.height)
            Detail("Home World", it.homeworld.toString())
            Detail("Films", it.films.toString())
            Detail("Species", it.species.toString())
        }
    } else {
        Text(text = "Loading...")
    }
}

@Composable
fun Detail(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(5.dp)
        ) {
            Text(text = label, style = MaterialTheme.typography.labelMedium)
            Spacer(Modifier.width(10.dp))
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = value,
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        HorizontalDivider()
    }
}

@Preview(showBackground = true)
@Composable
fun FirstPreview() {
    StarWarsTheme {
//        PersonDetails(1)
        Detail(
            "Name",
            "Luke Skywalker Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam..."
        )

    }
}