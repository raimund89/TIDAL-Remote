package net.rfrentrop.tidalremote.player

import net.rfrentrop.tidalremote.MainActivity
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.net.InetAddress

// TODO: Implement ipv6?

data class PlayerHost(
        val host: InetAddress,
        val port: Int,
        val name: String,
        val version: String
)

class PlayerManager(
        context: MainActivity
): WebSocketListener(){

        private val client = OkHttpClient()
        var ws: WebSocket? = null

        fun connectToPlayer(player: PlayerHost) {
                val request = Request.Builder().url("ws://${player.host.hostAddress}:${player.port}").build()
                val listener = PlayerSocketListener()
                ws = client.newWebSocket(request, listener)

                client.dispatcher.executorService.shutdown()
        }

        fun sendCommand(command: String) {
                // TODO: Further implement this
                ws?.send(command)
        }

        private class PlayerSocketListener: WebSocketListener() {

                override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                        webSocket.close(1000, null)
                        // TODO: The player is disconnecting for a reason
                }

        }
}
