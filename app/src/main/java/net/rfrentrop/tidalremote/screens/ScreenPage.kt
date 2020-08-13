package net.rfrentrop.tidalremote.screens

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.Composable
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
import net.rfrentrop.tidalremote.R
import net.rfrentrop.tidalremote.tidalapi.TidalManager
import net.rfrentrop.tidalremote.tidalapi.loadPicture
import net.rfrentrop.tidalremote.ui.*
import org.json.JSONArray
import org.json.JSONObject

// TODO: Page doesn't return to top when refreshing

@Composable
fun ScreenPage(activity: MainActivity) {
    val (pageState, refreshPage) = activity.manager.getPageResult()

    activity.refresher = refreshPage

    if(pageState.loading){
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
    else {
        val rows = pageState.currentData?.getJSONArray("rows")

        if (rows != null) {
            LazyColumnItems(
                    items = IntRange(0, rows.length() - 1).toList()
            ) {
                PageRow(activity, refreshPage, rows.getJSONObject(it).getJSONArray("modules").getJSONObject(0))
            }
        }
    }
}

@Composable
fun PageRow(activity: MainActivity, refresh: () -> Unit, row: JSONObject) {
    val items: JSONArray = if(row.has("pagedList"))
        row.getJSONObject("pagedList").getJSONArray("items")
    else
        JSONArray()

    when(row["type"] as String) {
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
        "ALBUM_HEADER" -> {
            val album = row.getJSONObject("album")

            Column {
                val loadPictureState = loadPicture(TidalManager.IMAGE_URL.format(album.getString("cover").replace("-", "/"), 750, 750))

                if (loadPictureState is UiState.Success<Bitmap>)
                    Image(
                        modifier = Modifier.aspectRatio(1.3f) + Modifier.padding(20.dp),
                        asset = loadPictureState.data.asImageAsset(),
                        contentScale = ContentScale.FillHeight,
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
                            text = album.getString("title"),
                            color = Color.White,
                            style = MaterialTheme.typography.h2,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        // Construct the artist list
                        val artists = ArrayList<String>()
                        for(i in 0 until (album["artists"] as JSONArray).length())
                            artists.add(album.getJSONArray("artists").getJSONObject(i)["name"] as String)

                        Text(
                            text = artists.joinToString(", "),
                            color = Color.LightGray,
                            style = MaterialTheme.typography.body2,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // TODO: Add 'like' button
                }

                // TODO: These rows are actually coming from the "playbackControls" JSONArray item
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
                }
            }
        }
        "MIX_HEADER" -> {
            val mix = row.getJSONObject("mix")

            Column {
                val loadPictureState = loadPicture(mix.getJSONObject("images").getJSONObject("LARGE").getString("url").replace("-", "/"))

                if (loadPictureState is UiState.Success<Bitmap>)
                    Image(
                        modifier = Modifier.aspectRatio(1.3f) + Modifier.padding(20.dp),
                        asset = loadPictureState.data.asImageAsset(),
                        contentScale = ContentScale.FillHeight,
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
                            text = mix.getString("title"),
                            color = Color.White,
                            style = MaterialTheme.typography.h2,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = mix.getString("subTitle"),
                            color = Color.LightGray,
                            style = MaterialTheme.typography.body2,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // TODO: Add 'new playlist' button
                }

                // TODO: These rows are actually coming from the "playbackControls" JSONArray item
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
                }
            }
        }
        "ALBUM_ITEMS" -> {
            RowHeader(text = "Tracks")
            Column(
                modifier = Modifier.padding(start = 10.dp, end=10.dp)
            ) {
                for (i in 0 until items.length()) {
                    val item = items.getJSONObject(i).getJSONObject("item")

                    // Construct the artist list
                    val artists = ArrayList<String>()
                    for(i in 0 until (item["artists"] as JSONArray).length())
                        artists.add(item.getJSONArray("artists").getJSONObject(i)["name"] as String)

                    // Construct the flags list
                    val flags = ArrayList<String>()
                    if(item["explicit"] as Boolean)
                        flags.add("EXPLICIT")
                    if(item["audioQuality"] as String == "HI_RES")
                        flags.add("MASTER")

                    RowTemplate(
                        number = item.getInt("trackNumber"),
                        text1 = item.getString("title"),
                        text2 = artists.joinToString(", "),
                        text3 = flags.joinToString(" / "),
                        iconId = R.drawable.ic_more,
                        onClick = {

                        },
                        onIconClick = {

                        }
                    )
                }
            }
        }
        "TRACK_LIST" -> {
            RowHeader(text = if(row.getString("title").isEmpty()) "Tracks" else row.getString("title"))
            ListTracks(activity, items, Orientation.VERTICAL, if(activity.manager.currentPage == PageType.MIX) row.getJSONObject("pagedList").getInt("totalNumberOfItems") else 4, covers=false)
        }
        "ALBUM_LIST" -> {
            RowHeader(text = row.getString("title"))
            ListAlbums(activity, items, Orientation.HORIZONTAL)
        }
        "MIX_LIST" -> {
            RowHeader(text = row.getString("title"))
            ListMixes(activity, items, Orientation.HORIZONTAL)
        }
        "VIDEO_LIST" -> {
            RowHeader(text = row.getString("title"))
            ListVideos(activity, items, Orientation.HORIZONTAL)
        }
        "ARTIST_LIST" -> {
            RowHeader(text = row.getString("title"))
            ListArtists(activity, items, Orientation.HORIZONTAL)
        }
        "PLAYLIST_LIST" -> {
            RowHeader(text = row.getString("title"))
            ListPlaylists(activity, items, Orientation.HORIZONTAL)
        }
        "MIXED_TYPES_LIST" -> {
            RowHeader(text = row.getString("title"))

            // TODO: Make template function for these rows, so margins etc can be changed quickly
            LazyRowItems(
                    modifier = Modifier.padding(bottom = 20.dp, start = 10.dp, end = 10.dp) + Modifier.height(220.dp),
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
        "SOCIAL" -> {
            RowHeader(text = row.getString("title"))
            val list = row.getJSONArray("socialProfiles")

            // TODO: Styling, and include social icons
            for (k in 0 until list.length()) {
                Row {
                    Text(
                            modifier = Modifier.padding(10.dp),
                            text = list.getJSONObject(k).getString("url"),
                            style = MaterialTheme.typography.body1
                    )
                }
            }
        }
        else -> {
            Log.e("ScreenPage", "Row type not implemented: ${row["type"]}")
            Text(row["type"] as String)
        }
    }
}

@Composable
fun RowHeader(text: String) {
    Text(
            modifier = Modifier.padding(10.dp),
            text = text,
            style = MaterialTheme.typography.h2
    )
}
