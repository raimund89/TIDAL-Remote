package net.rfrentrop.tidalremote.screens

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.Composable
import androidx.compose.state
import androidx.ui.core.Alignment
import androidx.ui.core.ContentScale
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.ContentGravity
import androidx.ui.foundation.Image
import androidx.ui.foundation.Text
import androidx.ui.foundation.lazy.LazyColumnItems
import androidx.ui.foundation.lazy.LazyRowItems
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.Color
import androidx.ui.graphics.asImageAsset
import androidx.ui.layout.*
import androidx.ui.material.CircularProgressIndicator
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Surface
import androidx.ui.text.style.TextAlign
import androidx.ui.text.style.TextOverflow
import androidx.ui.unit.dp
import net.rfrentrop.tidalremote.MainActivity
import net.rfrentrop.tidalremote.tidalapi.TidalManager
import net.rfrentrop.tidalremote.ui.*
import org.json.JSONArray
import org.json.JSONObject

// TODO: Picture is not cropped right
// TODO: Gradient at the top
// TODO: Icons for the three action buttons

@Composable
fun ScreenArtist(activity: MainActivity) {

    if(activity.manager.currentArtist == -1) {
        Log.d("ScreenArtist", "No artist set")
        activity.onBackPressed()
    }

    val searchResult = state { JSONObject() }

    if(activity.manager.user.loggedIn)
        activity.manager.getArtist(searchResult)

    if(searchResult.value.names() != null) {
        val rows = searchResult.value.getJSONArray("rows")

        LazyColumnItems(
                items = IntRange(0, rows.length()-1).toList()
        ) {
            val row = rows.getJSONObject(it).getJSONArray("modules").getJSONObject(0)

            lateinit var list: JSONObject
            lateinit var items: JSONArray

            if(row.has("pagedList")) {
                list = row.getJSONObject("pagedList")
                items = list["items"] as JSONArray
            }

            when(row["type"]) {
                "ARTIST_HEADER" -> {
                    val artist = row.getJSONObject("artist")

                    Column {
                        val loadPictureState = loadPicture(TidalManager.IMAGE_URL.format(artist.getString("picture").replace("-", "/"), 750, 750))

                        if (loadPictureState is UiState.Success<Bitmap>)
                            Image(
                                    modifier = Modifier.aspectRatio(1.3f),
                                    asset = loadPictureState.data.asImageAsset(),
                                    contentScale = ContentScale.FillWidth,
                            )
                        else
                            Column(
                                    horizontalGravity = Alignment.CenterHorizontally,
                                    modifier = Modifier.aspectRatio(1.3f)
                            ) {
                                Text(
                                        text = "Loading...",
                                        style = MaterialTheme.typography.body1
                                )

                                CircularProgressIndicator(
                                        modifier = Modifier.padding(top = 10.dp) + Modifier.size(100.dp),
                                        color = Color.White
                                )
                            }

                        Row(
                                modifier = Modifier.padding(start = 10.dp, end = 10.dp, top=10.dp)
                        ) {
                            Column {
                                Text(
                                        text = artist.getString("name"),
                                        color = Color.White,
                                        style = MaterialTheme.typography.h2,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                )

                                Text(
                                        text = "TO BE IMPLEMENTED",
                                        color = Color.LightGray,
                                        style = MaterialTheme.typography.body2,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                )
                            }

                            // TODO: Add 'like' button
                        }

                        Row(
                                modifier = Modifier.padding(start = 10.dp, end = 10.dp, top=10.dp, bottom=20.dp)
                        ) {
                            Surface(
                                    color = Color.DarkGray,
                                    shape = RoundedCornerShape(5.dp),
                                    modifier = Modifier.weight(1f, true) + Modifier.padding(end=10.dp)
                            ) {
                                Text(
                                        text = "Play",
                                        style = MaterialTheme.typography.body2,
                                        color = Color.White,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(top=10.dp, bottom=10.dp)
                                )
                            }
                            Surface(
                                    color = Color.DarkGray,
                                    shape = RoundedCornerShape(5.dp),
                                    modifier = Modifier.weight(1f, true) + Modifier.padding(end=10.dp)
                            ) {
                                Text(
                                        text = "Shuffle",
                                        style = MaterialTheme.typography.body2,
                                        color = Color.White,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(top=10.dp, bottom=10.dp)
                                )
                            }
                            Surface(
                                    color = Color.DarkGray,
                                    shape = RoundedCornerShape(5.dp),
                                    modifier = Modifier.weight(1f, true) + Modifier.padding(end=10.dp)
                            ) {
                                Text(
                                        text = "Radio",
                                        style = MaterialTheme.typography.body2,
                                        color = Color.White,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(top=10.dp, bottom=10.dp)
                                )
                            }
                        }
                    }
                }
                "TRACK_LIST" -> {
                    ListTracks(activity, items, Orientation.VERTICAL, 4)
                }
                "ALBUM_LIST" -> {
                    ListAlbums(activity, items, Orientation.HORIZONTAL)
                }
                "MIX_LIST" -> {
                    ListMixes(activity, items, Orientation.HORIZONTAL)
                }
                "MIXED_TYPES_LIST" -> {
                    LazyRowItems(
                            modifier = Modifier.padding(bottom = 20.dp) + Modifier.height(220.dp),
                            items = IntRange(0, items.length() - 1).toList()
                    ) { index ->
                        val item = items.getJSONObject(index)

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
                "VIDEO_LIST" -> {
                    ListVideos(activity, items, Orientation.HORIZONTAL)
                }
                "ITEM_LIST_WITH_ROLES" -> {
                    Text("Credits list to be implemented!!!!!")
                }
                "ARTIST_LIST" -> {
                    ListArtists(activity, items, Orientation.HORIZONTAL)
                }
                "SOCIAL" -> {
                    val list = row.getJSONArray("socialProfiles")

                    for (k in 0 until list.length()) {
                        Row {
                            Text(text = list.getJSONObject(k).getString("url"))
                        }
                    }
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
