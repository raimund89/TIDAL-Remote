package net.rfrentrop.tidalremote.player

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log
import androidx.compose.mutableStateMapOf
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
        const val TAG = "PlayerManager"
    }

    var players = mutableStateMapOf<String, PlayerHost>()
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

        // Called as soon as service discovery begins.
        override fun onDiscoveryStarted(regType: String) {
            Log.d(TAG, "Service discovery started")
            discoveryActive = true
        }

        override fun onServiceFound(service: NsdServiceInfo) {
            // A service was found! Do something with it.
            Log.d(TAG, "Service discovery success$service")

            if(service.serviceType == SERVICE_TYPE)
                nsdManager.resolveService(service, resolveListener)
        }

        override fun onServiceLost(service: NsdServiceInfo) {
            // When the network service is no longer available.
            // Internal bookkeeping code goes here.
            Log.e(TAG, "service lost: $service")

            if(players.containsKey(service.serviceName))
                players.remove(service.serviceName)
        }

        override fun onDiscoveryStopped(serviceType: String) {
            Log.i(TAG, "Discovery stopped: $serviceType")
        }

        override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
            Log.e(TAG, "Discovery failed: Error code:$errorCode")

            if(discoveryActive)
                nsdManager.stopServiceDiscovery(this)
            discoveryActive = false
        }

        override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
            Log.e(TAG, "Discovery failed: Error code:$errorCode")

            if(discoveryActive)
                nsdManager.stopServiceDiscovery(this)
            discoveryActive = false
        }
    }

    private val resolveListener = object : NsdManager.ResolveListener {

        override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
            // Called when the resolve fails. Use the error code to debug.
            Log.d(TAG, "Resolve failed: $errorCode")
        }

        override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
            Log.d(TAG, "Resolve Succeeded. $serviceInfo")

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
