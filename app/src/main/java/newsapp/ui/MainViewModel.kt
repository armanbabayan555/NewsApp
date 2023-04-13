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

    private val _newsResource: MutableLiveData<Resource<List<NewsModel>>> = MutableLiveData()
    val newsResource: LiveData<Resource<List<NewsModel>>> = _newsResource

    fun loadNews() {
        viewModelScope.launch {
            _newsResource.postValue(Resource(Status.LOADING, null))
            val loadedItems = MyRepo().getNews()
            val loadedNews = loadedItems.newsModels
            val loadedStatus = loadedItems.status
            if (loadedStatus.isNotBlank()) {
                _newsResource.postValue(Resource(Status.OK, loadedNews))
            } else {
                _newsResource.postValue(Resource(Status.ERROR, null))
            }
        }
    }
}