package net.rfrentrop.tidalremote.screens

import android.graphics.Bitmap
import androidx.compose.Composable
import androidx.compose.getValue
import androidx.compose.setValue
import androidx.compose.state
import androidx.ui.core.Alignment
import androidx.ui.core.ContentScale
import androidx.ui.core.Modifier
import androidx.ui.foundation.*
import androidx.ui.graphics.Color
import androidx.ui.graphics.asImageAsset
import androidx.ui.layout.*
import androidx.ui.material.Button
import androidx.ui.material.IconButton
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Slider
import androidx.ui.res.imageResource
import androidx.ui.res.vectorResource
import androidx.ui.text.style.TextOverflow
import androidx.ui.unit.dp
import net.rfrentrop.tidalremote.MainActivity
import net.rfrentrop.tidalremote.R
import net.rfrentrop.tidalremote.tidalapi.loadPicture
import net.rfrentrop.tidalremote.ui.UiState
import org.json.JSONArray
import java.net.InetAddress

@Composable
fun ScreenPlayer(activity: MainActivity) {

    Column {
        Row(
                verticalGravity = Alignment.CenterVertically
        ) {
            IconButton(
                    modifier = Modifier.padding(20.dp),
                    onClick = {
                        // TODO: implement a sliding animation
                        activity.onBackPressed()
                    }
            ) {
                Icon(asset = vectorResource(id = R.drawable.ic_down))
            }
            Text(
                    text = "Now playing"
            )

            Spacer(modifier = Modifier.weight(1f, true))

            IconButton(
                    modifier = Modifier.padding(20.dp),
                    onClick = {
                        // TODO: Show playlist
                    }
            ) {
                Icon(asset = vectorResource(id = R.drawable.ic_queue))
            }
        }

        val track = activity.currentTrack.value
        
        if(track.length() > 0) {
            Box(
                    modifier = Modifier.padding(20.dp)
            ) {
                val loadPictureState = loadPicture(url = track.getJSONObject("album")["cover"] as String)

                if (loadPictureState is UiState.Success<Bitmap>) {
                    Image(
                            modifier = Modifier.aspectRatio(1f),
                            asset = loadPictureState.data.asImageAsset(),
                            contentScale = ContentScale.FillWidth
                    )
                } else {
                    Image(
                            modifier = Modifier.aspectRatio(1f),
                            asset = imageResource(id = R.drawable.emptycover),
                            contentScale = ContentScale.FillWidth
                    )
                }
            }

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

            Row(
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp)
            ) {
                Column(
                        modifier = Modifier.weight(1f, true)
                ) {
                    Text(
                            text = track.getString("title"),
                            style = MaterialTheme.typography.h3,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                    )

                    Text(
                            text = artists.joinToString(", "),
                            style = MaterialTheme.typography.body1,
                            color = Color.LightGray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                    )
                }

                // TODO: Add icon for favorites
            }

            var sliderPosition by state { activity.player.getCurrentPosition().toFloat() }

            Slider(
                    modifier = Modifier.padding(start=20.dp, end=20.dp),
                    value = sliderPosition,
                    onValueChange = {
                        sliderPosition = it
                        // TODO: Send the change to the player
                        // Do that in the same way as the search bar, only every 500ms or so
                    },
                    valueRange = 0f..track.getInt("duration").toFloat(),
                    color = MaterialTheme.colors.secondary,
            )

            Row(
                    modifier = Modifier.padding(start=20.dp, end=20.dp),
            ) {
                val elapsed = "${track.getInt("duration")/60}:${track.getInt("duration").rem(60)}"
                val remaining = "${(track.getInt("duration")-activity.player.getCurrentPosition())/60}:${(track.getInt("duration")-activity.player.getCurrentPosition()).rem(60)}"
                Text(
                        text = elapsed,
                        style = MaterialTheme.typography.subtitle1,
                        color = Color.LightGray
                )
                Spacer(modifier = Modifier.weight(1f, true))
                Text(
                        text = remaining,
                        style = MaterialTheme.typography.subtitle1,
                        color = Color.LightGray
                )
            }
            
            Row(
                    modifier = Modifier.padding(start=20.dp, end=20.dp),
            ) {
                IconButton(onClick = {}) {
                    Icon(asset = vectorResource(id = R.drawable.ic_shuffle))
                }
                
                Spacer(modifier = Modifier.weight(1f, true))

                IconButton(onClick = {}) {
                    Icon(asset = vectorResource(id = R.drawable.ic_previous))
                }
                IconButton(onClick = {}) {
                    Icon(asset = vectorResource(id = R.drawable.ic_play))
                }
                IconButton(onClick = {}) {
                    Icon(asset = vectorResource(id = R.drawable.ic_next))
                }
                
                Spacer(modifier = Modifier.weight(1f, true))

                IconButton(onClick = {}) {
                    Icon(asset = vectorResource(id = R.drawable.ic_repeat))
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f, true))

        Row(
                modifier = Modifier.padding(20.dp),
                verticalGravity = Alignment.CenterVertically
        ) {

            val playerlistState = state {false}

            if(playerlistState.value)
                Dialog(
                        onCloseRequest = {
                            playerlistState.value = false
                        }
                ) {
                    Column(
                            modifier = Modifier.fillMaxWidth() + Modifier.preferredHeight(150.dp)
                    ) {
                        activity.playerList.forEach { (_, playerHost) ->
                            Button(
                                    modifier = Modifier.height(50.dp) + Modifier.fillMaxWidth(),
                                    onClick = {
                                        activity.player.connectToPlayer(playerHost)
                                        playerlistState.value = false
                                    }
                            ) {
                                Text(text = playerHost.name)
                            }
                        }

                        if(activity.currentPlayer.value.host != InetAddress.getLocalHost())
                            Button(
                                    modifier = Modifier.height(50.dp) + Modifier.fillMaxWidth(),
                                    onClick = {
                                        activity.player.disconnectFromPlayer()
                                        playerlistState.value = false
                                    }
                            ) {
                                Text(text = "Disconnect")
                            }
                    }
                }

            IconButton(onClick = {
                if(activity.playerList.size > 0 || activity.currentPlayer.value.host != InetAddress.getLocalHost())
                    playerlistState.value = true
            }) {
                Icon(asset = vectorResource(id = R.drawable.ic_speaker), tint = if(!activity.playerList.isEmpty()) MaterialTheme.colors.secondary else Color.White)
            }

            Spacer(modifier = Modifier.weight(1f, true))

            val playername = if(activity.currentPlayer.value.host != InetAddress.getLocalHost())
                activity.currentPlayer.value.name
            else
                "<No Player>"

            Text(
                    text = playername,
                    style = MaterialTheme.typography.h2,
                    color = Color.White
            )

            Spacer(modifier = Modifier.weight(1f, true))

            IconButton(onClick = {
                // TODO: Implement
            }) {
                Icon(asset = vectorResource(id = R.drawable.ic_more))
            }
        }
    }
}
