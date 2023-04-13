package newsapp.data

import newsapp.model.NewsModel
import newsapp.model.ResponseModel

class MyRepo {
    suspend fun getNews(): ResponseModel {
        val res = RetrofitHelper.getInstance().create(MyApiService::class.java).fetchNews(country = COUNTRY_US)
        val newsModel = res.run {
            this.body()?.articles?.map {
                NewsModel(
                    it.Source?.name ?: "",
                    it.author ?: "",
                    it.title ?: "",
                    it.urlToImage ?: ""
                )
            } ?: listOf()
        }
        val status = res.body()?.status

        return ResponseModel(status = status!!, newsModels = newsModel)
    }
}