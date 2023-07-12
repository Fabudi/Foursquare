package inc.fabudi.foursquare.domain


data class Place(
    val fsq_id: String,
    var categories: List<Category>,
    var location: String,
    var name: String,
    var color: Int? = 0
)

data class Filter(
    val id: Int, val name: String, var isChecked: Boolean = false
)

data class Category(
    val id: Int, val name: String, val iconUrl: String
)

data class Tip(
    val fsq_id: String, val id: String, val created_at: String, val text: String
)

data class Photo(
    val fsq_id: String, val id: String, val imageUrl: String
)
