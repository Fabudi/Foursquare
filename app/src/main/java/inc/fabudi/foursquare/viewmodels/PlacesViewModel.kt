package inc.fabudi.foursquare.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import inc.fabudi.foursquare.R
import inc.fabudi.foursquare.database.getDatabase
import inc.fabudi.foursquare.domain.Filter
import inc.fabudi.foursquare.domain.Place
import inc.fabudi.foursquare.domain.Tip
import inc.fabudi.foursquare.repository.PlacesRepository
import kotlinx.coroutines.launch
import java.io.IOException

class PlacesViewModel(application: Application) : AndroidViewModel(application) {

    private val placesRepository = PlacesRepository(getDatabase(application))

    val places = placesRepository.places

    val filters: LiveData<List<Filter>>

    private val _places = MutableLiveData<List<Place>>()

    private val _tips = MutableLiveData<List<Tip>>()

    private var _eventNetworkError = MutableLiveData(false)

    val eventNetworkError: LiveData<Boolean>
        get() = _eventNetworkError

    private var _isNetworkErrorShown = MutableLiveData(false)

    val isNetworkErrorShown: LiveData<Boolean>
        get() = _isNetworkErrorShown

    init {
        val filtersArray =
            application.applicationContext.resources.getStringArray(R.array.categories_array)
        val filtersValuesArray =
            application.applicationContext.resources.getStringArray(R.array.categories_array_values)
        filters = MutableLiveData(filtersArray.mapIndexed { index, s ->
            Filter(
                filtersValuesArray[index].toInt(), s
            )
        })
    }

    fun getTips(fsq_id: String) = placesRepository.getTips(fsq_id)

    fun getPlaceDescription(fsq_id: String) = placesRepository.getPlaceDescription(fsq_id)

    fun getPhotos(fsq_id: String) = placesRepository.getPhotos(fsq_id)

    fun refreshDataFromRepository(token: String) {
        viewModelScope.launch {
            try {
                placesRepository.refreshPlaces(token)
                _eventNetworkError.value = false
                _isNetworkErrorShown.value = false
            } catch (networkError: IOException) {
                if (places.value.isNullOrEmpty()) _eventNetworkError.value = true
            }
        }
    }

    fun refreshDataFromRepository(token: String, fsq_id: String) {
        viewModelScope.launch {
            try {
                placesRepository.refreshTips(token, fsq_id)
                placesRepository.refreshPhotos(token, fsq_id)
                _eventNetworkError.value = false
                _isNetworkErrorShown.value = false

            } catch (networkError: IOException) {
                if (places.value.isNullOrEmpty()) _eventNetworkError.value = true
            }
        }
    }

    fun onNetworkErrorShown() {
        _isNetworkErrorShown.value = true
    }

    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PlacesViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST") return PlacesViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}