package com.example.starwars

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
import androidx.compose.runtime.livedata.observeAsState
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
import com.example.starwars.data.StarWarsService
import com.example.starwars.ui.theme.StarWarsTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.getValue

class MainActivity : ComponentActivity() {
    private val personViewModel by viewModels<PersonViewModel>()

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
                    NavigationGraph(
                        navController,
                        personViewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
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
fun NavigationGraph(
    navController: NavHostController,
    personViewModel: PersonViewModel,
    modifier: Modifier,
) {
    NavHost(navController, startDestination = Screen.Person.route + "/2") {
        composable(Screen.Film.route) { FilmDetails(navController, 1, modifier) }
        composable(Screen.Person.route + "/{id}") {
            PersonDetails(
                navController,
                personViewModel,
                it.arguments?.getString("id")?.toInt() ?: 1,
                modifier
            )
        }
    }
}

@Composable
fun FilmDetails(navController: NavHostController, id: Int, modifier: Modifier = Modifier) {
    val scope = CoroutineScope(Dispatchers.Default)
    val service = StarWarsService()
    var film by remember { mutableStateOf<Film?>(null) }

    LaunchedEffect(id) {
        scope.launch {
            film = service.getFilm(id)
        }
    }

    if (film != null) {
        val film = film!!
        Column(modifier = modifier) {
            Text(text = film.title)
            Text(text = film.opening_crawl)
        }
    } else {
        Text("Loading...")
    }
}

@Composable
fun PersonDetails(
    navController: NavHostController,
    viewModel: PersonViewModel,
    id: Int,
    modifier: Modifier = Modifier,
) {
    val person by viewModel.person.observeAsState()

    LaunchedEffect(id) {
        viewModel.fetch(id)
    }

    if (person != null) {
        Column(
            modifier = modifier.verticalScroll(rememberScrollState())
        ) {
            with(person!!) {
                Detail("Name", name)
                Detail("Gender", gender)
                Detail("Eye Color", eye_color)
                Detail("Hair Color", hair_color)
                Detail("Skin Color", skin_color)
                Detail("Height", height)
                Detail("Home World", homeworld.toString())
                Detail("Films", films.toString())
                Detail("Species", species.toString())
            }

            Button(onClick = {
                navController.navigate(Screen.Film.route)
            }) {

                Text(text = "Navigate to Screen 1")
            }
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