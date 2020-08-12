package net.rfrentrop.tidalremote.screens

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.ContentScale
import androidx.ui.core.Modifier
import androidx.ui.foundation.*
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
import net.rfrentrop.tidalremote.ui.RowTemplate
import net.rfrentrop.tidalremote.ui.UiState
import net.rfrentrop.tidalremote.ui.currentData
import net.rfrentrop.tidalremote.ui.loading
import org.json.JSONArray

@Composable
fun ScreenPlaylist(activity: MainActivity) {
    val (pageState, refreshPage) = activity.manager.getPlaylist()

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
        val details = pageState.currentData?.getJSONObject("details")
        val items = pageState.currentData?.getJSONObject("items")?.getJSONArray("items")

        ScrollableColumn {
            if(details != null) {
                Column {
                    val loadPictureState = loadPicture(
                        TidalManager.IMAGE_URL.format(
                            details.getString("squareImage").replace("-", "/"), 750, 750
                        )
                    )

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
                        modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 10.dp)
                    ) {
                        Column {
                            Text(
                                text = details.getString("title"),
                                color = Color.White,
                                style = MaterialTheme.typography.h2,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            var creator = ""
                            creator = if (details.getJSONObject("creator").has("name"))
                                "by " + details.getJSONObject("creator")["name"] as String
                            else
                                "by TIDAL"

                            Text(
                                text = creator,
                                color = Color.LightGray,
                                style = MaterialTheme.typography.body2,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // TODO: Add 'like' button
                    }

                    Text(
                        modifier = Modifier.padding(10.dp),
                        text = details.getString("description"),
                        color = Color.LightGray,
                        style = MaterialTheme.typography.body2
                    )

                    val numberOf = if((details["numberOfTracks"] as Int) == 0)
                        "${details["numberOfVideos"] as Int} VIDEOS"
                    else
                        "${details["numberOfTracks"] as Int} TRACKS"

                    Text(
                        modifier = Modifier.padding(10.dp),
                        text = numberOf,
                        color = Color.LightGray,
                        style = MaterialTheme.typography.body2
                    )

                    // TODO: These rows are actually coming from the "playbackControls" JSONArray item
                    Row(
                        modifier = Modifier.padding(
                            start = 10.dp,
                            end = 10.dp,
                            top = 10.dp,
                            bottom = 20.dp
                        )
                    ) {
                        Surface(
                            color = Color.DarkGray,
                            shape = RoundedCornerShape(5.dp),
                            modifier = Modifier.weight(1f, true) + Modifier.padding(end = 10.dp)
                        ) {
                            Text(
                                text = "Play",
                                style = MaterialTheme.typography.body2,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 10.dp, bottom = 10.dp)
                            )
                        }
                        Surface(
                            color = Color.DarkGray,
                            shape = RoundedCornerShape(5.dp),
                            modifier = Modifier.weight(1f, true) + Modifier.padding(end = 10.dp)
                        ) {
                            Text(
                                text = "Shuffle",
                                style = MaterialTheme.typography.body2,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 10.dp, bottom = 10.dp)
                            )
                        }
                    }
                }
            }

            if(items != null) {
                Column(
                    modifier = Modifier.padding(start = 10.dp, end=10.dp)
                ) {
                    Log.d("ScreenPlaylist", "Number of tracks: ${items.length()}")

                    for (i in 0 until items.length()) {
                        val item = items.getJSONObject(i).getJSONObject("item")

                        // Construct the artist list
                        val artists = ArrayList<String>()
                        for (i in 0 until (item["artists"] as JSONArray).length())
                            artists.add(item.getJSONArray("artists").getJSONObject(i)["name"] as String)

                        // Construct the flags list
                        val flags = ArrayList<String>()
                        if (item["explicit"] as Boolean)
                            flags.add("EXPLICIT")
                        if (item["audioQuality"] as String == "HI_RES")
                            flags.add("MASTER")

                        RowTemplate(
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
        }
    }
}
