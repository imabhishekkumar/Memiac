package dev.abhishekkumar.memeapp.api

import com.facebook.stetho.okhttp3.StethoInterceptor
import dev.abhishekkumar.memeapp.Model.Model
import dev.abhishekkumar.memeapp.ui.MainActivity
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url
import okhttp3.ResponseBody



interface ApiService {


    @GET()
    fun loadMemes(@Url url: String): Call<Model>

   
}
object RetrofitClient {
    private var retrofit: Retrofit? = null


    fun getClient(baseUrl: String): Retrofit {
        val okHttpClient = OkHttpClient.Builder()
            .addNetworkInterceptor(StethoInterceptor())
            .build()
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }
}