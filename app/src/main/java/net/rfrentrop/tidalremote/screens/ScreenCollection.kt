package net.rfrentrop.tidalremote.screens

import androidx.compose.Composable
import androidx.compose.state
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.ContentGravity
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.foundation.lazy.LazyColumnItems
import androidx.ui.foundation.lazy.LazyRowItems
import androidx.ui.graphics.Color
import androidx.ui.layout.*
import androidx.ui.material.CircularProgressIndicator
import androidx.ui.material.IconButton
import androidx.ui.material.MaterialTheme
import androidx.ui.res.vectorResource
import androidx.ui.unit.dp
import net.rfrentrop.tidalremote.MainActivity
import net.rfrentrop.tidalremote.R
import net.rfrentrop.tidalremote.tidalapi.TidalManager
import net.rfrentrop.tidalremote.ui.*
import org.json.JSONObject

val categories = listOf("ALBUM", "ARTIST", "PLAYLIST", "TRACK", "VIDEO")

// TODO: Missing My Mix and Recent Activity

@Composable
fun ScreenCollection(activity: MainActivity, manager: TidalManager) {

    val searchResult = state { JSONObject() }

    if(manager.user.loggedIn)
        manager.getFavorites(searchResult)

    Column(
            modifier = Modifier.padding(10.dp)
    ) {
        Row {
            Text(
                    text = "Collection",
                    style = MaterialTheme.typography.h1,
            )
            Spacer(modifier = Modifier.weight(1f, true))
            IconButton(onClick = {
                activity.navigate(Screen.Settings)
            }) {
                Icon(vectorResource(id = R.drawable.ic_settings))
            }
        }

        if(searchResult.value.names() != null) {

            LazyColumnItems(
                    modifier = Modifier.padding(top=10.dp),
                    items = IntRange(0, searchResult.value.length()-1).toList()
            ) {
                if(searchResult.value.isNull(categories[it]))
                    return@LazyColumnItems

                val category = searchResult.value.getJSONArray(categories[it])

                Row(
                        verticalGravity = Alignment.CenterVertically
                ) {
                    Text(
                            modifier = Modifier.padding(top = 10.dp, bottom = 10.dp) + Modifier.weight(1f, true),
                            text = categories[it].toLowerCase().capitalize() + "s",
                            style = MaterialTheme.typography.h2
                    )

                    // TODO: Make clickable
                    Text(
                            text = "Show as list",
                            style = MaterialTheme.typography.body2
                    )
                }

                val detailsResult = state { JSONObject() }

                var urlPart = categories[it].toLowerCase()
                urlPart = if(urlPart == "playlist")
                    "playlistsAndFavoritePlaylist"
                else
                    "favorites/$urlPart"

                if(manager.user.loggedIn)
                    manager.getFavorites(detailsResult, urlPart + "s")

                LazyRowItems(
                        modifier = Modifier.padding(bottom = 20.dp) + Modifier.height(if(category.length()>0) 220.dp else 20.dp),
                        items = IntRange(0, category.length()-1).toList()
                ) { index ->
                    if(detailsResult.value.names() != null)
                        when(categories[it]) {
                            "ALBUM" -> {
                                PageAlbumItem(detailsResult.value.getJSONArray("items").getJSONObject(index).getJSONObject("item"))
                            }
                            "ARTIST" -> {
                                PageArtistItem(detailsResult.value.getJSONArray("items").getJSONObject(index).getJSONObject("item"))
                            }
                            "PLAYLIST" -> {
                                PagePlaylistItem(detailsResult.value.getJSONArray("items").getJSONObject(index).getJSONObject("playlist"), "creator")
                            }
                            "TRACK" -> {
                                PageTrackItem(detailsResult.value.getJSONArray("items").getJSONObject(index).getJSONObject("item"))
                            }
                            "VIDEO" -> {
                                PageVideoItem(detailsResult.value.getJSONArray("items").getJSONObject(index).getJSONObject("item"))
                            }
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
