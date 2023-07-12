package inc.fabudi.foursquare.util

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("isNetworkError", "places")
fun hideIfNetworkError(view: View, isNetWorkError: Boolean, places: Any?) {
    view.visibility = if (places != null) View.GONE else View.VISIBLE

    if (isNetWorkError) {
        view.visibility = View.GONE
    }
}