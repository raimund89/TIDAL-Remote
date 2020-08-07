package net.rfrentrop.tidalremote.screens

import androidx.compose.Composable
import androidx.compose.state
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.ContentGravity
import androidx.ui.foundation.Text
import androidx.ui.foundation.lazy.LazyColumnItems
import androidx.ui.foundation.lazy.LazyRowItems
import androidx.ui.graphics.Color
import androidx.ui.layout.*
import androidx.ui.material.CircularProgressIndicator
import androidx.ui.material.MaterialTheme
import androidx.ui.unit.dp
import net.rfrentrop.tidalremote.MainActivity
import net.rfrentrop.tidalremote.ui.*
import org.json.JSONArray
import org.json.JSONObject

@Composable
fun ScreenHome(activity: MainActivity) {

    val searchResult = state { JSONObject() }

    if(activity.manager.user.loggedIn)
        activity.manager.getHome(searchResult)

    Column(
        modifier = Modifier.padding(10.dp)
    ) {
        Text(
            text = "Home",
            style = MaterialTheme.typography.h1,
        )

        if(searchResult.value.names() != null) {
            val rows = searchResult.value.getJSONArray("rows")

            LazyColumnItems(
                    modifier = Modifier.padding(top=10.dp),
                    items = IntRange(0, rows.length()-1).toList()
            ) {
                val row = rows.getJSONObject(it).getJSONArray("modules").getJSONObject(0)

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

                lateinit var list: JSONObject
                lateinit var items: JSONArray

                if(row.has("pagedList")) {
                    list = row.getJSONObject("pagedList")
                    items = list["items"] as JSONArray
                }

                when (row["type"] as String) {
                    "MIXED_TYPES_LIST" -> {
                        LazyRowItems(
                                modifier = Modifier.padding(bottom = 20.dp, start = 10.dp, end = 10.dp) + Modifier.height(220.dp),
                                items = IntRange(0, items.length() - 1).toList()
                        ) {
                            val item = items.getJSONObject(it)

                            when (item["type"] as String) {
                                "ALBUM" -> {
                                    PageAlbum(activity, item.getJSONObject("item"))
                                }
                                "MIX" -> {
                                    PageMix(activity, item.getJSONObject("item"))
                                }
                                "PLAYLIST" -> {
                                    PagePlaylist(activity, item.getJSONObject("item"))
                                }
                                else -> {
                                    Text(item["type"] as String)
                                }
                            }
                        }
                    }
                    "TRACK_LIST" -> {
                        ListTracks(activity, items, Orientation.VERTICAL, 5)
                    }
                    "ALBUM_LIST" -> {
                        ListAlbums(activity, items, Orientation.HORIZONTAL)
                    }
                    "MIX_LIST" -> {
                        ListMixes(activity, items, Orientation.HORIZONTAL)
                    }
                    "PLAYLIST_LIST" -> {
                        ListPlaylists(activity, items, Orientation.HORIZONTAL)
                    }
                    else -> {
                        Text(row["type"] as String)
                    }
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
