package newsapp.data

import newsapp.model.NewsModel
import newsapp.model.ResponseModel
import retrofit2.Response

class MyRepo {
    suspend fun getNews(category: String? = null, searchQuery: String? = null): ResponseModel {
        val res: Response<FullJson> = when {
            category != null && searchQuery == null -> {
                RetrofitHelper.getInstance()
                    .create(MyApiService::class.java)
                    .fetchNews(country = "us", category = category)
            }
            category != null && searchQuery != null -> {
                RetrofitHelper.getInstance()
                    .create(MyApiService::class.java)
                    .fetchNews(country = "us", category = category, searchQuery = searchQuery)
            }
            category == null && searchQuery != null -> {
                RetrofitHelper.getInstance()
                    .create(MyApiService::class.java)
                    .fetchNews(country = "us", searchQuery = searchQuery)
            }
            else -> {
                RetrofitHelper.getInstance()
                    .create(MyApiService::class.java)
                    .fetchNews("us")
            }
        }

        val newsModel = res.run {
            this.body()?.articles?.map {
                NewsModel(
                    it.Source?.name ?: "",
                    it.author ?: "",
                    it.title ?: "",
                    it.urlToImage ?: "",
                    it.description ?: "",
                )
            } ?: listOf()
        }
        val status = res.body()?.status

        return ResponseModel(status = status!!, newsModels = newsModel)
    }
}