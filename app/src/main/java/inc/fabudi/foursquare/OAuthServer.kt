package inc.fabudi.foursquare

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST


interface OAuthServer {
    @FormUrlEncoded
    @POST("oauth2/access_token")
    fun requestTokenForm(
        @Field("code") code: String?,
        @Field("client_id") client_id: String?,
        @Field("client_secret") client_secret: String?,
        @Field("grant_type") grant_type: String?,
        @Field("redirect_uri") redirect_uri: String?
    ): Call<OAuthToken>?
}