package net.rfrentrop.tidalremote.screens

import androidx.compose.*
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Icon
import androidx.ui.foundation.ScrollableColumn
import androidx.ui.foundation.Text
import androidx.ui.foundation.TextField
import androidx.ui.graphics.Color
import androidx.ui.input.TextFieldValue
import androidx.ui.layout.Row
import androidx.ui.layout.padding
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Surface
import androidx.ui.res.vectorResource
import androidx.ui.savedinstancestate.savedInstanceState
import androidx.ui.unit.dp
import net.rfrentrop.tidalremote.R
import net.rfrentrop.tidalremote.tidalapi.TidalManager
import net.rfrentrop.tidalremote.ui.Screen
import org.json.JSONObject

@Composable
fun ScreenSearch(page: MutableState<Screen>, manager: TidalManager) {

    val searchResult = state { JSONObject() }
    var lastSearch = 0L

    ScrollableColumn(
        modifier = Modifier.padding(10.dp)
    ) {

        var searchval by savedInstanceState(saver = TextFieldValue.Saver) { TextFieldValue() }

        Surface(color = Color.White) {
            Row {
                Icon(
                    modifier = Modifier.gravity(Alignment.CenterVertically) + Modifier.padding(start=10.dp),
                    asset = vectorResource(id = R.drawable.ic_search),
                    tint = Color.Black
                )
                TextField(
                    modifier = Modifier.padding(15.dp) + Modifier.weight(1f, true),
                    value = searchval,
                    onValueChange = {
                        searchval = it
                        // TODO: The last entered character is not searched now. Make this a queued system
                        if(!it.text.isBlank() && System.currentTimeMillis() - lastSearch > 1000L) {
                            lastSearch = System.currentTimeMillis()
                            manager.search(it.text, searchResult)
                        }
                        else
                            manager.getExplore(searchResult)
                    },
                    textStyle = MaterialTheme.typography.h3,
                    textColor = Color.Gray,
                    cursorColor = Color.Gray
                )
                Icon(
                    modifier = Modifier.gravity(Alignment.CenterVertically) + Modifier.padding(end=10.dp),
                    asset = vectorResource(id = R.drawable.ic_clear),
                    tint = Color.Black
                )
            }
        }

        // Top Result
        TopResult(page, searchResult.value.getJSONObject("topHit"))
        // Tracks
        for (i in 0 until searchResult.value.getJSONObject("tracks").getJSONArray("items").length()){
            if(i>2)
                break
            TrackRow(page, searchResult.value.getJSONObject("tracks").getJSONArray("items").getJSONObject(i))
        }
        // Artists
        for (i in 0 until searchResult.value.getJSONObject("artists").getJSONArray("items").length()){
            if(i>2)
                break
            ArtistRow(page, searchResult.value.getJSONObject("artists").getJSONArray("items").getJSONObject(i))
        }
        // Albums
        for (i in 0 until searchResult.value.getJSONObject("albums").getJSONArray("items").length()){
            if(i>2)
                break
            AlbumRow(page, searchResult.value.getJSONObject("albums").getJSONArray("items").getJSONObject(i))
        }
        // Playlists
        for (i in 0 until searchResult.value.getJSONObject("playlists").getJSONArray("items").length()){
            if(i>2)
                break
            PlaylistRow(page, searchResult.value.getJSONObject("playlists").getJSONArray("items").getJSONObject(i))
        }
        // Videos
        for (i in 0 until searchResult.value.getJSONObject("videos").getJSONArray("items").length()){
            if(i>2)
                break
            VideoRow(page, searchResult.value.getJSONObject("videos").getJSONArray("items").getJSONObject(i))
        }
    }
}

@Composable
fun TopResult(page: MutableState<Screen>, top: JSONObject) {
    top?.let {
        when(top.getString("type")) {
            "ARTISTS" -> {
                ArtistRow(page, top)
            }
            "ALBUMS" -> {
                AlbumRow(page, top)
            }
            "TRACKS" -> {
                TrackRow(page, top)
            }
            "PLAYLISTS" -> {
                PlaylistRow(page, top)
            }
            "VIDEOS" -> {
                VideoRow(page, top)
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
    Text(artist.getString("name"))
}

@Composable
fun AlbumRow(page: MutableState<Screen>, album: JSONObject) {
    Text(album.getString("name"))
}

@Composable
fun TrackRow(page: MutableState<Screen>, track: JSONObject) {
    Text(track.getString("name"))
}

@Composable
fun PlaylistRow(page: MutableState<Screen>, playlist: JSONObject) {
    Text(playlist.getString("name"))
}

@Composable
fun VideoRow(page: MutableState<Screen>, video: JSONObject) {
    Text(video.getString("name"))
}