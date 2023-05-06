package newsapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import newsapp.data.MyRepo
import newsapp.data.Status
import newsapp.model.NewsModel
import kotlinx.coroutines.launch


data class Resource<T>(val status: Status, val data: T?)

class MainViewModel : ViewModel() {

    private var _status = MutableLiveData(Status.LOADING)
    private val _newsResource: MutableLiveData<Resource<List<NewsModel>>> = MutableLiveData()
    val status: LiveData<Status> = _status
    val newsResource: LiveData<Resource<List<NewsModel>>> = _newsResource

    fun loadNews(category: String? = null, searchQuery: String? = null) {
        viewModelScope.launch {
            val loadedItems = MyRepo().getNews(category = category, searchQuery = searchQuery)
            val loadedNews = loadedItems.newsModels
            val loadedStatus = loadedItems.status
            if (loadedStatus.isNotBlank()) {
                _status.postValue(Status.LOADING)
                _newsResource.postValue(Resource(Status.OK, loadedNews))
            } else {
                _newsResource.postValue(Resource(Status.ERROR, null))
            }
        }
    }

    fun refreshNews(category: String = "general") {
        viewModelScope.launch {
            _newsResource.postValue( Resource(Status.LOADING, null))
            try {
                loadNews(category = category)
            } catch (e: Exception) {
                _newsResource.postValue(Resource(Status.ERROR, null))
            }
        }
    }
}