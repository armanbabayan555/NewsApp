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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.text.font.FontWeight
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import newsapp.data.Status
import newsapp.model.NewsModel

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val viewModel: MainViewModel = MainViewModel()
                val newsResource by viewModel.newsResource.observeAsState()
                Scaffold(
                    topBar = {
                        SearchBar()
                    }
                ) {
                    when (newsResource?.status) {
                        Status.LOADING -> CircularProgressIndicator()
                        Status.OK -> NewsList(newsList = newsResource?.data)
                        Status.ERROR -> Text("Error loading news")
                        else -> {}
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBar() {
    var searchText by remember { mutableStateOf("") }
    TextField(
        value = searchText,
        onValueChange = { searchText = it },
        label = { Text("Search") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun NewsList(newsList: List<NewsModel>?) {
    LazyColumn {
        items(items = newsList ?: emptyList()) { news ->
            NewsItem(news = news)
        }
    }
}

@Composable
fun NewsItem(news: NewsModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(news.imageUrl),
            contentDescription = "News Image",
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
            Text(text = news.author, fontWeight = FontWeight.Normal, fontSize = 14.sp, color = Color.Gray)
            Text(text = news.title, fontWeight = FontWeight.Normal, fontSize = 14.sp)
        }
    }
}
