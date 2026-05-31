package com.example

import org.junit.Test
import okhttp3.OkHttpClient
import okhttp3.Request

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    val instances = listOf(
        "https://pipedapi.kavin.rocks/",
        "https://pipedapi.in.projectsegfau.lt/",
        "https://pipedapi.us.projectsegfau.lt/",
        "https://pipedapi.asia.projectsegfau.lt/",
        "https://pipedapi.eu.projectsegfau.lt/",
        "https://piped-api.lunar.icu/",
        "https://pi.pivpn.moe/",
        "https://pipedapi.syncpundit.io/",
        "https://pipedapi.moomoo.me/",
        "https://pipedapi.privacy.com.de/",
        "https://api.piped.privacydev.net/",
        "https://api.piped.mint.lgbt/",
        "https://api.piped.projectsegfau.lt/",
        "https://pipedapi.smnz.de/",
        "https://pipedapi.adminforge.de/",
        "https://pipedapi.tokhmi.xyz/",
        "https://api.piped.bz/",
        "https://pipedapi.video.v2",
        "https://pipedapi.1337.cx/",
        "https://api-piped.mha.fi/",
        "https://piped-api.garudalinux.org/",
        "https://pipedapi.ytmnd.com/"
    )
    val client = OkHttpClient.Builder().connectTimeout(3, java.util.concurrent.TimeUnit.SECONDS).build()
    val sb = java.lang.StringBuilder()
    for (instance in instances) {
        try {
            val req = Request.Builder().url("${instance}trending?region=US")
                .header("User-Agent", "Mozilla/5.0")
                .build()
            val res = client.newCall(req).execute()
            val msg = "$instance -> ${res.code} ${res.header("Content-Type")}\n"
            println(msg)
            sb.append(msg)
        } catch (e: Exception) {
            val msg = "$instance -> Exception: ${e.message}\n"
            println(msg)
            sb.append(msg)
        }
    }
    java.io.File("piped_test.txt").writeText(sb.toString())
  }
}
