package newsapp.model

data class ResponseModel(
    val status: String,
    val newsModels: List<NewsModel>
)