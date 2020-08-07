package net.rfrentrop.tidalremote.screens

import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import androidx.annotation.DrawableRes
import androidx.compose.Composable
import androidx.compose.MutableState
import androidx.compose.state
import androidx.ui.core.Alignment
import androidx.ui.core.ContentScale
import androidx.ui.core.Modifier
import androidx.ui.foundation.*
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.Color
import androidx.ui.graphics.RectangleShape
import androidx.ui.graphics.asImageAsset
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
import net.rfrentrop.tidalremote.ui.UiState
import net.rfrentrop.tidalremote.ui.loadPicture
import org.json.JSONArray
import org.json.JSONObject

@Composable
fun ScreenSearch(page: MutableState<Screen>, manager: TidalManager) {

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

        ScrollableColumn (
                modifier = Modifier.padding(top=10.dp)
        ) {
            if(searchResult.value.names() != null) {
                if(searchResult.value.has("title"))
                    ExploreResults(page, searchResult.value)
                else
                    SearchResults(page, searchResult.value)
            }
        }
    }
}

// TODO: Make all items clickable!
@Composable
fun ExploreResults(page: MutableState<Screen>, result: JSONObject) {
    val rows = result["rows"] as JSONArray

    for(i in 0 until rows.length()) {
        val row = rows.getJSONObject(i).getJSONArray("modules").getJSONObject(0)

        if(row["type"] == "FEATURED_PROMOTIONS")
            continue

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

                when(row["title"]) {
                    // TODO: Make clickable
                    "Genres" -> {
                        // TODO: Implement multiline
                        ScrollableRow {
                            for (k in 0 until items.length())
                                Surface(
                                        color = Color.DarkGray,
                                        modifier = Modifier.padding(10.dp) +
                                                Modifier.clickable(onClick = { TODO() }),
                                        shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(
                                            modifier = Modifier.padding(10.dp),
                                            color = Color.White,
                                            text = items.getJSONObject(k)["title"] as String,
                                            style = MaterialTheme.typography.body2,
                                            maxLines = 1
                                    )
                                }
                        }
                    }
                    "Moods & Activities" -> {
                        ScrollableRow {
                            for (k in 0 until items.length())
                                Surface(
                                        color = Color.DarkGray,
                                        modifier = Modifier.padding(10.dp) +
                                                Modifier.clickable(onClick = { TODO() }),
                                        shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(
                                            modifier = Modifier.padding(10.dp),
                                            color = Color.White,
                                            text = items.getJSONObject(k)["title"] as String,
                                            style = MaterialTheme.typography.body2,
                                            maxLines = 1
                                    )
                                }
                        }
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

                ScrollableRow(
                        modifier = Modifier.padding(bottom=20.dp)
                ) {
                    for (j in 0 until items.length()) {
                        val item = items.getJSONObject(j)
                        Column(
                                modifier = Modifier.width(180.dp) + Modifier.padding(end = 20.dp)
                        ) {
                            val loadPictureState = loadPicture(TidalManager.IMAGE_URL.format(item.getString("cover").replace("-", "/"), 320, 320))

                            if (loadPictureState is UiState.Success<Bitmap>)
                                Image(
                                        modifier = Modifier.aspectRatio(1f),
                                        asset = loadPictureState.data.asImageAsset(),
                                        contentScale = ContentScale.FillWidth
                                )
                            else
                                Image(
                                        modifier = Modifier.aspectRatio(1f),
                                        asset = imageResource(id = R.drawable.emptycover),
                                        contentScale = ContentScale.FillWidth
                                )

                            Text(
                                    modifier = Modifier.padding(top=10.dp),
                                    text = item["title"] as String,
                                    style = MaterialTheme.typography.body1,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                            )

                            // Construct the artist list
                            val artists = ArrayList<String>()
                            for(i in 0 until (item["artists"] as JSONArray).length())
                                artists.add(item.getJSONArray("artists").getJSONObject(i)["name"] as String)

                            Text(
                                    text = artists.joinToString(", "),
                                    style = MaterialTheme.typography.body2,
                                    color = Color.LightGray,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                            )

                            Text(
                                    modifier = Modifier.padding(top=5.dp),
                                    text = (item["releaseDate"] as String).substring(0, 4),
                                    style = MaterialTheme.typography.subtitle1,
                                    color = MaterialTheme.colors.secondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
            "ARTIST_LIST" -> {
                val list = row.getJSONObject("pagedList")
                val items = list["items"] as JSONArray

                ScrollableRow(
                        modifier = Modifier.padding(bottom = 20.dp)
                ) {
                    for (j in 0 until items.length()) {
                        val item = items.getJSONObject(j)
                        Column(
                                modifier = Modifier.width(180.dp) + Modifier.padding(end = 20.dp)
                        ) {
                            val loadPictureState = loadPicture(TidalManager.IMAGE_URL.format(item.getString("picture").replace("-", "/"), 320, 320))

                            if (loadPictureState is UiState.Success<Bitmap>)
                                Surface(
                                        shape = RoundedCornerShape(50)
                                ) {
                                    Image(
                                            modifier = Modifier.aspectRatio(1f),
                                            asset = loadPictureState.data.asImageAsset(),
                                            contentScale = ContentScale.FillWidth
                                    )
                                }
                            else
                                Image(
                                        modifier = Modifier.aspectRatio(1f),
                                        asset = imageResource(id = R.drawable.emptycover),
                                        contentScale = ContentScale.FillWidth
                                )

                            Text(
                                    modifier = Modifier.padding(top = 10.dp),
                                    text = item["name"] as String,
                                    style = MaterialTheme.typography.body1,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                            )

                            // Construct the artist roles
                            val roles = ArrayList<String>()
                            for (i in 0 until (item["artistRoles"] as JSONArray).length())
                                roles.add(item.getJSONArray("artistRoles").getJSONObject(i)["category"] as String)

                            Text(
                                    text = roles.joinToString(", "),
                                    style = MaterialTheme.typography.body2,
                                    color = Color.LightGray,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchResults(page: MutableState<Screen>, result: JSONObject) {
    // Top Result
    Text(
            modifier = Modifier.padding(top=10.dp, bottom=10.dp),
            text = "Top result",
            style = MaterialTheme.typography.h2
    )
    TopResult(page, result.getJSONObject("topHit"))

    // Tracks
    Text(
            modifier = Modifier.padding(top=10.dp, bottom=10.dp),
            text = "Tracks",
            style = MaterialTheme.typography.h2
    )
    for (i in 0 until result.getJSONObject("tracks").getJSONArray("items").length()) {
        if (i > 2)
            break
        TrackRow(page, result.getJSONObject("tracks").getJSONArray("items").getJSONObject(i))
    }

    // Artists
    Text(
            modifier = Modifier.padding(top=10.dp, bottom=10.dp),
            text = "Artists",
            style = MaterialTheme.typography.h2
    )
    for (i in 0 until result.getJSONObject("artists").getJSONArray("items").length()) {
        if (i > 2)
            break
        ArtistRow(page, result.getJSONObject("artists").getJSONArray("items").getJSONObject(i))
    }

    // Albums
    Text(
            modifier = Modifier.padding(top=10.dp, bottom=10.dp),
            text = "Albums",
            style = MaterialTheme.typography.h2
    )
    for (i in 0 until result.getJSONObject("albums").getJSONArray("items").length()) {
        if (i > 2)
            break
        AlbumRow(page, result.getJSONObject("albums").getJSONArray("items").getJSONObject(i))
    }

    // Playlists
    Text(
            modifier = Modifier.padding(top=10.dp, bottom=10.dp),
            text = "Playlists",
            style = MaterialTheme.typography.h2
    )
    for (i in 0 until result.getJSONObject("playlists").getJSONArray("items").length()) {
        if (i > 2)
            break
        PlaylistRow(page, result.getJSONObject("playlists").getJSONArray("items").getJSONObject(i))
    }

    // Videos
    Text(
            modifier = Modifier.padding(top=10.dp, bottom=10.dp),
            text = "Videos",
            style = MaterialTheme.typography.h2
    )
    for (i in 0 until result.getJSONObject("videos").getJSONArray("items").length()) {
        if (i > 2)
            break
        VideoRow(page, result.getJSONObject("videos").getJSONArray("items").getJSONObject(i))
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
    // Construct the artist roles
    val roles = ArrayList<String>()
    for(i in 0 until (artist["artistRoles"] as JSONArray).length())
        roles.add(artist.getJSONArray("artistRoles").getJSONObject(i)["category"] as String)

    RowTemplate(
            imageUrl = if(!artist.isNull("picture")) artist["picture"] as String else "",
            text1 = artist["name"] as String,
            text2 = roles.joinToString(", "),
            text3 = "",
            rounded = true,
            iconId = R.drawable.ic_more,
            onClick = {

            },
            onIconClick = {

            }
    )
}

@Composable
fun AlbumRow(page: MutableState<Screen>, album: JSONObject) {
    // Construct the artist list
    val artists = ArrayList<String>()
    for(i in 0 until (album["artists"] as JSONArray).length())
        artists.add(album.getJSONArray("artists").getJSONObject(i)["name"] as String)

    RowTemplate(
            imageUrl = album["cover"] as String,
            text1 = album["title"] as String,
            text2 = artists.joinToString(", "),
            text3 = (album["releaseDate"] as String).substring(0, 4),
            iconId = R.drawable.ic_more,
            onClick = {

            },
            onIconClick = {

            }
    )
}

@Composable
fun TrackRow(page: MutableState<Screen>, track: JSONObject) {
    // Construct the artist list
    val artists = ArrayList<String>()
    for(i in 0 until (track["artists"] as JSONArray).length())
        artists.add(track.getJSONArray("artists").getJSONObject(i)["name"] as String)

    // Construct the flags list
    val flags = ArrayList<String>()
    if(track["explicit"] as Boolean)
        flags.add("EXPLICIT")
    if(track["audioQuality"] as String == "HI_RES")
        flags.add("MASTER")

    RowTemplate(
            imageUrl = track.getJSONObject("album")["cover"] as String,
            text1 = track["title"] as String,
            text2 = artists.joinToString(", "),
            text3 = flags.joinToString(" / "),
            iconId = R.drawable.ic_more,
            onClick = {

            },
            onIconClick = {

            }
    )
}

@Composable
fun PlaylistRow(page: MutableState<Screen>, playlist: JSONObject) {
    RowTemplate(
            imageUrl = playlist["squareImage"] as String,
            text1 = playlist["title"] as String,
            text2 = if(playlist.getJSONObject("creator").has("name")) playlist.getJSONObject("creator")["name"] as String else "TIDAL",
            text3 = "${playlist["numberOfTracks"] as Int} TRACKS",
            iconId = R.drawable.ic_more,
            onClick = {

            },
            onIconClick = {

            }
    )
}

@Composable
fun VideoRow(page: MutableState<Screen>, video: JSONObject) {
    // Construct the artist list
    val artists = ArrayList<String>()
    for(i in 0 until (video["artists"] as JSONArray).length())
        artists.add(video.getJSONArray("artists").getJSONObject(i)["name"] as String)

    // Construct the duration
    val duration = video["duration"] as Int
    val hours = duration / 3600
    val minutes = duration.rem(3600) / 60
    val seconds = duration.rem(60)

    var durationString = ""
    if(hours > 0)
        durationString = "${hours}HR ${minutes}MIN"
    else
        durationString = "${minutes}MIN ${seconds}SEC"

    RowTemplate(
            imageUrl = video["imageId"] as String,
            text1 = video["title"] as String,
            text2 = artists.joinToString(", "),
            text3 = durationString,
            iconId = R.drawable.ic_more,
            onClick = {

            },
            onIconClick = {

            }
    )
}

@Composable
fun RowTemplate(
        imageUrl: String,
        text1: String,
        text2: String,
        text3: String,
        rounded: Boolean = false,
        @DrawableRes iconId: Int,
        onClick: () -> Unit,
        onIconClick: () -> Unit
) {
    Row(
            modifier = Modifier.height(80.dp) + Modifier.fillMaxWidth()
                    + Modifier.padding(bottom = 20.dp)
                    + Modifier.clickable(onClick = {onClick()}),
            verticalGravity = Alignment.CenterVertically
    ) {
        if(imageUrl.isNotEmpty()) {
            val loadPictureState = loadPicture(TidalManager.IMAGE_URL.format(imageUrl.replace("-", "/"), 160, 160))

            if (loadPictureState is UiState.Success<Bitmap>)
                Surface(
                        shape = if(rounded) RoundedCornerShape(50) else RectangleShape
                ) {
                    Image(
                            modifier = Modifier.aspectRatio(1f),
                            asset = loadPictureState.data.asImageAsset(),
                            contentScale = ContentScale.FillHeight
                    )
                }
            else
                Image(
                        modifier = Modifier.aspectRatio(1f),
                        asset = imageResource(id = R.drawable.emptycover),
                        contentScale = ContentScale.FillHeight
                )
        }
        else {
            Image(
                    modifier = Modifier.aspectRatio(1f),
                    asset = imageResource(id = R.drawable.emptycover),
                    contentScale = ContentScale.FillHeight
            )
        }

        Column(
                modifier = Modifier.padding(start = 20.dp, end = 20.dp) + Modifier.weight(1f, true)
        ) {
            Text(
                    text = text1,
                    style = MaterialTheme.typography.body1,
                    color = Color.White,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
            )

            Text(
                    text = text2,
                    style = MaterialTheme.typography.body2,
                    color = Color.LightGray,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
            )

            if(text3.isNotEmpty())
                Text(
                        modifier = Modifier.padding(top=5.dp),
                        text = text3,
                        style = MaterialTheme.typography.subtitle1,
                        color = MaterialTheme.colors.secondary,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                )
        }

        IconButton(onClick = {
            onIconClick()
        }) {
            Icon(vectorResource(id = iconId))
        }
    }
}
