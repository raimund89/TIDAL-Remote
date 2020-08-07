package net.rfrentrop.tidalremote.screens

import android.os.Handler
import android.os.Looper
import androidx.compose.Composable
import androidx.compose.state
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.*
import androidx.ui.foundation.lazy.LazyColumnItems
import androidx.ui.foundation.lazy.LazyRowItems
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.Color
import androidx.ui.input.TextFieldValue
import androidx.ui.layout.*
import androidx.ui.material.CircularProgressIndicator
import androidx.ui.material.IconButton
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Surface
import androidx.ui.res.vectorResource
import androidx.ui.savedinstancestate.savedInstanceState
import androidx.ui.unit.dp
import net.rfrentrop.tidalremote.MainActivity
import net.rfrentrop.tidalremote.R
import net.rfrentrop.tidalremote.tidalapi.TidalManager
import net.rfrentrop.tidalremote.ui.*
import org.json.JSONArray
import org.json.JSONObject

// TODO: Scrolling of search results is still not very smooth

@Composable
fun ScreenSearch(activity: MainActivity, manager: TidalManager) {

    val searchResult = state { JSONObject() }
    var lastSearch = 0L
    val delayedSearch = Handler(Looper.myLooper()!!)

    manager.getExplore(searchResult)

    Column(
            modifier = Modifier.padding(10.dp)
    ) {
        val searchval = savedInstanceState(saver = TextFieldValue.Saver) { TextFieldValue() }

        Surface(color = Color.White) {
            Row {
                Icon(
                        modifier = Modifier.gravity(Alignment.CenterVertically) + Modifier.padding(start=10.dp),
                        asset = vectorResource(id = R.drawable.ic_search),
                        tint = Color.Black
                )
                TextField(
                        modifier = Modifier.padding(15.dp) + Modifier.weight(1f, true),
                        value = searchval.value,
                        onValueChange = {
                            searchval.value = it

                            delayedSearch.removeCallbacksAndMessages(null)

                            if(!it.text.isBlank()) {
                                if(System.currentTimeMillis() - lastSearch > 1000L) {
                                    lastSearch = System.currentTimeMillis()
                                    manager.search(it.text, searchResult)
                                }
                                else {
                                    delayedSearch.postDelayed({
                                        lastSearch = System.currentTimeMillis()
                                        manager.search(it.text, searchResult)
                                    }, 1000)
                                }
                            }
                            else
                                manager.getExplore(searchResult)
                        },
                        textStyle = MaterialTheme.typography.h3,
                        textColor = Color.Gray,
                        cursorColor = Color.Gray
                )
                IconButton(
                        modifier = Modifier.gravity(Alignment.CenterVertically) + Modifier.padding(end=10.dp),
                        onClick = {
                            searchval.value = TextFieldValue()
                        }
                ) {
                    Icon(
                            asset = vectorResource(id = R.drawable.ic_clear),
                            tint = Color.Black
                    )
                }
            }
        }

        if(searchResult.value.names() != null) {

            if(searchResult.value.has("title")) {
                // These are results from the Explore page
                val rows = searchResult.value.getJSONArray("rows")

                LazyColumnItems(
                        modifier = Modifier.padding(top=10.dp),
                        items = IntRange(0, rows.length()-1).toList()
                ) {
                    ExploreResult(activity, rows.getJSONObject(it).getJSONArray("modules").getJSONObject(0))
                }
            }
            else {
                // These are search results
                LazyColumnItems(
                        modifier = Modifier.padding(top=10.dp),
                        items = IntRange(0, searchResult.value.length()-1).toList()
                ) {
                    SearchResult(activity, searchResult.value, it)
                }
            }
        }
        else {
            Box(
                    modifier = Modifier.fillMaxSize(),
                    gravity = ContentGravity.Center
            ) {
                Column(
                        horizontalGravity = Alignment.CenterHorizontally
                ) {
                    Text(
                            text = "Loading...",
                            style = MaterialTheme.typography.body1
                    )

                    CircularProgressIndicator(
                            modifier = Modifier.padding(top=10.dp) + Modifier.size(100.dp),
                            color = Color.White
                    )
                }
            }
        }
    }
}

// TODO: Make all items clickable!
@Composable
fun ExploreResult(activity: MainActivity, row: JSONObject) {

    if(row["type"] == "FEATURED_PROMOTIONS")
        return

    if(row.getString("title").isNotEmpty())
        Row(
                verticalGravity = Alignment.CenterVertically
        ) {
            Text(
                    modifier = Modifier.padding(top = 10.dp, bottom = 10.dp) + Modifier.weight(1f, true),
                    text = row["title"] as String,
                    style = MaterialTheme.typography.h2
            )

            if(!row.isNull("showMore"))
                // TODO: Make clickable
                Text(
                        text = row.getJSONObject("showMore").getString("title"),
                        style = MaterialTheme.typography.body2
                )
        }

    when(row["type"]){
        "PAGE_LINKS_CLOUD" -> {
            val list = row.getJSONObject("pagedList")
            val items = list["items"] as JSONArray

            LazyRowItems(
                modifier = Modifier.height(70.dp),
                items = IntRange(0, items.length()-1).toList()
            ) {
                Surface(
                    color = Color.DarkGray,
                    modifier = Modifier.padding(10.dp) +
                            Modifier.clickable(onClick = { TODO() }),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        modifier = Modifier.padding(10.dp),
                        color = Color.White,
                        text = items.getJSONObject(it)["title"] as String,
                        style = MaterialTheme.typography.body2,
                        maxLines = 1
                    )
                }
            }
        }
        "PAGE_LINKS" -> {
            val list = row.getJSONObject("pagedList")
            val items = list["items"] as JSONArray

            // TODO: Make links clickable
            for (j in 0 until items.length()) {
                val item = items.getJSONObject(j)
                Row(
                        modifier = Modifier.padding(top = 15.dp, bottom = 15.dp, start = 20.dp),
                        verticalGravity = Alignment.CenterVertically
                ) {
                    Icon(
                            asset = vectorResource(id = R.drawable.ic_starred),
                            tint = MaterialTheme.colors.secondary
                    )
                    Text(
                            modifier = Modifier.padding(start=10.dp),
                            text = item["title"] as String,
                            color = Color.White,
                            style = MaterialTheme.typography.body1
                    )
                }
            }
        }
        "ALBUM_LIST" -> {
            val list = row.getJSONObject("pagedList")
            val items = list["items"] as JSONArray

            LazyRowItems(
                modifier = Modifier.padding(bottom=20.dp) + Modifier.height(220.dp),
                items = IntRange(0, items.length()-1).toList()
            ) {
                PageAlbum(items.getJSONObject(it))
            }
        }
        "ARTIST_LIST" -> {
            val list = row.getJSONObject("pagedList")
            val items = list["items"] as JSONArray

            LazyRowItems(
                modifier = Modifier.padding(bottom=20.dp) + Modifier.height(220.dp),
                items = IntRange(0, items.length()-1).toList()
            ) {
                PageArtist(items.getJSONObject(it))
            }
        }
        else -> {
            Text(text=row["type"] as String)
        }
    }
}

@Composable
fun SearchResult(activity: MainActivity, result: JSONObject, num: Int) {
    when(num) {
        0 -> {
            // Top Result
            Text(
                    modifier = Modifier.padding(top=10.dp, bottom=10.dp),
                    text = "Top result",
                    style = MaterialTheme.typography.h2
            )
            TopResult(activity, result.getJSONObject("topHit"))
        }
        1 -> {
            // Tracks
            Text(
                    modifier = Modifier.padding(top=10.dp, bottom=10.dp),
                    text = "Tracks",
                    style = MaterialTheme.typography.h2
            )
            for (i in 0 until result.getJSONObject("tracks").getJSONArray("items").length()) {
                if (i > 2)
                    break
                RowTrack(activity, result.getJSONObject("tracks").getJSONArray("items").getJSONObject(i))
            }
        }
        2 -> {
            // Artists
            Text(
                    modifier = Modifier.padding(top=10.dp, bottom=10.dp),
                    text = "Artists",
                    style = MaterialTheme.typography.h2
            )
            for (i in 0 until result.getJSONObject("artists").getJSONArray("items").length()) {
                if (i > 2)
                    break
                RowArtist(activity, result.getJSONObject("artists").getJSONArray("items").getJSONObject(i))
            }
        }
        3 -> {
            // Albums
            Text(
                    modifier = Modifier.padding(top=10.dp, bottom=10.dp),
                    text = "Albums",
                    style = MaterialTheme.typography.h2
            )
            for (i in 0 until result.getJSONObject("albums").getJSONArray("items").length()) {
                if (i > 2)
                    break
                RowAlbum(activity, result.getJSONObject("albums").getJSONArray("items").getJSONObject(i))
            }
        }
        4 -> {
            // Playlists
            Text(
                    modifier = Modifier.padding(top=10.dp, bottom=10.dp),
                    text = "Playlists",
                    style = MaterialTheme.typography.h2
            )
            for (i in 0 until result.getJSONObject("playlists").getJSONArray("items").length()) {
                if (i > 2)
                    break
                RowPlaylist(activity, result.getJSONObject("playlists").getJSONArray("items").getJSONObject(i))
            }
        }
        5 -> {
            // Videos
            Text(
                    modifier = Modifier.padding(top=10.dp, bottom=10.dp),
                    text = "Videos",
                    style = MaterialTheme.typography.h2
            )
            for (i in 0 until result.getJSONObject("videos").getJSONArray("items").length()) {
                if (i > 2)
                    break
                RowVideo(activity, result.getJSONObject("videos").getJSONArray("items").getJSONObject(i))
            }
        }
        else -> {
            Text("Not implemented!")
        }
    }
}

@Composable
fun TopResult(activity: MainActivity, top: JSONObject) {
    top.let {
        when(top.getString("type")) {
            "ARTISTS" -> {
                RowArtist(activity, top["value"] as JSONObject)
            }
            "ALBUMS" -> {
                RowAlbum(activity, top["value"] as JSONObject)
            }
            "TRACKS" -> {
                RowTrack(activity, top["value"] as JSONObject)
            }
            "PLAYLISTS" -> {
                RowPlaylist(activity, top["value"] as JSONObject)
            }
            "VIDEOS" -> {
                RowVideo(activity, top["value"] as JSONObject)
            }
            else -> {
                // Apparently the type is not supported yet? Show it instead
                Text(top.getString("type"))
            }
        }
    }
}
