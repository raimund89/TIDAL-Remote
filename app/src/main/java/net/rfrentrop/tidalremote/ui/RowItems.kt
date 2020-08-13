package net.rfrentrop.tidalremote.ui

import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import androidx.compose.Composable
import androidx.compose.state
import androidx.ui.core.Alignment
import androidx.ui.core.ContentScale
import androidx.ui.core.Modifier
import androidx.ui.foundation.*
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.Color
import androidx.ui.graphics.RectangleShape
import androidx.ui.graphics.asImageAsset
import androidx.ui.layout.*
import androidx.ui.material.Button
import androidx.ui.material.IconButton
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Surface
import androidx.ui.res.imageResource
import androidx.ui.res.vectorResource
import androidx.ui.text.style.TextAlign
import androidx.ui.text.style.TextOverflow
import androidx.ui.unit.IntSize
import androidx.ui.unit.dp
import net.rfrentrop.tidalremote.MainActivity
import net.rfrentrop.tidalremote.R
import net.rfrentrop.tidalremote.tidalapi.StreamType
import net.rfrentrop.tidalremote.tidalapi.TidalManager
import net.rfrentrop.tidalremote.tidalapi.loadPicture
import org.json.JSONArray
import org.json.JSONObject

@Composable
fun RowArtist(activity: MainActivity, artist: JSONObject) {
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
                activity.navigate(Screen.Page, PageType.ARTIST, artist.getInt("id").toString())
            },
            onIconClick = {

            }
    )
}

@Composable
fun RowAlbum(activity: MainActivity, album: JSONObject) {
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
                activity.navigate(Screen.Page, PageType.ALBUM, album.getInt("id").toString())
            },
            onIconClick = {

            }
    )
}

@Composable
fun RowTrack(activity: MainActivity, track: JSONObject, covers: Boolean = true, number: Boolean = false) {
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

    val clickState = state {false}
    
    RowTemplate(
            imageUrl = if(covers && !number) track.getJSONObject("album")["cover"] as String else "",
            number = if(number) track.getInt("trackNumber") else 0,
            text1 = track["title"] as String,
            text2 = artists.joinToString(", "),
            text3 = flags.joinToString(" / "),
            iconId = R.drawable.ic_more,
            onClick = {
                clickState.value = true
            },
            onIconClick = {

            }
    )

    if(clickState.value)
        Dialog(
            onCloseRequest = {
                clickState.value = false
            }
        ) {
            Column(
                modifier = Modifier.fillMaxWidth() + Modifier.preferredHeight(150.dp)
            ) {
                Button(
                    modifier = Modifier.height(50.dp) + Modifier.fillMaxWidth(),
                    onClick = {
                        activity.manager.getStreamingUrl(StreamType.TRACK, track.getInt("id").toString()) { url ->
                            activity.player.playTrackNow(track, url)
                        }
                    }
                ) {
                    Text(text = "Play now")
                }
                Button(
                    modifier = Modifier.height(50.dp) + Modifier.fillMaxWidth(),
                    onClick = {
                        activity.manager.getStreamingUrl(StreamType.TRACK, track.getInt("id").toString()) { url ->
                            activity.player.playTrackNext(track, url)
                        }
                    }
                ) {
                    Text(text = "Play next")
                }
                Button(
                    modifier = Modifier.height(50.dp) + Modifier.fillMaxWidth(),
                    onClick = {
                        activity.manager.getStreamingUrl(StreamType.TRACK, track.getInt("id").toString()) { url ->
                            activity.player.queueTrack(track, url)
                        }
                    }
                ) {
                    Text(text = "Add to queue")
                }
            }
        }
}

@Composable
fun RowPlaylist(activity: MainActivity, playlist: JSONObject) {
    RowTemplate(
            imageUrl = playlist["squareImage"] as String,
            text1 = playlist["title"] as String,
            text2 = if(playlist.getJSONObject("creator").has("name")) playlist.getJSONObject("creator")["name"] as String else "TIDAL",
            text3 = "${playlist["numberOfTracks"] as Int} TRACKS",
            iconId = R.drawable.ic_more,
            onClick = {
                activity.navigate(Screen.Playlist, PageType.NONE, playlist.getString("uuid"))
            },
            onIconClick = {

            }
    )
}

@Composable
fun RowVideo(activity: MainActivity, video: JSONObject) {
    // Construct the artist list
    val artists = ArrayList<String>()
    for(i in 0 until (video["artists"] as JSONArray).length())
        artists.add(video.getJSONArray("artists").getJSONObject(i)["name"] as String)

    // Construct the duration
    val duration = video["duration"] as Int
    val hours = duration / 3600
    val minutes = duration.rem(3600) / 60
    val seconds = duration.rem(60)

    val durationString = if(hours > 0)
        "${hours}HR ${minutes}MIN"
    else
        "${minutes}MIN ${seconds}SEC"

    RowTemplate(
            imageUrl = video["imageId"] as String,
            text1 = video["title"] as String,
            text2 = artists.joinToString(", "),
            text3 = durationString,
            iconId = R.drawable.ic_more,
            onClick = {
                // TODO: Implement
            },
            onIconClick = {

            }
    )
}

@Composable
fun RowTemplate(
    imageUrl: String = "",
    imageSize: IntSize = IntSize(160, 160),
    number: Int = 0,
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
            val loadPictureState = loadPicture(TidalManager.IMAGE_URL.format(imageUrl.replace("-", "/"), imageSize.width, imageSize.height))

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
        else if(number != 0) {
            Text(
                modifier = Modifier.width(30.dp),
                textAlign = TextAlign.Right,
                text = "$number."
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
