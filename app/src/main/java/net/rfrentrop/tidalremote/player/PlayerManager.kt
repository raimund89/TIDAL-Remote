package net.rfrentrop.tidalremote.player

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
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
) {

    companion object {
        const val SERVICE_TYPE = "_tidalplayer._tcp."
    }

    var players = HashMap<String, PlayerHost>()
    val nsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager
    var discoveryActive = false

    fun startDiscovery() {
        nsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener)
    }

    fun stopDiscovery() {
        if(discoveryActive)
            nsdManager.stopServiceDiscovery(discoveryListener)
        discoveryActive = false
    }

    private val discoveryListener = object : NsdManager.DiscoveryListener {

        override fun onDiscoveryStarted(regType: String) {
            discoveryActive = true
        }

        override fun onServiceFound(service: NsdServiceInfo) {
            if(service.serviceType == SERVICE_TYPE)
                nsdManager.resolveService(service, resolveListener)
        }

        override fun onServiceLost(service: NsdServiceInfo) {
            if(players.containsKey(service.serviceName))
                players.remove(service.serviceName)
        }

        override fun onDiscoveryStopped(serviceType: String) {
        }

        override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
            if(discoveryActive)
                nsdManager.stopServiceDiscovery(this)
            discoveryActive = false
        }

        override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
            if(discoveryActive)
                nsdManager.stopServiceDiscovery(this)
            discoveryActive = false
        }
    }

    private val resolveListener = object : NsdManager.ResolveListener {

        override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
        }

        override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
            // Check the list if the player id (UUID) exists
            if(players.containsKey(serviceInfo.serviceName)) {
                // Player already exists. Update the ip.
                players[serviceInfo.serviceName] = PlayerHost(serviceInfo.host,
                                                              serviceInfo.port,
                                                              serviceInfo.attributes["name"]!!.decodeToString(),
                                                              serviceInfo.attributes["version"]!!.decodeToString())
            }
        }
    }
}
