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
import androidx.ui.layout.Column
import androidx.ui.layout.aspectRatio
import androidx.ui.layout.padding
import androidx.ui.layout.width
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Surface
import androidx.ui.res.imageResource
import androidx.ui.text.style.TextOverflow
import androidx.ui.unit.dp
import net.rfrentrop.tidalremote.R
import net.rfrentrop.tidalremote.tidalapi.TidalManager
import org.json.JSONArray
import org.json.JSONObject

@Composable
fun PageArtistItem(item: JSONObject) {

    // Construct the artist roles
    val roles = ArrayList<String>()
    for (i in 0 until (item["artistRoles"] as JSONArray).length())
        roles.add(item.getJSONArray("artistRoles").getJSONObject(i)["category"] as String)

    PageItem(
        imageUrl = item.getString("picture"),
        rounded = true,
        text1 = item["name"] as String,
        text2 = roles.joinToString(", "),
        onClick = {

        }
    )
}

@Composable
fun PageAlbumItem(item: JSONObject) {

    // Construct the artist list
    val artists = ArrayList<String>()
    for(i in 0 until (item["artists"] as JSONArray).length())
        artists.add(item.getJSONArray("artists").getJSONObject(i)["name"] as String)

    PageItem(
        imageUrl = item.getString("cover"),
        text1 = item["title"] as String,
        text2 = artists.joinToString(", "),
        text3 = if(!item.isNull("releaseDate")) (item["releaseDate"] as String).substring(0, 4) else "",
        onClick = {
            // TODO: Implement
        }
    )
}

@Composable
fun PageItem(
    imageUrl: String,
    rounded: Boolean = false,
    text1: String,
    text2: String,
    text3: String = "",
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.width(180.dp) + Modifier.padding(end = 20.dp) + Modifier.clickable(onClick = {onClick()}),
    ) {
        val loadPictureState = loadPicture(TidalManager.IMAGE_URL.format(imageUrl.replace("-", "/"), 320, 320))

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
