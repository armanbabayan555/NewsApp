package newsapp.data

import com.google.gson.annotations.SerializedName

data class FullJson(
    @SerializedName("status")
    val status: String,
    @SerializedName("totalResults")
    val totalRes: String?,
    @SerializedName("articles")
    val articles: List<ArticleResponse>?
)

data class ArticleResponse(
    @SerializedName("source")
    val Source: Source?,

    @SerializedName("author")
    val author: String?,

    @SerializedName("title")
    val title: String?,

    @SerializedName("urlToImage")
    val urlToImage: String?
)

data class Source(
    @SerializedName("name")
    val name: String?
)