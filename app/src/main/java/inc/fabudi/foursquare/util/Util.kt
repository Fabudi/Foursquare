package inc.fabudi.foursquare.util

import android.content.Context
import android.graphics.Color
import inc.fabudi.foursquare.R

fun getColorForCategory(categoryId: Int, context: Context): Int {
    val colors = context.resources.getStringArray(R.array.colors)
    var color = Color.parseColor(colors[0])
    for ((index, cat) in context.resources.getStringArray(R.array.categories_array_values)
        .withIndex()) when (categoryId) {
        in cat.toInt()..cat.toInt() + 999 -> color = Color.parseColor(
            colors[index]
        )
    }
    return color
}