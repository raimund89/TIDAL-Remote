package net.rfrentrop.tidalremote.player

import net.rfrentrop.tidalremote.MainActivity
import okhttp3.*
import org.json.JSONObject
import java.net.InetAddress

// TODO: Implement ipv6?

data class PlayerHost(
        val host: InetAddress,
        val port: Int,
        val name: String,
        val version: String
)

// TODO: Add calls for: play, pause, resume, stop, previous, next, forward, rewind, add, remove, shuffle, add at position
// TODO: Add video calls for: play, pause, resume, stop, previous, next, forward, rewind, add, remove, add at position

class PlayerManager(
        context: MainActivity
): WebSocketListener(){

        private val client = OkHttpClient()
        var ws: WebSocket? = null

        private var currentPlayer: PlayerHost? = null

        fun connectToPlayer(player: PlayerHost) {
                val request = Request.Builder().url("ws://${player.host.hostAddress}:${player.port}").build()
                ws = client.newWebSocket(request, playerSocketListener)

                client.dispatcher.executorService.shutdown()
        }

        fun disconnectFromPlayer() {
                currentPlayer?.let {
                        ws?.close(1000, null)
                }

                currentPlayer = null
        }

        fun sendCommand(command: String) {
                // TODO: Further implement this
                ws?.send(command)
        }

        private val playerSocketListener = object : WebSocketListener() {

                override fun onOpen(webSocket: WebSocket, response: Response) {
                        // TODO: Call a callback, which shows a player is connected
                        // TODO: Update the current playlist and currently playing track
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                        // TODO: Received a message, which is probably the current state of affairs
                }

                override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                        webSocket.close(1000, null)
                        webSocket.cancel()

                        // TODO: Call a callback, which shows the player disconnected
                        // TODO: Clear playlist and currently playing

                        currentPlayer = null
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
}
