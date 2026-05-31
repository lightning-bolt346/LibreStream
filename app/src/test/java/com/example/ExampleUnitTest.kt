package com.example

import org.junit.Assert.*
import org.junit.Test
import okhttp3.OkHttpClient
import okhttp3.Request

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    val instances = listOf(
        "https://pipedapi.adminforge.de",
        "https://piped-api.lunar.icu",
        "https://pipedapi.yt",
        "https://pa.il.ax",
        "https://pipedapi.ngn.tf",
        "https://pipedapi.r4fo.com",
        "https://pipedapi.astartes.nl",
        "https://pipedapi.smuglo.li",
        "https://pipedapi.buss.lol",
        "https://pipedapi.cf",
        "https://api.piped.privacydev.net"
    )
    val client = OkHttpClient.Builder().connectTimeout(3, java.util.concurrent.TimeUnit.SECONDS).build()
    for (instance in instances) {
        try {
            val req = Request.Builder().url("$instance/trending?region=US").build()
            val res = client.newCall(req).execute()
            println("$instance -> ${res.code} ${res.header("Content-Type")}")
        } catch (e: Exception) {
            println("$instance -> Exception: ${e.message}")
        }
    }
  }
}
