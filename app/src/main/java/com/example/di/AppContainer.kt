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
        okhttp3.OkHttpClient.Builder()
            .connectTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
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
            .baseUrl("https://pipedapi.kavin.rocks/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
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
