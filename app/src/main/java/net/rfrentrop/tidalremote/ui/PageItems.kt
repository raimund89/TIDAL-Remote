package net.rfrentrop.tidalremote.ui

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.Composable
import androidx.ui.core.ContentScale
import androidx.ui.core.Modifier
import androidx.ui.foundation.Image
import androidx.ui.foundation.Text
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.Color
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

@Composable
fun PageAlbumItem(item: JSONObject) {
    Log.d("PageItems", item.toString())
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
            text = if(!item.isNull("releaseDate")) (item["releaseDate"] as String).substring(0, 4) else "",
            style = MaterialTheme.typography.subtitle1,
            color = MaterialTheme.colors.secondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
