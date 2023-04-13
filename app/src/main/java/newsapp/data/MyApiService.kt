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

}


