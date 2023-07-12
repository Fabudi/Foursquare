package inc.fabudi.foursquare.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Dao
interface PlaceDao {
    @Query("select * from databaseplace")
    fun getPlaces(): LiveData<List<DatabasePlace>>

    @Query("select * from databaseplace where fsq_id=:fsq_id")
    fun getPlace(fsq_id: String): LiveData<DatabasePlace>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(places: List<DatabasePlace>)
}


@Database(entities = [DatabasePlace::class, DatabaseTip::class, DatabasePhoto::class], version = 1)
@TypeConverters(Converters::class, TipConverter::class)
abstract class PlacesDatabase : RoomDatabase() {
    abstract val placeDao: PlaceDao
    abstract val tipsDao: TipDao
    abstract val photosDao: PhotoDao
}

private lateinit var INSTANCE: PlacesDatabase

fun getDatabase(context: Context): PlacesDatabase {
    synchronized(PlacesDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext, PlacesDatabase::class.java, "places"
            ).build()
        }
    }
    return INSTANCE
}

@Dao
interface TipDao {
    @Query("select * from databasetip where fsq_id= :fsq_id")
    fun getTips(fsq_id: String): LiveData<List<DatabaseTip>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(tips: List<DatabaseTip>)
}
@Dao
interface PhotoDao {
    @Query("select * from databasephoto where fsq_id= :fsq_id")
    fun getPhotos(fsq_id: String): LiveData<List<DatabasePhoto>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(photos: List<DatabasePhoto>)
}