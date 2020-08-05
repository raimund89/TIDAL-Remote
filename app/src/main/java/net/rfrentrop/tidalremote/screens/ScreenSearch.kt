package net.rfrentrop.tidalremote.screens

import android.os.Handler
import android.os.Looper
import androidx.compose.Composable
import androidx.compose.MutableState
import androidx.compose.state
import androidx.ui.core.Alignment
import androidx.ui.core.ContentScale
import androidx.ui.core.Modifier
import androidx.ui.foundation.*
import androidx.ui.graphics.Color
import androidx.ui.input.TextFieldValue
import androidx.ui.layout.*
import androidx.ui.material.IconButton
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Surface
import androidx.ui.res.imageResource
import androidx.ui.res.vectorResource
import androidx.ui.savedinstancestate.savedInstanceState
import androidx.ui.text.style.TextOverflow
import androidx.ui.unit.dp
import net.rfrentrop.tidalremote.R
import net.rfrentrop.tidalremote.tidalapi.TidalManager
import net.rfrentrop.tidalremote.ui.Screen
import org.json.JSONArray
import org.json.JSONObject

@Composable
fun ScreenSearch(page: MutableState<Screen>, manager: TidalManager) {

    val searchResult = state { JSONObject() }
    var lastSearch = 0L
    val delayedSearch = Handler(Looper.myLooper()!!)

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

        ScrollableColumn {
            if(searchResult.value.names() != null) {
                // Top Result
                Text(
                        modifier = Modifier.padding(top=10.dp, bottom=10.dp),
                        text = "Top result",
                        style = MaterialTheme.typography.h2
                )
                TopResult(page, searchResult.value.getJSONObject("topHit"))

                // Tracks
                Text(
                        modifier = Modifier.padding(top=10.dp, bottom=10.dp),
                        text = "Tracks",
                        style = MaterialTheme.typography.h2
                )
                for (i in 0 until searchResult.value.getJSONObject("tracks").getJSONArray("items").length()) {
                    if (i > 2)
                        break
                    TrackRow(page, searchResult.value.getJSONObject("tracks").getJSONArray("items").getJSONObject(i))
                }

                // Artists
                Text(
                        modifier = Modifier.padding(top=10.dp, bottom=10.dp),
                        text = "Artists",
                        style = MaterialTheme.typography.h2
                )
                for (i in 0 until searchResult.value.getJSONObject("artists").getJSONArray("items").length()) {
                    if (i > 2)
                        break
                    ArtistRow(page, searchResult.value.getJSONObject("artists").getJSONArray("items").getJSONObject(i))
                }

                // Albums
                Text(
                        modifier = Modifier.padding(top=10.dp, bottom=10.dp),
                        text = "Albums",
                        style = MaterialTheme.typography.h2
                )
                for (i in 0 until searchResult.value.getJSONObject("albums").getJSONArray("items").length()) {
                    if (i > 2)
                        break
                    AlbumRow(page, searchResult.value.getJSONObject("albums").getJSONArray("items").getJSONObject(i))
                }

                // Playlists
                Text(
                        modifier = Modifier.padding(top=10.dp, bottom=10.dp),
                        text = "Playlists",
                        style = MaterialTheme.typography.h2
                )
                for (i in 0 until searchResult.value.getJSONObject("playlists").getJSONArray("items").length()) {
                    if (i > 2)
                        break
                    PlaylistRow(page, searchResult.value.getJSONObject("playlists").getJSONArray("items").getJSONObject(i))
                }

                // Videos
                Text(
                        modifier = Modifier.padding(top=10.dp, bottom=10.dp),
                        text = "Videos",
                        style = MaterialTheme.typography.h2
                )
                for (i in 0 until searchResult.value.getJSONObject("videos").getJSONArray("items").length()) {
                    if (i > 2)
                        break
                    VideoRow(page, searchResult.value.getJSONObject("videos").getJSONArray("items").getJSONObject(i))
                }
            }
        }
    }
}

@Composable
fun TopResult(page: MutableState<Screen>, top: JSONObject) {
    top.let {
        when(top.getString("type")) {
            "ARTISTS" -> {
                ArtistRow(page, top["value"] as JSONObject)
            }
            "ALBUMS" -> {
                AlbumRow(page, top["value"] as JSONObject)
            }
            "TRACKS" -> {
                TrackRow(page, top["value"] as JSONObject)
            }
            "PLAYLISTS" -> {
                PlaylistRow(page, top["value"] as JSONObject)
            }
            "VIDEOS" -> {
                VideoRow(page, top["value"] as JSONObject)
            }
            else -> {
                // Apparently the type is not supported yet? Show it instead
                Text(top.getString("type"))
            }
        }
    }
}

@Composable
fun ArtistRow(page: MutableState<Screen>, artist: JSONObject) {
    Row(
            modifier = Modifier.height(70.dp) + Modifier.fillMaxWidth(),
            verticalGravity = Alignment.CenterVertically
    ) {
        Image(
                modifier = Modifier.aspectRatio(1f),
                asset = imageResource(id = R.drawable.emptycover),
                contentScale = ContentScale.FillHeight
        )

        Column(
                modifier = Modifier.padding(start = 10.dp, end = 10.dp) + Modifier.weight(1f, true)
        ) {
            Text(
                    text = artist["name"] as String,
                    style = MaterialTheme.typography.body1,
                    color = Color.White,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
            )

            // Fabricate the artist roles
            val roles = ArrayList<String>()
            for(i in 0 until (artist["artistRoles"] as JSONArray).length())
                roles.add(artist.getJSONArray("artistRoles").getJSONObject(i)["category"] as String)

            Text(
                    text = roles.joinToString(", "),
                    style = MaterialTheme.typography.body2,
                    color = Color.LightGray,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
            )
        }

        IconButton(onClick = {
            TODO()
        }) {
            Icon(vectorResource(id = R.drawable.ic_more))
        }

        Spacer(modifier = Modifier.width(10.dp))
    }
}

// TODO: Add album year
@Composable
fun AlbumRow(page: MutableState<Screen>, album: JSONObject) {
    Row(
            modifier = Modifier.height(70.dp) + Modifier.fillMaxWidth(),
            verticalGravity = Alignment.CenterVertically
    ) {
        Image(
                modifier = Modifier.aspectRatio(1f),
                asset = imageResource(id = R.drawable.emptycover),
                contentScale = ContentScale.FillHeight
        )

        Column(
                modifier = Modifier.padding(start = 10.dp, end = 10.dp) + Modifier.weight(1f, true)
        ) {
            Text(
                    text = album["title"] as String,
                    style = MaterialTheme.typography.body1,
                    color = Color.White,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
            )

            // Fabricate the artist list
            val artists = ArrayList<String>()
            for(i in 0 until (album["artists"] as JSONArray).length())
                artists.add(album.getJSONArray("artists").getJSONObject(i)["name"] as String)

            Text(
                    text = artists.joinToString(", "),
                    style = MaterialTheme.typography.body2,
                    color = Color.LightGray,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
            )
        }

        IconButton(onClick = {
            TODO()
        }) {
            Icon(vectorResource(id = R.drawable.ic_more))
        }

        Spacer(modifier = Modifier.width(10.dp))
    }
}

// TODO: Add explicit/master/etc.
@Composable
fun TrackRow(page: MutableState<Screen>, track: JSONObject) {
    Row(
            modifier = Modifier.height(70.dp) + Modifier.fillMaxWidth(),
            verticalGravity = Alignment.CenterVertically
    ) {
        Image(
                modifier = Modifier.aspectRatio(1f),
                asset = imageResource(id = R.drawable.emptycover),
                contentScale = ContentScale.FillHeight
        )

        Column(
                modifier = Modifier.padding(start = 10.dp, end = 10.dp) + Modifier.weight(1f, true)
        ) {
            Text(
                    text = track["title"] as String,
                    style = MaterialTheme.typography.body1,
                    color = Color.White,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
            )

            // Fabricate the artist list
            val artists = ArrayList<String>()
            for(i in 0 until (track["artists"] as JSONArray).length())
                artists.add(track.getJSONArray("artists").getJSONObject(i)["name"] as String)

            Text(
                    text = artists.joinToString(", "),
                    style = MaterialTheme.typography.body2,
                    color = Color.LightGray,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
            )
        }

        IconButton(onClick = {
            TODO()
        }) {
            Icon(vectorResource(id = R.drawable.ic_more))
        }

        Spacer(modifier = Modifier.width(10.dp))
    }
}

// TODO: Add nr of tracks
@Composable
fun PlaylistRow(page: MutableState<Screen>, playlist: JSONObject) {
    Row(
            modifier = Modifier.height(70.dp) + Modifier.fillMaxWidth(),
            verticalGravity = Alignment.CenterVertically
    ) {
        Image(
                modifier = Modifier.aspectRatio(1f),
                asset = imageResource(id = R.drawable.emptycover),
                contentScale = ContentScale.FillHeight
        )

        Column(
                modifier = Modifier.padding(start = 10.dp, end = 10.dp) + Modifier.weight(1f, true)
        ) {
            Text(
                    text = playlist["title"] as String,
                    style = MaterialTheme.typography.body1,
                    color = Color.White,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
            )

            Text(
                    text = if(playlist.getJSONObject("creator").has("name")) playlist.getJSONObject("creator")["name"] as String else "TIDAL",
                    style = MaterialTheme.typography.body2,
                    color = Color.LightGray,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
            )
        }

        IconButton(onClick = {
            TODO()
        }) {
            Icon(vectorResource(id = R.drawable.ic_more))
        }

        Spacer(modifier = Modifier.width(10.dp))
    }
}

// TODO: Add duration
@Composable
fun VideoRow(page: MutableState<Screen>, video: JSONObject) {
    Row(
            modifier = Modifier.height(70.dp) + Modifier.fillMaxWidth(),
            verticalGravity = Alignment.CenterVertically
    ) {
        Image(
                modifier = Modifier.aspectRatio(1f),
                asset = imageResource(id = R.drawable.emptycover),
                contentScale = ContentScale.FillHeight
        )

        Column(
                modifier = Modifier.padding(start = 10.dp, end = 10.dp) + Modifier.weight(1f, true)
        ) {
            Text(
                    text = video["title"] as String,
                    style = MaterialTheme.typography.body1,
                    color = Color.White,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
            )

            // Fabricate the artist list
            val artists = ArrayList<String>()
            for(i in 0 until (video["artists"] as JSONArray).length())
                artists.add(video.getJSONArray("artists").getJSONObject(i)["name"] as String)

            Text(
                    text = artists.joinToString(", "),
                    style = MaterialTheme.typography.body2,
                    color = Color.LightGray,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
            )
        }

        IconButton(onClick = {
            TODO()
        }) {
            Icon(vectorResource(id = R.drawable.ic_more))
        }

        Spacer(modifier = Modifier.width(10.dp))
    }
}
