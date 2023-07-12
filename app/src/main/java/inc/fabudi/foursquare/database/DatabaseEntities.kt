package inc.fabudi.foursquare.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import inc.fabudi.foursquare.domain.Category
import inc.fabudi.foursquare.domain.Photo
import inc.fabudi.foursquare.domain.Place
import inc.fabudi.foursquare.domain.Tip
import kotlinx.serialization.Serializable


@Entity
data class DatabasePlace constructor(
    @PrimaryKey val fsq_id: String,
    var categories: List<DatabaseCategory>,
    var location: String,
    var name: String
)

@Entity
@Serializable
data class DatabaseCategory constructor(
    @PrimaryKey val id: Int, val name: String, val iconUrl: String
)

@Entity
@Serializable
data class DatabaseTip constructor(
    val fsq_id: String, @PrimaryKey val id: String, val created_at: String, val text: String
)

@Entity
@Serializable
data class DatabasePhoto constructor(
    val fsq_id: String, @PrimaryKey val id: String, val imageUrl: String
)


fun List<DatabasePlace>.asDomainModel(): List<Place> {
    return map {
        Place(
            fsq_id = it.fsq_id, categories = it.categories.map { category ->
                Category(
                    category.id, category.name, category.iconUrl
                )
            }, location = it.location, name = it.name
        )
    }
}

fun DatabasePlace.asDomainModel(): Place {
    return Place(
        fsq_id = fsq_id, categories = categories.map { category ->
            Category(
                category.id, category.name, category.iconUrl
            )
        }, location = location, name = name
    )
}

@JvmName("TipAsDomainModel")
fun List<DatabaseTip>.asDomainModel(): List<Tip> {
    return map {
        Tip(
            it.fsq_id, it.id, it.created_at, it.text
        )
    }
}

@JvmName("PhotoAsDomainModel")
fun List<DatabasePhoto>.asDomainModel(): List<Photo> {
    return map {
        Photo(
            it.fsq_id, it.id, it.imageUrl
        )
    }
}