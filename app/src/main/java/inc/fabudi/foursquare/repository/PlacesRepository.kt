package inc.fabudi.foursquare.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import inc.fabudi.foursquare.database.PlacesDatabase
import inc.fabudi.foursquare.database.asDomainModel
import inc.fabudi.foursquare.domain.Photo
import inc.fabudi.foursquare.domain.Place
import inc.fabudi.foursquare.domain.Tip
import inc.fabudi.foursquare.network.PlaceNetwork
import inc.fabudi.foursquare.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PlacesRepository(private val database: PlacesDatabase) {

    val places: LiveData<List<Place>> = database.placeDao.getPlaces().map {
        it.asDomainModel()
    }

    fun getTips(fsq_id: String): LiveData<List<Tip>> {
        return database.tipsDao.getTips(fsq_id).map {
            it.asDomainModel()
        }
    }

    fun getPhotos(fsq_id: String): LiveData<List<Photo>> {
        return database.photosDao.getPhotos(fsq_id).map {
            it.asDomainModel()
        }
    }

    fun getPlaceDescription(fsq_id: String): LiveData<Place> {
        return database.placeDao.getPlace(fsq_id).map {
            it.asDomainModel()
        }
    }

    suspend fun refreshPlaces(token: String) {
        withContext(Dispatchers.IO) {
            val placesList = PlaceNetwork.places.getPlacesList(token = token)
            database.placeDao.insertAll(placesList.asDatabaseModel())
        }
    }

    suspend fun refreshTips(token: String, fsq_id: String) {
        withContext(Dispatchers.IO) {
            val tipsList = PlaceNetwork.tips.getTipsList(fsq_id, token)
            database.tipsDao.insertAll(tipsList.asDatabaseModel(fsq_id))
        }
    }

    suspend fun refreshPhotos(token: String, fsq_id: String) {
        withContext(Dispatchers.IO) {
            val photos = PlaceNetwork.photos.getPhotos(fsq_id, token)
            database.photosDao.insertAll(photos.asDatabaseModel(fsq_id))
        }
    }


}
