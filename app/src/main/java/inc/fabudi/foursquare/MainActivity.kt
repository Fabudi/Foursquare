package inc.fabudi.foursquare

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip


class MainActivity : AppCompatActivity() {
    private lateinit var presenter: Presenter
    private lateinit var loginButton: Button
    private lateinit var clearFilter: Chip
    private lateinit var emptyViewText: TextView
    private lateinit var categoriesRecyclerView: RecyclerView
    private lateinit var emptyView: LinearLayout
    private lateinit var placesRecyclerView: RecyclerView
    private lateinit var adapter: CategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        presenter = Presenter(Model())
        presenter.attachView(this)
    }

    fun findViews() {
        loginButton = findViewById(R.id.login_button)
        emptyViewText = findViewById(R.id.places_list_emptyview_message)
        categoriesRecyclerView = findViewById(R.id.categories_recyclerview)
        emptyView = findViewById(R.id.places_list_emptyview)
        placesRecyclerView = findViewById(R.id.places_recyclerview)
        clearFilter = findViewById(R.id.clearChip)
    }

    fun setupOnClicks() {
        loginButton.setOnClickListener { presenter.loginClick() }
        clearFilter.setOnClickListener { presenter.clearFilters() }
    }

    fun setupRecyclerView() {
        val categoriesLabelsArray = resources.getStringArray(R.array.categories_array)
        val a = ArrayList<Category>(categoriesLabelsArray.size)
        for (category in categoriesLabelsArray) {
            a.add(Category(category))
        }
        adapter = CategoryAdapter(a, object : CategoryAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                presenter.categorySelect(position)
            }
        })
        categoriesRecyclerView.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.top_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                presenter.logout(getString(R.string.preference_file_key))
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    fun getSharedPreferences(): SharedPreferences = this.getSharedPreferences(
        getString(R.string.preference_file_key), Context.MODE_PRIVATE
    )

    fun showToast(message: String?) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    fun gotoLoginActivity() = startActivity(Intent(this, LoginActivity::class.java))

    fun isOnline(): Boolean {
        val connectivityManager =
            this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return capabilities != null
    }

    fun getCategoryId(position: Int): String =
        resources.getStringArray(R.array.categories_array_values)[position]

    fun clearFilters() {
        adapter.unselectAll()
    }


}