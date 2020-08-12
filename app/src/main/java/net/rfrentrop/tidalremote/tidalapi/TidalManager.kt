package net.rfrentrop.tidalremote.tidalapi

import android.content.Context
import android.util.Log
import androidx.compose.*
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import net.rfrentrop.tidalremote.MainActivity
import net.rfrentrop.tidalremote.ui.RefreshableUiState
import net.rfrentrop.tidalremote.ui.RefreshableUiStateHandler
import net.rfrentrop.tidalremote.ui.currentData
import org.json.JSONObject

enum class PageType {
    ARTIST,
    ALBUM,
    MIX,

    NONE
}

// TODO: Move all getter functions to composables

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

    private lateinit var queue: RequestQueue

    var currentPage = PageType.NONE
    var currentId = ""

    fun init(user: TidalUser) {
        queue = Volley.newRequestQueue(context)
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

    fun setPage(type: PageType, id: String) {
        currentPage = type
        currentId = id
    }

    @Composable
    fun getPlaylist(): RefreshableUiStateHandler<JSONObject> {
        var pageState: RefreshableUiState<JSONObject> by state { RefreshableUiState.Success(data = null, loading = true) }

        fun repositoryCall(callback: (JSONObject) -> Unit) {
            val params = requestParams()

            queue.add(TidalRequest(
                meth = Request.Method.GET,
                url = API_LOCATION + "playlists/$currentId",
                headers = null,
                params = params,
                listener = { response1 ->
                    params["limit"] = if((response1["numberOfTracks"] as Int) == 0)
                        response1.getInt("numberOfVideos").toString()
                    else
                        response1.getInt("numberOfTracks").toString()

                    queue.add(TidalRequest(
                        meth = Request.Method.GET,
                        url = API_LOCATION + "playlists/$currentId/items",
                        headers = null,
                        params = params,
                        listener = { response2 ->
                            Log.d("TidalManager", "${API_LOCATION}playlists/$currentId/items")
                            Log.d("TidalManager", params.toString())
                            val ret = JSONObject()
                            ret.put("details", response1)
                            ret.put("items", response2)
                            callback(ret)
                        },
                        errorListener = {
                            // TODO: Implement the error in the UI as well
                            it.printStackTrace()
                        }
                    ))
                },
                errorListener = {
                    // TODO: Implement the error in the UI as well
                    it.printStackTrace()
                }
            ))
        }

        val refresh = {
            pageState = RefreshableUiState.Success(data = pageState.currentData, loading = true)
            repositoryCall { result ->
                pageState = RefreshableUiState.Success(data = result, loading = false)
            }
        }

        onActive {
            refresh()
        }

        return RefreshableUiStateHandler(pageState, refresh)
    }

    @Composable
    fun getPageResult(): RefreshableUiStateHandler<JSONObject> {
        var pageState: RefreshableUiState<JSONObject> by state { RefreshableUiState.Success(data = null, loading = true) }

        fun repositoryCall(callback: (JSONObject) -> Unit) {
            val params = requestParams()

            val pageName = currentPage.name.toLowerCase()

            params["${pageName}Id"] = currentId

            queue.add(TidalRequest(
                    meth = Request.Method.GET,
                    url = API_LOCATION + "pages/$pageName",
                    headers = null,
                    params = params,
                    listener = { response ->
                        callback(response)
                    },
                    errorListener = {
                        // TODO: Implement the error in the UI as well
                        it.printStackTrace()
                    }
            ))
        }

        val refresh = {
            pageState = RefreshableUiState.Success(data = pageState.currentData, loading = true)
            repositoryCall { result ->
                pageState = RefreshableUiState.Success(data = result, loading = false)
            }
        }

        onActive {
            refresh()
        }

        return RefreshableUiStateHandler(pageState, refresh)
    }

    override fun toString(): String {
        return "Session ID: $sessionId, Country Code: $countryCode, Username: ${user.username}"
    }
}
