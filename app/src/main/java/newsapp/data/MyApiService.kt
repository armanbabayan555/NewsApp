package newsapp.data

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface MyApiService {

    @GET("/$VERSION/$TOP_HEADLINES")
    suspend fun fetchNews(
        @Query("apiKey") apiKey: String = API_KEY_VALUE,
        @Query("country") country: String
    ): Response<FullJson>

    @GET("/$VERSION/$TOP_HEADLINES")
    suspend fun fetchNews(
        @Query("country") country: String,
        @Query("category") category: String,
        @Query("apiKey") apiKey: String = API_KEY_VALUE
    ): Response<FullJson>

    @GET("/$VERSION/$TOP_HEADLINES")
    suspend fun fetchNews(
        @Query("country") country: String,
        @Query("category") category: String? = null,
        @Query("q") searchQuery: String? = null,
        @Query("apiKey") apiKey: String = API_KEY_VALUE
    ): Response<FullJson>

}


