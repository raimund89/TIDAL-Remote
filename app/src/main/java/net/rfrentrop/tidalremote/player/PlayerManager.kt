package net.rfrentrop.tidalremote.player

import android.util.Log
import net.rfrentrop.tidalremote.MainActivity
import okhttp3.*
import org.json.JSONObject
import java.net.InetAddress
import javax.net.ssl.HostnameVerifier

data class PlayerHost(
        val host: InetAddress,
        val port: Int,
        val name: String,
        val version: String
)

// TODO: Add calls for: forward, rewind, remove, shuffle, seek, move in playlist
// TODO: Add video calls for: play, pause, resume, stop, previous, next, forward, rewind, add, remove, add at position

// TODO: Automatically reconnect after a host temporarily disconnects
// Reconnection works when controlled by the phone. But if a websocket suddenly closes, reconnection needs to be initiated
// Also set timeouts for retries

class PlayerManager(
        private val context: MainActivity
): WebSocketListener(){

        private val client = OkHttpClient
                .Builder()
                .retryOnConnectionFailure(true)
                .hostnameVerifier(HostnameVerifier { s, sslSession -> return@HostnameVerifier true })
                .build()
        var ws: WebSocket? = null

        private var currentPlayer: PlayerHost? = null
        private var currentPlaylist = mutableListOf<JSONObject>()
        private var currentTrack: JSONObject? = null
        private var currentPosition = 0

        private var previousPlayer: PlayerHost? = null

        fun getCurrentTrack(): JSONObject? {
                return currentTrack
        }

        fun getCurrentPlaylist(): List<JSONObject> {
                return currentPlaylist
        }

        fun getCurrentPlayer(): PlayerHost? {
                return currentPlayer
        }

        fun getCurrentPosition(): Int {
                return currentPosition
        }

        fun reconnectToPlayer() {
                previousPlayer?.let {
                        connectToPlayer(it)
                }
        }

        fun connectToPlayer(player: PlayerHost) {
                Log.e("PlayerManager", "Trying to connect to websocket with URL wss://${player.host.hostAddress}:${player.port}.")

                val request = Request
                        .Builder()
                        .url("wss://${player.host.hostAddress}:${player.port}")
                        .build()
                ws = client.newWebSocket(request, PlayerSocketListener())

                currentPlayer = player
                previousPlayer = player
                context.currentPlayer.value = player
        }

        fun disconnectFromPlayer() {
                currentPlayer?.let {
                        ws?.close(1000, null)
                }
        }

        inner class PlayerSocketListener: WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                        Log.e("PlayerManager", "Connected!")
                        // TODO: Update the current playlist and currently playing track
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                        // TODO: Received a message, which is probably the current state of affairs
                        if(text.isEmpty())
                                return

                        val payload = JSONObject(text)

                        if(payload.has("playlist")) {
                                currentPlaylist = if(payload.isNull("playlist"))
                                        mutableListOf()
                                else
                                        MutableList(payload.getJSONArray("payload").length()) {
                                                payload.getJSONArray("payload")[it] as JSONObject
                                        }
                        }

                        currentTrack = if(payload.has("currentTrack")) {
                                payload.getJSONObject("currentTrack")
                        } else {
                                null
                        }

                        currentPosition = if(payload.has("position"))
                                payload.getInt("position")
                        else
                                0
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                        Log.e("On failure", response.toString())
                        t.printStackTrace()
                }

                override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                        webSocket.close(1000, null)
                        webSocket.cancel()

                        // TODO: Call a callback, which shows the player disconnected
                        // TODO: Clear playlist and currently playing

                        currentPlayer = null
                        currentPlaylist.clear()
                        currentTrack = null
                        currentPosition = 0
                        context.currentPlayer.value = PlayerHost(InetAddress.getLoopbackAddress(), 22, "<No Player>", "0.0")
                }

                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                        currentPlayer = null
                        currentPlaylist.clear()
                        currentTrack = null
                        currentPosition = 0
                        context.currentPlayer.value = PlayerHost(InetAddress.getLoopbackAddress(), 22, "<No Player>", "0.0")
                }
        }

        fun playTrackNow(track: JSONObject, url: String) {
                val payload = JSONObject()
                payload.put("command", "playnow")
                payload.put("track", track)
                payload.put("url", url)

                ws?.send(payload.toString())
        }

        fun playTrackNext(track: JSONObject, url: String) {
                val payload = JSONObject()
                payload.put("command", "playnext")
                payload.put("track", track)
                payload.put("url", url)

                ws?.send(payload.toString())
        }

        fun queueTrack(track: JSONObject, url: String) {
                val payload = JSONObject()
                payload.put("command", "queue")
                payload.put("track", track)
                payload.put("url", url)

                ws?.send(payload.toString())
        }

        fun removeTrack(position: Int) {
                val payload = JSONObject()
                payload.put("command", "remove")
                payload.put("position", position)

                ws?.send(payload.toString())
        }

        fun playTrack() {
                val payload = JSONObject()
                payload.put("command", "play")

                ws?.send(payload.toString())
        }

        fun pauseTrack() {
                val payload = JSONObject()
                payload.put("command", "pause")

                ws?.send(payload.toString())
        }

        fun previousTrack() {
                val payload = JSONObject()
                payload.put("command", "previous")

                ws?.send(payload.toString())
        }

        fun nextTrack() {
                val payload = JSONObject()
                payload.put("command", "next")

                ws?.send(payload.toString())
        }
}
