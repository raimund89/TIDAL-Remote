package net.rfrentrop.tidalremote

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.compose.MutableState
import androidx.compose.state
import androidx.ui.core.Alignment
import androidx.ui.core.ContentScale
import androidx.ui.core.Modifier
import androidx.ui.core.setContent
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Image
import androidx.ui.foundation.Text
import androidx.ui.graphics.Color
import androidx.ui.layout.*
import androidx.ui.layout.RowScope.weight
import androidx.ui.material.*
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Face
import androidx.ui.material.icons.filled.Favorite
import androidx.ui.material.icons.filled.Home
import androidx.ui.material.icons.filled.Search
import androidx.ui.res.imageResource
import androidx.ui.res.vectorResource
import androidx.ui.text.style.TextOverflow
import androidx.ui.unit.dp
import net.rfrentrop.tidalremote.screens.ScreenHome
import net.rfrentrop.tidalremote.ui.Screen
import net.rfrentrop.tidalremote.ui.TIDALRemoteTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val page = state { Screen.Home }

            TIDALRemoteTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {

                    Column(Modifier.fillMaxHeight()) {

                        MainContent(page)
                        Player()
                        AppBar(page)
                    }
                }
            }
        }
    }
}

@Composable
fun MainContent(page: MutableState<Screen>) {
    Column(modifier = Modifier.weight(1f, true)) {
        when(page.value) {
            Screen.Home -> ScreenHome()
            Screen.Videos -> Text("Videos")
            Screen.Search -> Text("Search")
            Screen.Collection -> Text("Collection")
            Screen.Album -> TODO()
            Screen.Artist -> TODO()
            Screen.Playlist -> TODO()
            Screen.Track -> TODO()
        }
    }
}

@Composable
fun Player() {
    Column {
        Divider(color = Color.DarkGray, thickness = 1.dp)
        Row(
            modifier = Modifier.height(70.dp),
            verticalGravity = Alignment.CenterVertically
        ) {
            Image(
                    asset = imageResource(R.drawable.emptycover),
                    modifier = Modifier.aspectRatio(1f),
                    contentScale = ContentScale.FillHeight
            )
            Column(
                modifier = Modifier.padding(start = 10.dp, end = 10.dp) + Modifier.weight(1f, true)
            ) {
                Text(
                    text = "Hotel California",
                    style = MaterialTheme.typography.body1,
                    color = Color.White,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Eagles",
                    style = MaterialTheme.typography.body2,
                    color = Color.LightGray
                )
            }

            IconButton(onClick = {
                TODO()
            }) {
                Icon(vectorResource(id = R.drawable.ic_play))
            }
            IconButton(onClick = {
                TODO()
            }) {
                Icon(vectorResource(id = R.drawable.ic_favorite_off))
            }
            Spacer(modifier = Modifier.width(10.dp))
        }
    }
}

@Composable
fun AppBar(page: MutableState<Screen>) {
    BottomAppBar(
            elevation = 2.dp,
            backgroundColor = MaterialTheme.colors.background
    ) {
        Spacer(modifier = Modifier.weight(1f, true))
        IconButton(onClick = { page.value = Screen.Home}) {
            Icon(Icons.Filled.Home, tint = if (page.value == Screen.Home) MaterialTheme.colors.secondary else Color.White)
        }
        Spacer(modifier = Modifier.weight(1f, true))
        IconButton(onClick = { page.value = Screen.Videos}) {
            Icon(Icons.Filled.Face, tint = if (page.value == Screen.Videos) MaterialTheme.colors.secondary else Color.White)
        }
        Spacer(modifier = Modifier.weight(1f, true))
        IconButton(onClick = { page.value = Screen.Search}) {
            Icon(Icons.Filled.Search, tint = if (page.value == Screen.Search) MaterialTheme.colors.secondary else Color.White)
        }
        Spacer(modifier = Modifier.weight(1f, true))
        IconButton(onClick = { page.value = Screen.Collection}) {
            Icon(Icons.Filled.Favorite, tint = if (page.value == Screen.Collection) MaterialTheme.colors.secondary else Color.White)
        }
        Spacer(modifier = Modifier.weight(1f, true))
    }
}
