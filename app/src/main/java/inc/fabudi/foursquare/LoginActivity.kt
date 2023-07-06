package inc.fabudi.foursquare

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import okhttp3.HttpUrl
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class LoginActivity : AppCompatActivity() {
    private lateinit var sharedPref: SharedPreferences

    private val CLIENT_ID = "K4ZEOM3GMKGE2NXZIKUZQDSLSYIRFWQQTMNE3MN21DHQRLUP"
    private val CLIENT_SECRET = "KVRZQNX2PTXLCTHCDNLMZMGRLTKEULU5OMUYWUJR51KFCQ3H"
    private val GRANT_TYPE = "authorization_code"
    private val REDIRECT_URI = "auth://fabudi.inc"
    private val CODE = "code"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref = this.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE
        )
        checkAuth()
    }

    private fun checkAuth() {
        val authToken = sharedPref.getString(getString(R.string.preference_file_key), "")
        if (authToken == "") login()
    }

    private fun login() {
        val authorizeUrl = HttpUrl.parse("https://foursquare.com/oauth2/authenticate")?.newBuilder()
            ?.addQueryParameter("client_id", CLIENT_ID)
            ?.addQueryParameter("redirect_uri", REDIRECT_URI)
            ?.addQueryParameter("response_type", CODE)?.build()
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(authorizeUrl?.url().toString())
        startActivity(i)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val data = intent?.data
        val code = data?.getQueryParameter(CODE)
        val retrofit = Retrofit.Builder().baseUrl("https://foursquare.com/")
            .addConverterFactory(GsonConverterFactory.create()).build()
        val server = retrofit.create(OAuthServer::class.java)
        val a = server.requestTokenForm(code, CLIENT_ID, CLIENT_SECRET, GRANT_TYPE, REDIRECT_URI)
        a?.enqueue(object : Callback<OAuthToken> {
            override fun onResponse(call: Call<OAuthToken>, response: Response<OAuthToken>) {
                with(sharedPref.edit()) {
                    putString(
                        getString(R.string.preference_file_key),
                        (response.body() as OAuthToken).access_token
                    )
                    apply()
                }
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            }

            override fun onFailure(call: Call<OAuthToken>, t: Throwable) {
                Log.e("Retrofit", "$t")
            }

        })
    }

}
