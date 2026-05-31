package com.example.di

import android.content.Context
import androidx.room.Room
import com.example.db.AppDatabase
import com.example.network.PipedApi
import com.example.repository.AppRepository
import com.example.repository.ChannelRepository
import com.example.repository.PlaylistRepository
import com.example.repository.SearchRepository
import com.example.repository.SettingsRepository
import com.example.repository.SubscriptionRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class AppContainer(private val context: Context) {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val exoPlayerOkHttpClient: okhttp3.OkHttpClient by lazy {
        val trustAllCerts = arrayOf<javax.net.ssl.TrustManager>(
            object : javax.net.ssl.X509TrustManager {
                override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}
                override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}
                override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> = arrayOf()
            }
        )
        val sslContext = javax.net.ssl.SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, java.security.SecureRandom())
        val sslSocketFactory = sslContext.socketFactory

        okhttp3.OkHttpClient.Builder()
            .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as javax.net.ssl.X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .connectTimeout(5, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
            .dns(object : okhttp3.Dns {
                override fun lookup(hostname: String): List<java.net.InetAddress> {
                    val addresses = okhttp3.Dns.SYSTEM.lookup(hostname)
                    val ipv4Addresses = addresses.filter { it is java.net.Inet4Address }
                    if (ipv4Addresses.isEmpty()) {
                        return addresses // Fallback to whatever was resolved if no IPv4
                    }
                    return ipv4Addresses
                }
            })
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36")
                    .build()
                chain.proceed(request)
            }
            .build()
    }

    val okHttpClient: okhttp3.OkHttpClient by lazy {
        exoPlayerOkHttpClient.newBuilder()
            .addInterceptor(com.example.network.InstanceInterceptor())
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://pipedapi.smnz.de/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    private val pipedApi: PipedApi by lazy {
        retrofit.create(PipedApi::class.java)
    }

    private val database: AppDatabase by lazy {
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "libretube_database"
        )
        .fallbackToDestructiveMigration(true)
        .build()
    }

    val appRepository: AppRepository by lazy {
        AppRepository(pipedApi, database.watchHistoryDao())
    }

    val searchRepository: SearchRepository by lazy {
        SearchRepository(pipedApi, database.searchHistoryDao())
    }

    val subscriptionRepository: SubscriptionRepository by lazy {
        SubscriptionRepository(database.subscriptionDao())
    }

    val channelRepository: ChannelRepository by lazy {
        ChannelRepository(pipedApi)
    }

    val playlistRepository: PlaylistRepository by lazy {
        PlaylistRepository(database.playlistDao(), database.bookmarkDao())
    }

    val settingsRepository: SettingsRepository by lazy {
        SettingsRepository(context)
    }

    val playerManager: com.example.service.PlayerManager by lazy {
        com.example.service.PlayerManager(context).apply { initialize() }
    }
}
