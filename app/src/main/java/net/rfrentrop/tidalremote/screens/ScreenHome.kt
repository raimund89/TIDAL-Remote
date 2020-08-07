package net.rfrentrop.tidalremote.screens

import androidx.compose.Composable
import androidx.compose.MutableState
import androidx.compose.state
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.ScrollableColumn
import androidx.ui.foundation.Text
import androidx.ui.foundation.lazy.LazyRowItems
import androidx.ui.layout.Column
import androidx.ui.layout.Row
import androidx.ui.layout.height
import androidx.ui.layout.padding
import androidx.ui.material.MaterialTheme
import androidx.ui.unit.dp
import net.rfrentrop.tidalremote.tidalapi.TidalManager
import net.rfrentrop.tidalremote.ui.PageAlbumItem
import net.rfrentrop.tidalremote.ui.PageMixItem
import net.rfrentrop.tidalremote.ui.PagePlaylistItem
import net.rfrentrop.tidalremote.ui.Screen
import org.json.JSONArray
import org.json.JSONObject

@Composable
fun ScreenHome(page: MutableState<Screen>, manager: TidalManager) {

    val searchResult = state { JSONObject() }

    if(manager.user.loggedIn)
        manager.getHome(searchResult)

    Column(
        modifier = Modifier.padding(10.dp)
    ) {
        Text(
            text = "Home",
            style = MaterialTheme.typography.h1,
        )

        ScrollableColumn(
            modifier = Modifier.padding(top=10.dp)
        ) {
            if(searchResult.value.names() != null) {
                val rows = searchResult.value.getJSONArray("rows")

                for (i in 0 until rows.length()) {
                    val row = rows.getJSONObject(i).getJSONArray("modules").getJSONObject(0)

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

                    when (row["type"] as String) {
                        "MIXED_TYPES_LIST" -> {
                            val list = row.getJSONObject("pagedList")
                            val items = list["items"] as JSONArray

                            LazyRowItems(
                                modifier = Modifier.padding(bottom = 20.dp) + Modifier.height(220.dp),
                                items = IntRange(0, items.length() - 1).toList()
                            ) {
                                val item = items.getJSONObject(it)

                                when (item["type"] as String) {
                                    "ALBUM" -> {
                                        PageAlbumItem(item.getJSONObject("item"))
                                    }
                                    "MIX" -> {
                                        PageMixItem(item.getJSONObject("item"))
                                    }
                                    "PLAYLIST" -> {
                                        PagePlaylistItem(item.getJSONObject("item"))
                                    }
                                    else -> {
                                        Text(item["type"] as String)
                                    }
                                }
                            }
                        }
                        "TRACK_LIST" -> {
                            val list = row.getJSONObject("pagedList")
                            val items = list["items"] as JSONArray

                            for (i in 0 until items.length())
                                TrackRow(page, items.getJSONObject(i))
                        }
                        "ALBUM_LIST" -> {
                            val list = row.getJSONObject("pagedList")
                            val items = list["items"] as JSONArray

                            LazyRowItems(
                                modifier = Modifier.padding(bottom=20.dp) + Modifier.height(220.dp),
                                items = IntRange(0, items.length()-1).toList()
                            ) {
                                PageAlbumItem(items.getJSONObject(it))
                            }
                        }
                        "MIX_LIST" -> {
                            val list = row.getJSONObject("pagedList")
                            val items = list["items"] as JSONArray

                            LazyRowItems(
                                modifier = Modifier.padding(bottom=20.dp) + Modifier.height(220.dp),
                                items = IntRange(0, items.length()-1).toList()
                            ) {
                                PageMixItem(items.getJSONObject(it))
                            }
                        }
                        "PLAYLIST_LIST" -> {
                            val list = row.getJSONObject("pagedList")
                            val items = list["items"] as JSONArray

                            LazyRowItems(
                                modifier = Modifier.padding(bottom=20.dp) + Modifier.height(220.dp),
                                items = IntRange(0, items.length()-1).toList()
                            ) {
                                PagePlaylistItem(items.getJSONObject(it))
                            }
                        }
                        else -> {
                            Text(row["type"] as String)
                        }
                    }
                }
            }
        }
    }
}
