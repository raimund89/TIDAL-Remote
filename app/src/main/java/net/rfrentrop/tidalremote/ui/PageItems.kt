package net.rfrentrop.tidalremote.ui

import android.graphics.Bitmap
import androidx.compose.Composable
import androidx.ui.core.ContentScale
import androidx.ui.core.Modifier
import androidx.ui.foundation.Image
import androidx.ui.foundation.Text
import androidx.ui.foundation.clickable
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.Color
import androidx.ui.graphics.RectangleShape
import androidx.ui.graphics.asImageAsset
import androidx.ui.layout.*
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Surface
import androidx.ui.res.imageResource
import androidx.ui.text.style.TextOverflow
import androidx.ui.unit.IntSize
import androidx.ui.unit.dp
import net.rfrentrop.tidalremote.MainActivity
import net.rfrentrop.tidalremote.R
import net.rfrentrop.tidalremote.tidalapi.PageType
import net.rfrentrop.tidalremote.tidalapi.TidalManager
import net.rfrentrop.tidalremote.tidalapi.loadPicture
import org.json.JSONArray
import org.json.JSONObject

@Composable
fun PageArtist(activity: MainActivity, item: JSONObject) {

    // Construct the artist roles
    val roles = ArrayList<String>()
    for (i in 0 until (item["artistRoles"] as JSONArray).length())
        roles.add(item.getJSONArray("artistRoles").getJSONObject(i)["category"] as String)

    PageTemplate(
        imageUrl = item.getString("picture"),
        rounded = true,
        text1 = item["name"] as String,
        text2 = roles.joinToString(", "),
        onClick = {
            activity.navigate(Screen.Page, PageType.ARTIST, item.getInt("id").toString())
        }
    )
}

@Composable
fun PageAlbum(activity: MainActivity, item: JSONObject) {

    // Construct the artist list
    val artists = ArrayList<String>()
    for(i in 0 until (item["artists"] as JSONArray).length())
        artists.add(item.getJSONArray("artists").getJSONObject(i)["name"] as String)

    PageTemplate(
        imageUrl = item.getString("cover"),
        text1 = item["title"] as String,
        text2 = artists.joinToString(", "),
        text3 = if(!item.isNull("releaseDate")) (item["releaseDate"] as String).substring(0, 4) else "",
        onClick = {
            activity.navigate(Screen.Page, PageType.ALBUM, item.getInt("id").toString())
        }
    )
}

@Composable
fun PagePlaylist(activity: MainActivity, item: JSONObject, creatorLabel: String = "creators") {

    // TODO: This code is wrong, TIDAL is not always caught

    var creators = ""
    if(creatorLabel == "creators") {
        if (item.getJSONArray(creatorLabel).length() > 0)
            creators = "by " + item.getJSONArray(creatorLabel).getJSONObject(0)["name"]
    }
    else if(creatorLabel == "creator") {
        if (item.getJSONObject(creatorLabel).has("name"))
            creators = "by " + item.getJSONObject(creatorLabel)["name"] as String
    }
    else
        creators = "by TIDAL"

    val numberOf = if((item["numberOfTracks"] as Int) == 0)
                        "${item["numberOfVideos"] as Int} VIDEOS"
                    else
                        "${item["numberOfTracks"] as Int} TRACKS"

    PageTemplate(
        imageUrl = item.getString("squareImage"),
        text1 = item["title"] as String,
        text2 = creators,
        text3 = numberOf,
        onClick = {
            activity.navigate(Screen.Playlist, PageType.NONE, item.getString("uuid"))
        }
    )
}

@Composable
fun PageMix(activity: MainActivity, item: JSONObject) {
    PageTemplate(
        imageUrl = item.getJSONObject("images").getJSONObject("LARGE").getString("url"),
        text1 = item["title"] as String,
        text2 = item["subTitle"] as String,
        onClick = {
            // TODO: Implement
        }
    )
}

@Composable
fun PageTrack(activity: MainActivity, item: JSONObject) {

    // Construct the duration
    val duration = item["duration"] as Int
    val hours = duration / 3600
    val minutes = duration.rem(3600) / 60
    val seconds = duration.rem(60)

    var durationString = ""
    if(hours > 0)
        durationString = "${hours}HR ${minutes}MIN"
    else
        durationString = "${minutes}MIN ${seconds}SEC"

    PageTemplate(
            imageUrl = item.getJSONObject("album").getString("cover"),
            text1 = item["title"] as String,
            text2 = item.getJSONObject("album")["title"] as String,
            text3 = durationString,
            onClick = {
                // TODO: Implement
            }
    )
}

@Composable
fun PageVideo(activity: MainActivity, item: JSONObject) {

    // Construct the artist list
    val artists = ArrayList<String>()
    for(i in 0 until (item["artists"] as JSONArray).length())
        artists.add(item.getJSONArray("artists").getJSONObject(i)["name"] as String)

    // Construct the duration
    val duration = item["duration"] as Int
    val hours = duration / 3600
    val minutes = duration.rem(3600) / 60
    val seconds = duration.rem(60)

    var durationString = ""
    if(hours > 0)
        durationString = "${hours}HR ${minutes}MIN"
    else
        durationString = "${minutes}MIN ${seconds}SEC"

    PageTemplate(
            imageUrl = item.getString("imageId"),
            imageSize = IntSize(480, 320),
            text1 = item["title"] as String,
            text2 = artists.joinToString(", "),
            text3 = durationString,
            onClick = {
                // TODO: Implement
            }
    )
}

@Composable
fun PageTemplate(
        imageUrl: String,
        imageSize: IntSize = IntSize(320, 320),
        rounded: Boolean = false,
        text1: String,
        text2: String,
        text3: String = "",
        onClick: () -> Unit
) {
    Column(
        modifier = Modifier.width(160.dp) + Modifier.height(220.dp) + Modifier.padding(end = 20.dp) + Modifier.clickable(onClick = {onClick()}),
    ) {
        val loadPictureState = if(imageUrl.startsWith("http"))
                                    loadPicture(imageUrl)
                                else
                                    loadPicture(TidalManager.IMAGE_URL.format(imageUrl.replace("-", "/"), imageSize.width, imageSize.height))

        if (loadPictureState is UiState.Success<Bitmap>)
            Surface(
                shape = if(rounded) RoundedCornerShape(50) else RectangleShape
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
            modifier = Modifier.padding(top=10.dp),
            text = text1,
            style = MaterialTheme.typography.body1,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = text2,
            style = MaterialTheme.typography.body2,
            color = Color.LightGray,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        if(text3.isNotEmpty())
            Text(
                modifier = Modifier.padding(top=5.dp),
                text = text3,
                style = MaterialTheme.typography.subtitle1,
                color = MaterialTheme.colors.secondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
    }
}
