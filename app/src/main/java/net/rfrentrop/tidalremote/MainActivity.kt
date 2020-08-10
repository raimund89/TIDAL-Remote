package net.rfrentrop.tidalremote

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.compose.mutableStateMapOf
import androidx.compose.mutableStateOf
import androidx.compose.remember
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
import net.rfrentrop.tidalremote.player.PlayerHost
import net.rfrentrop.tidalremote.screens.*
import net.rfrentrop.tidalremote.tidalapi.TidalManager
import net.rfrentrop.tidalremote.tidalapi.TidalUser
import net.rfrentrop.tidalremote.ui.Screen
import net.rfrentrop.tidalremote.ui.TIDALRemoteTheme

// TODO: add TidalManager to the onPause and onResume functions??

class MainActivity : AppCompatActivity() {

    companion object {
        const val SERVICE_TYPE = "_tidalplayer._tcp."
    }

    private val backstack = java.util.Stack<Screen>()
    var manager = TidalManager(this)

    var discoveryActive = false
    lateinit var nsdManager: NsdManager

    var page = mutableStateOf(Screen.Home)
    var players = mutableStateMapOf<String, PlayerHost>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        nsdManager = getSystemService(Context.NSD_SERVICE) as NsdManager

        val user = TidalUser()
        manager.init(user)
        manager.login()

        setContent {

            // If the user is updated, update the content here
            val userstate = remember { user }

            TIDALRemoteTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {

                    Column(Modifier.fillMaxHeight()) {

                        MainContent(this@MainActivity)
                        Player(this@MainActivity)
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

    override fun onResume() {
        super.onResume()

        Log.d("MainActivity", "Resuming")

        Handler(Looper.myLooper()!!).postDelayed({
            nsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener)
        }, 1000)
    }

    override fun onPause() {
        super.onPause()

        if(discoveryActive)
            nsdManager.stopServiceDiscovery(discoveryListener)
        discoveryActive = false
    }

    private val discoveryListener = object : NsdManager.DiscoveryListener {

        // Called as soon as service discovery begins.
        override fun onDiscoveryStarted(regType: String) {
            discoveryActive = true
        }

        override fun onServiceFound(service: NsdServiceInfo) {
            if(service.serviceType == SERVICE_TYPE)
                nsdManager.resolveService(service, resolveListener)
        }

        override fun onServiceLost(service: NsdServiceInfo) {
            if(players.containsKey(service.serviceName)) {
                players.remove(service.serviceName)
            }
        }

        override fun onDiscoveryStopped(serviceType: String) {
        }

        override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
            if(discoveryActive)
                nsdManager.stopServiceDiscovery(this)
            discoveryActive = false
        }

        override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
            if(discoveryActive)
                nsdManager.stopServiceDiscovery(this)
            discoveryActive = false
        }
    }

    private val resolveListener = object : NsdManager.ResolveListener {

        override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
        }

        override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
            // If the player already exists, update it. If not, insert it.
            players[serviceInfo.serviceName] = PlayerHost(serviceInfo.host,
                serviceInfo.port,
                serviceInfo.attributes["name"]!!.decodeToString(),
                serviceInfo.attributes["version"]!!.decodeToString())
        }
    }
}

@Composable
fun MainContent(activity: MainActivity) {
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
            Screen.Mix -> TODO()
            Screen.Settings -> ScreenSettings(activity)
        }
    }
}

@Composable
fun Player(activity: MainActivity) {
    Log.d("Player", "Redrawing")
    activity.players.forEach { s, playerHost ->
        Log.d("Player", "Name: $s")
    }

    Column {
        // TODO: This doesn't work. The state is not updated
        Divider(color = if(activity.manager.user.loggedIn) Color.DarkGray else Color.Red, thickness = 1.dp)
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
