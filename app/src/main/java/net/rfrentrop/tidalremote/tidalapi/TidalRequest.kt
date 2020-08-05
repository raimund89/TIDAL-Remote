package net.rfrentrop.tidalremote.tidalapi

import com.android.volley.NetworkResponse
import com.android.volley.ParseError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.nio.charset.Charset

class TidalRequest(
    private val meth: Int = Method.POST,
    private val url: String,
    private val headers: MutableMap<String, String>?,
    private val params: MutableMap<String, String>?,
    private val listener: Response.Listener<JSONObject>,
    errorListener: Response.ErrorListener
): Request<JSONObject>(meth, url, errorListener) {
    override fun getHeaders(): MutableMap<String, String> = headers ?: super.getHeaders()

    override fun getParams(): MutableMap<String, String> = params ?: super.getParams()

    override fun deliverResponse(response: JSONObject?) {
        listener.onResponse(response)
    }

    override fun getUrl(): String {
        if(meth == Method.GET) {
            val stringBuilder = StringBuilder(url)
            var i = 1
            params?.forEach {
                try {
                    val key = URLEncoder.encode(it.key, "UTF-8")
                    val value = URLEncoder.encode(it.value, "UTF-8")

                    val prefix = if (i == 1) "?" else "&"
                    stringBuilder.append("$prefix$key=$value")
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                }
                i++
            }
            return stringBuilder.toString()
        }
        return super.getUrl()
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
