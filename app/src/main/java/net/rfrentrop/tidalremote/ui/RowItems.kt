package net.rfrentrop.tidalremote.ui

import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.ContentScale
import androidx.ui.core.Modifier
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Image
import androidx.ui.foundation.Text
import androidx.ui.foundation.clickable
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.Color
import androidx.ui.graphics.RectangleShape
import androidx.ui.graphics.asImageAsset
import androidx.ui.layout.*
import androidx.ui.material.IconButton
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Surface
import androidx.ui.res.imageResource
import androidx.ui.res.vectorResource
import androidx.ui.text.style.TextOverflow
import androidx.ui.unit.dp
import net.rfrentrop.tidalremote.MainActivity
import net.rfrentrop.tidalremote.R
import net.rfrentrop.tidalremote.tidalapi.PageType
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
fun RowTrack(activity: MainActivity, track: JSONObject) {
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
                // TODO: Implement
            },
            onIconClick = {

            }
    )
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
                // TODO: Implement
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
                // TODO: Implement
            },
            onIconClick = {

            }
    )
}

@Composable
fun RowTemplate(
        imageUrl: String = "",
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
        else if(number != 0) {
            Text(
                modifier = Modifier.padding(start = 20.dp),
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
