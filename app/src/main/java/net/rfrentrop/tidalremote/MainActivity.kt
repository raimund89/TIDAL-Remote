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
import androidx.ui.foundation.clickable
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
import net.rfrentrop.tidalremote.player.PlayerManager
import net.rfrentrop.tidalremote.screens.*
import net.rfrentrop.tidalremote.theme.TIDALRemoteTheme
import net.rfrentrop.tidalremote.tidalapi.TidalManager
import net.rfrentrop.tidalremote.tidalapi.TidalUser
import net.rfrentrop.tidalremote.ui.BackstackItem
import net.rfrentrop.tidalremote.ui.PageType
import net.rfrentrop.tidalremote.ui.Screen
import org.json.JSONObject
import java.net.InetAddress

// TODO: add TidalManager to the onPause and onResume functions??

class MainActivity : AppCompatActivity() {

    companion object {
        const val SERVICE_TYPE = "_tidalplayer._tcp."
    }

    // Backend control variables: the navigation backstack, and managers for the
    // connection with Tidal and the current player (if any)
    private val backstack = java.util.Stack<BackstackItem>()
    var manager = TidalManager(this)
    val player = PlayerManager(this)

    // The network service discovery manager
    lateinit var nsdManager: NsdManager

    // States that should trigger a UI update
    var currentPage = mutableStateOf(BackstackItem(Screen.Home, PageType.NONE, ""))
    var refresher: (() -> Unit)? = null
    var playerList = mutableStateMapOf<String, PlayerHost>()

    var currentPlayer = mutableStateOf(PlayerHost(InetAddress.getLoopbackAddress(), 22, "<No Player>", "0.0"))
    var currentTrack = mutableStateOf(JSONObject())

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
        if(backstack.size > 0) {
            val item = backstack.pop()

            manager.setPage(item.type, item.id)

            if(currentPage.value.page == item.page)
                refresher?.let { it() }

            currentPage.value = item
        }
        else {
            super.onBackPressed()
        }
    }

    fun navigate(screen: Screen, type: PageType = PageType.NONE, id: String = "") {
        if(screen == Screen.Home || screen == Screen.Videos || screen == Screen.Search || screen == Screen.Collection)
            backstack.clear()
        else
            backstack.push(currentPage.value)

        manager.setPage(type, id)

        if(currentPage.value.page == screen)
            refresher?.let { it() }

        currentPage.value = BackstackItem(screen, type, id)
    }

    fun getScreen(): Screen {
        return currentPage.value.page
    }

    override fun onResume() {
        super.onResume()

        Handler(Looper.myLooper()!!).postDelayed({
            nsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener)
        }, 1000)

        player.reconnectToPlayer()
    }

    override fun onPause() {
        super.onPause()

        nsdManager.stopServiceDiscovery(discoveryListener)

        player.disconnectFromPlayer()
    }

    private val discoveryListener = object : NsdManager.DiscoveryListener {

        // Called as soon as service discovery begins.
        override fun onDiscoveryStarted(regType: String) {
        }

        override fun onServiceFound(service: NsdServiceInfo) {
            if(service.serviceType == SERVICE_TYPE)
                nsdManager.resolveService(service, resolveListener)
        }

        override fun onServiceLost(service: NsdServiceInfo) {
            if(playerList.containsKey(service.serviceName)) {
                playerList.remove(service.serviceName)
            }
        }

        override fun onDiscoveryStopped(serviceType: String) {
        }

        override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
            nsdManager.stopServiceDiscovery(this)
        }

        override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
            nsdManager.stopServiceDiscovery(this)
        }
    }

    private val resolveListener = object : NsdManager.ResolveListener {

        override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
        }

        override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
            Log.e("MainActivity", "Resolved host")
            // If the player already exists, update it. If not, insert it.
            playerList[serviceInfo.serviceName] = PlayerHost(serviceInfo.host,
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
            Screen.Page -> ScreenPage(activity)
            Screen.Settings -> ScreenSettings(activity)
            Screen.Playlist -> ScreenPlaylist(activity)
            Screen.Player -> ScreenPlayer(activity)
        }
    }
}

@Composable
fun Player(activity: MainActivity) {
    if(activity.currentPage.value.page != Screen.Player) {
        Column(
                modifier = Modifier.clickable(onClick = {
                    activity.navigate(Screen.Player)
                })
        ) {
            // TODO: This doesn't work. The state is not updated
            Divider(color = if (activity.manager.user.loggedIn) Color.DarkGray else Color.Red, thickness = 1.dp)
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
