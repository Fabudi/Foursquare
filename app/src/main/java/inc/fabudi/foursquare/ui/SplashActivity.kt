package inc.fabudi.foursquare.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import inc.fabudi.foursquare.R


class SplashActivity : AppCompatActivity() {
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        sharedPref = applicationContext.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE
        )
        if (isAuthenticated()) startActivity(Intent(this, MainActivity::class.java))
        else startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun isAuthenticated(): Boolean =
        sharedPref.getString(getString(R.string.preference_file_key), "") != ""

}
