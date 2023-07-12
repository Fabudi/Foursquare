package inc.fabudi.foursquare.network

import com.squareup.moshi.JsonClass
import inc.fabudi.foursquare.database.DatabaseCategory
import inc.fabudi.foursquare.database.DatabasePhoto
import inc.fabudi.foursquare.database.DatabasePlace
import inc.fabudi.foursquare.database.DatabaseTip

@JsonClass(generateAdapter = true)
data class NetworkPlaceContainer(val results: List<NetworkPlace>)

@JsonClass(generateAdapter = true)
data class NetworkPlace(
    val fsq_id: String,
    var categories: List<NetworkCategory>,
    var location: NetworkLocation,
    var name: String
)

@JsonClass(generateAdapter = true)
data class NetworkTip(
    val id: String, val created_at: String, val text: String
)

@JsonClass(generateAdapter = true)
data class NetworkPhoto(
    val id: String, val prefix: String, val suffix: String
)

@JsonClass(generateAdapter = true)
data class NetworkCategory(
    val id: Int, val name: String, val icon: NetworkIcon
)

@JsonClass(generateAdapter = true)
data class NetworkIcon(
    val prefix: String, val suffix: String
)

@JsonClass(generateAdapter = true)
data class NetworkLocation(
    val formatted_address: String
)

@JsonClass(generateAdapter = true)
data class OAuthToken(
    val access_token: String
)

fun NetworkPlaceContainer.asDatabaseModel(): List<DatabasePlace> {
    return results.map {
        DatabasePlace(
            it.fsq_id, it.categories.map { category ->
                DatabaseCategory(
                    category.id, category.name, category.icon.prefix + "120" + category.icon.suffix
                )
            }, it.location.formatted_address, it.name
        )
    }
}

fun List<NetworkTip>.asDatabaseModel(fsq_id: String): List<DatabaseTip> {
    return map {
        DatabaseTip(
            fsq_id, it.id, it.created_at, it.text
        )
    }
}

@JvmName("PhotoAsDatabaseModel")
fun List<NetworkPhoto>.asDatabaseModel(fsq_id: String): List<DatabasePhoto> {
    return map {
        DatabasePhoto(
            fsq_id, it.id, it.prefix + "original" + it.suffix
        )
    }
}