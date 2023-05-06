package newsapp.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.text.font.FontWeight
import coil.compose.rememberAsyncImagePainter
import newsapp.data.Status
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.gson.Gson
import newsapp.model.NewsModel
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import androidx.compose.foundation.lazy.rememberLazyListState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                val viewModel = MainViewModel()
                viewModel.loadNews()
                val newsResource by viewModel.newsResource.observeAsState()
                val isRefreshRequested = remember { mutableStateOf(false) }
                val currentCategory = remember { mutableStateOf("") }


                LaunchedEffect(isRefreshRequested.value, isRefreshRequested.value) {
                    if (isRefreshRequested.value) {
                        viewModel.loadNews(category = currentCategory.value)
                        isRefreshRequested.value = false
                    }
                }

                Scaffold(
                    topBar = {
                        SearchBar(
                            onFilterSelected = { filter ->
                                currentCategory.value = filter
                                viewModel.loadNews(category = filter)
                            },
                            onSearchClicked = { query ->
                                viewModel.loadNews(searchQuery = query)
                            },
                            onClearClicked = {
                                viewModel.loadNews()
                            }
                        )
                    }
                ) {
                    val navController = rememberNavController()
                    NavHost(navController, startDestination = Screen.NewsList.route) {
                        composable(Screen.NewsList.route) {
                            when (newsResource?.status) {
                                Status.LOADING -> CircularProgressIndicator()
                                Status.OK -> {
                                    val isRefreshRequested = remember { mutableStateOf(false) }
                                    NewsList(
                                        navController = navController,
                                        newsList = newsResource?.data,
                                        isRefreshRequested = isRefreshRequested
                                    )
                                    if (isRefreshRequested.value) {
                                        viewModel.refreshNews()
                                        isRefreshRequested.value = false
                                    }
                                }
                                Status.ERROR -> Text("Error loading news")
                                else -> {}
                            }
                        }
                        composable(
                            route = Screen.NewsDetails.route,
                            arguments = listOf(navArgument("news") { type = NavType.StringType })
                        ) { entry ->
                            val encodedNewsJson = entry.arguments?.getString("news")
                            if (encodedNewsJson != null) {
                                val decodedNewsJson = URLDecoder.decode(
                                    encodedNewsJson,
                                    StandardCharsets.UTF_8.toString()
                                )
                                val newsModel =
                                    Gson().fromJson(decodedNewsJson, NewsModel::class.java)
                                NewsDetails(news = newsModel)
                            }
                        }

                    }
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    onFilterSelected: (String) -> Unit,
    onSearchClicked: (String) -> Unit,
    onClearClicked: () -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text("Search") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { onSearchClicked(searchText) }) {
                Icon(Icons.Filled.Search, contentDescription = "Search")
            }
            IconButton(onClick = { onClearClicked(); searchText = "" }) {
                Icon(Icons.Filled.Clear, contentDescription = "Clear")
            }
        }
        FilterMenu(onFilterSelected = onFilterSelected)
    }
}


@Composable
fun FilterMenu(onFilterSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val categories = listOf("None", "Business", "Entertainment", "General", "Health")
    var selectedCategory by remember { mutableStateOf(categories[0]) }
    Box(modifier = Modifier.fillMaxWidth()) {
        TextButton(
            onClick = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Filters: $selectedCategory")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            categories.forEach { category ->
                DropdownMenuItem(onClick = {
                    expanded = false
                    selectedCategory = category
                    onFilterSelected(category.toLowerCase())
                }) {
                    Text(category)
                }
            }
        }
    }
}

@Composable
fun NewsList(navController: NavHostController, newsList: List<NewsModel>?, isRefreshRequested: MutableState<Boolean>) {
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshRequested.value)
    val lazyListState = rememberLazyListState()

    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = { isRefreshRequested.value = true },
    ) {
        if (newsList.isNullOrEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No Results Found")
            }
        } else {
            LazyColumn(state = lazyListState) {
                items(items = newsList) { news ->
                    NewsItem(navController, news = news)
                }
            }
        }
    }
}


@Composable
fun NewsItem(navController: NavHostController, news: NewsModel) {
    Card(
        border = BorderStroke(2.dp, Color.Gray),
        modifier = Modifier
            .padding(vertical = 6.dp, horizontal = 5.dp)
            .clickable {
                navController.navigate(Screen.NewsDetails.routeWithArgs(news))
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(news.imageUrl),
                contentDescription = "Image",
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.CenterVertically),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Text(text = news.source, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(
                    text = news.author,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = Color.Red
                )
                Text(text = news.title, fontWeight = FontWeight.Normal, fontSize = 14.sp)
            }
        }
    }
}

sealed class Screen(val route: String) {
    object NewsList : Screen("newsList")
    object NewsDetails : Screen("newsDetails/{news}") {
        fun routeWithArgs(news: NewsModel): String = "newsDetails/${news.toUrlEncodedString()}"
    }
}