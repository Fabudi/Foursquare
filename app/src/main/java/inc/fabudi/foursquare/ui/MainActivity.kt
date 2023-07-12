package inc.fabudi.foursquare.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import inc.fabudi.foursquare.R
import inc.fabudi.foursquare.database.getDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var sharedPreferences: SharedPreferences
    lateinit var token: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = applicationContext.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE
        )
        token = sharedPreferences.getString(R.string.preference_file_key.toString(), "").toString()
        setContentView(R.layout.activity_main)
        val navController = findNavController(R.id.my_nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.my_nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.top_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                with(
                    sharedPreferences.edit()
                ) {
                    putString(
                        getString(R.string.preference_file_key), ""
                    )
                    apply()
                    Toast.makeText(
                        applicationContext, "Successfully Logged Out", Toast.LENGTH_SHORT
                    ).show()
                    GlobalScope.launch(Dispatchers.IO){
                        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                        getDatabase(application).clearAllTables()
                        finish()
                    }
                }
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}