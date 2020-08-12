package net.rfrentrop.tidalremote.ui

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.lazy.LazyRowItems
import androidx.ui.layout.Column
import androidx.ui.layout.height
import androidx.ui.layout.padding
import androidx.ui.unit.dp
import net.rfrentrop.tidalremote.MainActivity
import org.json.JSONArray

enum class Orientation {
    HORIZONTAL,
    VERTICAL
}

@Composable
fun ListArtists(activity: MainActivity, artists: JSONArray, orientation: Orientation, limit: Int = 3) {
    if(orientation == Orientation.HORIZONTAL)
        LazyRowItems(
                modifier = Modifier.padding(bottom=20.dp, start=10.dp, end=10.dp) + Modifier.height(220.dp),
                items = IntRange(0, artists.length()-1).toList()
        ) {
            PageArtist(activity, artists.getJSONObject(it))
        }
    else {
        Column(
                modifier = Modifier.padding(start = 10.dp, end=10.dp)
        ) {
            for (i in 0 until artists.length()) {
                if (i >= limit)
                    break
                RowArtist(activity, artists.getJSONObject(i))
            }
        }
    }
}

@Composable
fun ListAlbums(activity: MainActivity, albums: JSONArray, orientation: Orientation, limit: Int = 3) {
    if(orientation == Orientation.HORIZONTAL) {
        LazyRowItems(
                modifier = Modifier.padding(bottom = 20.dp, start = 10.dp, end = 10.dp) + Modifier.height(220.dp),
                items = IntRange(0, albums.length() - 1).toList()
        ) {
            PageAlbum(activity, albums.getJSONObject(it))
        }
    }
    else {
        Column(
                modifier = Modifier.padding(start = 10.dp, end = 10.dp)
        ) {
            for (i in 0 until albums.length()) {
                if (i >= limit)
                    break
                RowAlbum(activity, albums.getJSONObject(i))
            }
        }
    }
}

@Composable
fun ListTracks(activity: MainActivity, tracks: JSONArray, orientation: Orientation, limit: Int = 3, covers: Boolean = true) {
    if(orientation == Orientation.HORIZONTAL) {
        LazyRowItems(
                modifier = Modifier.padding(bottom = 20.dp, start = 10.dp, end = 10.dp) + Modifier.height(220.dp),
                items = IntRange(0, tracks.length() - 1).toList()
        ) {
            PageTrack(activity, tracks.getJSONObject(it))
        }
    }
    else {
        Column(
                modifier = Modifier.padding(start = 10.dp, end = 10.dp)
        ) {
            for (i in 0 until tracks.length()) {
                if (i >= limit)
                    break
                RowTrack(activity, tracks.getJSONObject(i), covers=covers)
            }
        }
    }
}

@Composable
fun ListMixes(activity: MainActivity, mixes: JSONArray, orientation: Orientation, limit: Int = 3) {
    if(orientation == Orientation.HORIZONTAL){
        LazyRowItems(
                modifier = Modifier.padding(bottom=20.dp, start = 10.dp, end = 10.dp) + Modifier.height(220.dp),
                items = IntRange(0, mixes.length()-1).toList()
        ) { index ->
            PageMix(activity, mixes.getJSONObject(index))
        }
    }
    // TODO: Implement a row-style for mixes
    /*else {
        Column(
                modifier = Modifier.padding(start = 10.dp, end = 10.dp)
        ) {
            for (i in 0 until mixes.length()){
                if(i >= limit)
                    break
                RowMix
            }
        }
    }*/
}

@Composable
fun ListVideos(activity: MainActivity, videos: JSONArray, orientation: Orientation, limit: Int = 3) {
    if(orientation == Orientation.HORIZONTAL) {
        LazyRowItems(
                modifier = Modifier.padding(bottom=20.dp, start = 10.dp, end = 10.dp) + Modifier.height(220.dp),
                items = IntRange(0, videos.length()-1).toList()
        ) { index ->
            PageVideo(activity, videos.getJSONObject(index))
        }
    }
    else {
        Column(
                modifier = Modifier.padding(start = 10.dp, end = 10.dp)
        ) {
            for (i in 0 until videos.length()) {
                if(i >= limit)
                    break
                RowVideo(activity, videos.getJSONObject(i))
            }
        }
    }
}

@Composable
fun ListPlaylists(activity: MainActivity, playlists: JSONArray, orientation: Orientation, limit: Int = 3) {
    if(orientation == Orientation.HORIZONTAL) {
        LazyRowItems(
                modifier = Modifier.padding(bottom=20.dp, start = 10.dp, end = 10.dp) + Modifier.height(220.dp),
                items = IntRange(0, playlists.length()-1).toList()
        ) {
            PagePlaylist(activity, playlists.getJSONObject(it))
        }
    }
    else {
        Column(
                modifier = Modifier.padding(start = 10.dp, end = 10.dp)
        ) {
            for (i in 0 until playlists.length()) {
                if(i >= limit)
                    break
                RowPlaylist(activity, playlists.getJSONObject(i))
            }
        }
    }
}
