package inc.fabudi.foursquare.database

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    @TypeConverter
    fun fromList(value : List<DatabaseCategory>) = Json.encodeToString(value)

    @TypeConverter
    fun toList(value: String) = Json.decodeFromString<List<DatabaseCategory>>(value)
}

class TipConverter{
    @TypeConverter
    fun fromList(value : List<DatabaseTip>) = Json.encodeToString(value)

    @TypeConverter
    fun toList(value: String) = Json.decodeFromString<List<DatabaseTip>>(value)
}