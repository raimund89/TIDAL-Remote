package net.rfrentrop.tidalremote.screens

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import androidx.annotation.DrawableRes
import androidx.compose.*
import androidx.ui.core.Alignment
import androidx.ui.core.ContentScale
import androidx.ui.core.ContextAmbient
import androidx.ui.core.Modifier
import androidx.ui.foundation.*
import androidx.ui.graphics.Color
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
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import net.rfrentrop.tidalremote.R
import net.rfrentrop.tidalremote.tidalapi.TidalManager
import net.rfrentrop.tidalremote.ui.Screen
import net.rfrentrop.tidalremote.ui.UiState
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
    // Construct the artist roles
    val roles = ArrayList<String>()
    for(i in 0 until (artist["artistRoles"] as JSONArray).length())
        roles.add(artist.getJSONArray("artistRoles").getJSONObject(i)["category"] as String)

    RowTemplate(
            imageUrl = if(!artist.isNull("picture")) artist["picture"] as String else "",
            text1 = artist["name"] as String,
            text2 = roles.joinToString(", "),
            text3 = "",
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
        @DrawableRes iconId: Int,
        onClick: () -> Unit,
        onIconClick: () -> Unit
) {
    Row(
            modifier = Modifier.height(70.dp) + Modifier.fillMaxWidth() + Modifier.padding(bottom=10.dp),
            verticalGravity = Alignment.CenterVertically
    ) {
        if(imageUrl.isNotEmpty()) {
            val loadPictureState = loadPicture(TidalManager.IMAGE_URL.format(imageUrl.replace("-", "/"), 160, 160))

            if (loadPictureState is UiState.Success<Bitmap>)
                Image(
                        modifier = Modifier.aspectRatio(1f),
                        asset = loadPictureState.data.asImageAsset(),
                        contentScale = ContentScale.FillHeight
                )
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
                modifier = Modifier.padding(start = 10.dp, end = 10.dp) + Modifier.weight(1f, true)
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

        Spacer(modifier = Modifier.width(10.dp))
    }
}

@Composable
fun loadPicture(url: String): UiState<Bitmap> {
    var bitmapState: UiState<Bitmap> by state { UiState.Loading }

    Glide.with(ContextAmbient.current)
            .asBitmap()
            .load(url)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    bitmapState = UiState.Success(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) { }
            })

    return bitmapState
}
