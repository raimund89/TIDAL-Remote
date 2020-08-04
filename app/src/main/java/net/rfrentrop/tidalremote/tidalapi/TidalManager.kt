package net.rfrentrop.tidalremote.tidalapi

import android.content.Context
import android.util.Log
import com.android.volley.toolbox.Volley
import net.rfrentrop.tidalremote.MainActivity

class TidalManager (
    val context: MainActivity
) {

    companion object{
        const val API_LOCATION = "https://api.tidalhifi.com/v1/"
        const val IMAGE_URL = "https://resources.tidal.com/images/%s/%ix%i.jpg"
    }

    lateinit var apiToken: String
    lateinit var username: String
    lateinit var password: String

    var sessionId = ""
    var countryCode = ""
    lateinit var user: TidalUser

    private val queue = Volley.newRequestQueue(context)

    fun init(user: TidalUser) {
        val preferences = context.getPreferences(Context.MODE_PRIVATE)
        apiToken = preferences.getString("api_token", "") ?: ""
        username = preferences.getString("username", "") ?: ""
        password = preferences.getString("password", "") ?: ""

        this.user = user
    }

    fun login() {
        if(apiToken.isBlank() || username.isBlank() || password.isBlank())
            return

        val headers = HashMap<String, String>()
        headers["X-Tidal-Token"] = apiToken

        val params = HashMap<String, String>()
        params["username"] = username
        params["password"] = password

        val request = TidalRequest(
            API_LOCATION + "login/username",
            headers,
            params,
            { response ->
                user.username = username
                user.userId = response.getInt("userId")
                sessionId = response.getString("sessionId")
                countryCode = response.getString("countryCode")
                Log.d("TidalManager", this.toString())
            },
            {
                it.printStackTrace()
            }
        )

        queue.add(request)
    }

    fun relogin() {
        init(this.user)
        login()
    }

    override fun toString(): String {
        return "Session ID: $sessionId, Country Code: $countryCode, Username: ${user.username}"
    }
}
