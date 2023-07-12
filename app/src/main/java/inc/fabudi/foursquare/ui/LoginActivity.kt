package inc.fabudi.foursquare.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import inc.fabudi.foursquare.R
import inc.fabudi.foursquare.network.LoginNetwork
import inc.fabudi.foursquare.network.OAuthToken
import okhttp3.HttpUrl
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {
    private lateinit var sharedPref: SharedPreferences

    private val clientId = "K4ZEOM3GMKGE2NXZIKUZQDSLSYIRFWQQTMNE3MN21DHQRLUP"
    private val clientSecret = "KVRZQNX2PTXLCTHCDNLMZMGRLTKEULU5OMUYWUJR51KFCQ3H"
    private val grantType = "authorization_code"
    private val redirectUri = "auth://fabudi.inc"
    private val code = "code"
    private var inProcess = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        sharedPref = applicationContext.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE
        )
        findViewById<Button>(R.id.login).setOnClickListener { if (!inProcess) login() }
    }

    private fun login() {
        inProcess = true
        val authorizeUrl = HttpUrl.parse("https://foursquare.com/oauth2/authenticate")?.newBuilder()
            ?.addQueryParameter("client_id", clientId)
            ?.addQueryParameter("redirect_uri", redirectUri)
            ?.addQueryParameter("response_type", code)?.build()
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(authorizeUrl?.url().toString())
        startActivity(i)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val data = intent?.data
        val code = data?.getQueryParameter(code)
        val retrofit = LoginNetwork.oAuthService
        val a = retrofit.requestTokenForm(code, clientId, clientSecret, grantType, redirectUri)
        a.enqueue(object : Callback<OAuthToken> {
            override fun onResponse(call: Call<OAuthToken>, response: Response<OAuthToken>) {
                with(sharedPref.edit()) {
                    putString(
                        getString(R.string.preference_file_key),
                        (response.body() as OAuthToken).access_token
                    )
                    apply()
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    inProcess = false
                    finish()
                }

            }

            override fun onFailure(call: Call<OAuthToken>, t: Throwable) {
                Log.e("Retrofit", "$t")
                Toast.makeText(applicationContext, "Network error", Toast.LENGTH_SHORT).show()
                inProcess = false
            }

        })
    }

}
