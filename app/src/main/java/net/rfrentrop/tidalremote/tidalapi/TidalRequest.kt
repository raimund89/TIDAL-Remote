package net.rfrentrop.tidalremote.tidalapi

import com.android.volley.NetworkResponse
import com.android.volley.ParseError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import org.json.JSONObject
import java.nio.charset.Charset

class TidalRequest(
    url: String,
    private val headers: MutableMap<String, String>?,
    private val params: MutableMap<String, String>?,
    private val listener: Response.Listener<JSONObject>,
    errorListener: Response.ErrorListener
): Request<JSONObject>(Method.POST, url, errorListener) {
    override fun getHeaders(): MutableMap<String, String> = headers ?: super.getHeaders()

    override fun getParams(): MutableMap<String, String> = params ?: super.getParams()

    override fun deliverResponse(response: JSONObject?) {
        listener.onResponse(response)
    }

    override fun parseNetworkResponse(response: NetworkResponse?): Response<JSONObject> {
        return try {
            val text = String(
                response?.data ?: ByteArray(0),
                Charset.forName(HttpHeaderParser.parseCharset(response?.headers))
            )
            Response.success(
                JSONObject(text),
                HttpHeaderParser.parseCacheHeaders(response)
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Response.error(ParseError(e))
        }
    }
}
