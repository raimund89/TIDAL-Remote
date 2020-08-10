package net.rfrentrop.tidalremote.player

import net.rfrentrop.tidalremote.MainActivity
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
)
