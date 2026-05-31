import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

fun main() {
    val instances = listOf(
        "https://pipedapi.kavin.rocks/",
        "https://pipedapi.us.projectsegfau.lt/",
        "https://pipedapi.in.projectsegfau.lt/",
        "https://pipedapi.eu.projectsegfau.lt/",
        "https://pipedapi.asia.projectsegfau.lt/",
        "https://pi.pivpn.moe/",
        "https://pipedapi.syncpundit.io/",
        "https://pipedapi.adminforge.de/",
        "https://api.piped.privacydev.net/",
        "https://piped-api.lunar.icu/",
        "https://api.piped.mint.lgbt/",
        "https://pipedapi.smnz.de/",
        "https://pipedapi.tokhmi.xyz/",
        "https://piped-api.privacy.com.de/",
        "https://pipedapi.ytmnd.com/",
        "https://api.piped.bz/",
        "https://pipedapi.1337.cx/",
        "https://api-piped.mha.fi/"
    )

    for (urlStr in instances) {
        try {
            val endpoint = "${urlStr}trending?region=US"
            val url = URL(endpoint)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("User-Agent", "Mozilla/5.0")
            connection.connectTimeout = 3000
            connection.readTimeout = 3000
            val code = connection.responseCode
            val contentType = connection.contentType
            println("$urlStr -> $code $contentType")
        } catch (e: Exception) {
            println("$urlStr -> ${e.javaClass.simpleName}: ${e.message}")
        }
    }
}
