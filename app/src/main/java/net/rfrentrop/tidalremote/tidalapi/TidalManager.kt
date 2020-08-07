package net.rfrentrop.tidalremote.tidalapi

import android.content.Context
import androidx.compose.MutableState
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import net.rfrentrop.tidalremote.MainActivity
import org.json.JSONObject

class TidalManager (
    private val context: MainActivity
) {

    companion object{
        const val API_LOCATION = "https://api.tidalhifi.com/v1/"
        const val IMAGE_URL = "https://resources.tidal.com/images/%s/%dx%d.jpg"
    }

    lateinit var apiToken: String
    lateinit var username: String
    lateinit var password: String

    var sessionId = ""
    var countryCode = ""
    lateinit var user: TidalUser

    private val queue = Volley.newRequestQueue(context)

    var currentArtist = -1
    var currentAlbum = ""
    var currentPlaylist = ""
    var currentVideo = ""
    var currentTrack = ""
    var currentMix = ""

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
            url = API_LOCATION + "login/username",
            headers = headers,
            params = params,
            listener = { response ->
                user.username = username
                user.userId = response.getInt("userId")
                user.loggedIn = true
                sessionId = response.getString("sessionId")
                countryCode = response.getString("countryCode")
            },
            errorListener = {
                it.printStackTrace()
            }
        )

        queue.add(request)
    }

    fun relogin() {
        user.loggedIn = false
        init(this.user)
        login()
    }

    fun requestParams(): HashMap<String, String> {
        val params = HashMap<String, String>()
        params["sessionId"] = sessionId
        params["countryCode"] = countryCode
        params["deviceType"] = "BROWSER"
        params["locale"] = "en_US"
        return params
    }

    fun search(text: String, depot: MutableState<JSONObject>) {
        val params = requestParams()
        params["limit"] = "3"
        params["offset"] = "0"
        params["includeContributors"] = "true"
        params["types"] = "ARTISTS,ALBUMS,TRACKS,PLAYLISTS,VIDEOS"
        params["query"] = text

        val request = TidalRequest(
            meth = Request.Method.GET,
            url = API_LOCATION + "search",
            headers = null,
            params = params,
            listener = { response ->
                depot.value = response
            },
            errorListener = {
                it.printStackTrace()
            }
        )

        queue.add(request)
    }

    fun getExplore(depot: MutableState<JSONObject>) {
        val params = requestParams()

        val request = TidalRequest(
            meth = Request.Method.GET,
            url = API_LOCATION + "pages/explore",
            headers = null,
            params = params,
            listener = { response ->
                depot.value = response
            },
            errorListener = {
                it.printStackTrace()
            }
        )

        queue.add(request)
    }

    fun getHome(depot: MutableState<JSONObject>) {
        val params = requestParams()

        val request = TidalRequest(
            meth = Request.Method.GET,
            url = API_LOCATION + "pages/home",
            headers = null,
            params = params,
            listener = { response ->
                depot.value = response
            },
            errorListener = {
                it.printStackTrace()
            }
        )

        queue.add(request)
    }

    fun getVideos(depot: MutableState<JSONObject>) {
        val params = requestParams()

        val request = TidalRequest(
                meth = Request.Method.GET,
                url = API_LOCATION + "pages/videos",
                headers = null,
                params = params,
                listener = { response ->
                    depot.value = response
                },
                errorListener = {
                    it.printStackTrace()
                }
        )

        queue.add(request)
    }

    fun getFavorites(depot: MutableState<JSONObject>, category: String = "") {
        val params = requestParams()

        val request = TidalRequest(
                meth = Request.Method.GET,
                url = API_LOCATION + "users/${user.userId}/${if(category.isNotEmpty()) category else "favorites/ids"}",
                headers = null,
                params = params,
                listener = { response ->
                    depot.value = response
                },
                errorListener = {
                    it.printStackTrace()
                }
        )

        queue.add(request)
    }

    fun setArtist(artistId: Int) {
        currentArtist = artistId
    }

    fun getArtist(depot: MutableState<JSONObject>) {
        val params = requestParams()
        params["artistId"] = currentArtist.toString()

        val request = TidalRequest(
                meth = Request.Method.GET,
                url = API_LOCATION + "pages/artist",
                headers = null,
                params = params,
                listener = { response ->
                    depot.value = response
                },
                errorListener = {
                    it.printStackTrace()
                }
        )

        queue.add(request)
    }

    fun setMix(mixId: String) {
        currentMix = mixId
    }

    fun getMix(depot: MutableState<JSONObject>) {
        val params = requestParams()
        params["mixId"] = currentMix

        val request = TidalRequest(
                meth = Request.Method.GET,
                url = API_LOCATION + "pages/mix",
                headers = null,
                params = params,
                listener = { response ->
                    depot.value = response
                },
                errorListener = {
                    it.printStackTrace()
                }
        )

        queue.add(request)
    }

    override fun toString(): String {
        return "Session ID: $sessionId, Country Code: $countryCode, Username: ${user.username}"
    }
}
