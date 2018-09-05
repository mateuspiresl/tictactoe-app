package me.mateuspires.tictactoe.data.network

import me.mateuspires.tictactoe.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import rx.schedulers.Schedulers


object ImageSearchApiService {

    fun load(): ImageSearchApi {
        val builder = OkHttpClient.Builder()
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        builder.networkInterceptors().add(httpLoggingInterceptor)

        val rxAdapter: RxJavaCallAdapterFactory = RxJavaCallAdapterFactory.createWithScheduler(
                Schedulers.io())

        val retrofit = Retrofit.Builder()
                .client(builder.build())
                .baseUrl(Constants.IMAGE_SEARCH_URL)
                .addConverterFactory(GsonConverterFactory.create())
//                .addCallAdapterFactory(rxAdapter)
                .build()

        return retrofit.create(ImageSearchApi::class.java)
    }
}
