package inc.fabudi.foursquare

import android.content.SharedPreferences

class Presenter(val model: Model) {
    var view: MainActivity? = null
    var sharedPreferences: SharedPreferences? = null

    fun attachView(view: MainActivity) {
        this.view = view
        onViewAttached(this.view!!)
    }

    private fun onViewAttached(view: MainActivity) {
        sharedPreferences = view.getSharedPreferences()
        view.findViews()
        view.setupOnClicks()
        view.setupRecyclerView()
    }

    fun detachView() {
        this.view = null
        onViewDetached()
    }

    private fun onViewDetached() {

    }


    fun logout(key: String) {
        view?.showToast("Log out successfully")
        with(sharedPreferences?.edit()) {
            this?.putString(
                key, ""
            )
            this?.apply()
        }
    }

    fun isOnline() = view?.isOnline()


    fun loginClick() {
        view?.gotoLoginActivity()
    }

    fun categorySelect(position: Int) {
        view?.showToast(view?.getCategoryId(position))
    }

    fun clearFilters() {
        view?.clearFilters()
    }
}