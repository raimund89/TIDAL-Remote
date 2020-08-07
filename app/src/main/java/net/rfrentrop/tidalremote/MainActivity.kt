package net.rfrentrop.tidalremote

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.compose.MutableState
import androidx.compose.remember
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
import net.rfrentrop.tidalremote.screens.*
import net.rfrentrop.tidalremote.tidalapi.TidalManager
import net.rfrentrop.tidalremote.tidalapi.TidalUser
import net.rfrentrop.tidalremote.ui.Screen
import net.rfrentrop.tidalremote.ui.TIDALRemoteTheme

class MainActivity : AppCompatActivity() {

    private val backstack = java.util.Stack<Screen>()
    lateinit var page: MutableState<Screen>
    lateinit var manager: TidalManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        manager = TidalManager(this)
        val user = TidalUser()
        manager.init(user)
        manager.login()

        setContent {

            // The current page
            page = state { Screen.Home }
            // If the user is updated, update the content here
            val userstate = remember { user }

            TIDALRemoteTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {

                    Column(Modifier.fillMaxHeight()) {

                        MainContent(this@MainActivity, manager)
                        Player(this@MainActivity, userstate)
                        AppBar(this@MainActivity)
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        Log.d("MainActivity", "Back is pressed")
        if(backstack.size > 0) {
            page.value = backstack.pop()
            Log.d("MainActivity", "Next on the stack: ${page.value}")
        }
        else {
            super.onBackPressed()
        }
    }

    fun navigate(screen: Screen) {
        if(screen == Screen.Home || screen == Screen.Videos || screen == Screen.Search || screen == Screen.Collection)
            backstack.clear()
        else
            backstack.push(page.value)
        page.value = screen
    }

    fun getScreen(): Screen {
        return page.value
    }
}

@Composable
fun MainContent(activity: MainActivity, manager: TidalManager) {
    Column(modifier = Modifier.weight(1f, true)) {
        when(activity.getScreen()) {
            Screen.Home -> ScreenHome(activity)
            Screen.Videos -> ScreenVideos(activity)
            Screen.Search -> ScreenSearch(activity)
            Screen.Collection -> ScreenCollection(activity)
            Screen.Album -> TODO()
            Screen.Artist -> ScreenArtist(activity)
            Screen.Playlist -> TODO()
            Screen.Track -> TODO()
            Screen.Settings -> ScreenSettings(activity)
        }
    }
}

@Composable
fun Player(activity: MainActivity, user: TidalUser) {
    Column {
        // TODO: This doesn't work. The state is not updated
        Divider(color = if(user.loggedIn) Color.DarkGray else Color.Red, thickness = 1.dp)
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
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                )
                Text(
                        text = "Eagles",
                        style = MaterialTheme.typography.body2,
                        color = Color.LightGray,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
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
fun AppBar(activity: MainActivity) {
    BottomAppBar(
            elevation = 2.dp,
            backgroundColor = MaterialTheme.colors.background
    ) {
        Spacer(modifier = Modifier.weight(1f, true))
        IconButton(onClick = { activity.navigate(Screen.Home) }) {
            Icon(Icons.Filled.Home, tint = if (activity.getScreen() == Screen.Home) MaterialTheme.colors.secondary else Color.White)
        }
        Spacer(modifier = Modifier.weight(1f, true))
        IconButton(onClick = { activity.navigate(Screen.Videos) }) {
            Icon(Icons.Filled.Face, tint = if (activity.getScreen() == Screen.Videos) MaterialTheme.colors.secondary else Color.White)
        }
        Spacer(modifier = Modifier.weight(1f, true))
        IconButton(onClick = { activity.navigate(Screen.Search) }) {
            Icon(Icons.Filled.Search, tint = if (activity.getScreen() == Screen.Search) MaterialTheme.colors.secondary else Color.White)
        }
        Spacer(modifier = Modifier.weight(1f, true))
        IconButton(onClick = { activity.navigate(Screen.Collection) }) {
            Icon(Icons.Filled.Favorite, tint = if (activity.getScreen() == Screen.Collection) MaterialTheme.colors.secondary else Color.White)
        }
        Spacer(modifier = Modifier.weight(1f, true))
    }
}
