package inc.fabudi.foursquare.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface PlaceService {
    @Headers("Authorization: fsq3rUpsHQ/cwKLciVtgdPzhmL0TTFtXNt5anlKKwvTCNLA=")
    @GET("v3/places/search")
    suspend fun getPlacesList(
        @Query("ll") ll: String? = "",
        @Query("radius") radius: Int? = 4000,
        @Query("limit") limit: Int? = 30,
        @Query("token") token: String,
        @Query("categories") categories: String? = ""
    ): NetworkPlaceContainer
}

interface TipsService{
    @Headers("Authorization: fsq3rUpsHQ/cwKLciVtgdPzhmL0TTFtXNt5anlKKwvTCNLA=")
    @GET("v3/places/{fsq_id}/tips")
    suspend fun getTipsList(
        @Path("fsq_id") fsq_id: String,
        @Query("token") token: String
    ): List<NetworkTip>
}

interface PhotosService{
    @Headers("Authorization: fsq3rUpsHQ/cwKLciVtgdPzhmL0TTFtXNt5anlKKwvTCNLA=")
    @GET("v3/places/{fsq_id}/photos")
    suspend fun getPhotos(
        @Path("fsq_id") fsq_id: String,
        @Query("token") token: String
    ): List<NetworkPhoto>
}

object PlaceNetwork {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val retrofit = Retrofit.Builder().baseUrl("https://api.foursquare.com/")
        .addConverterFactory(MoshiConverterFactory.create(moshi)).build()

    val places: PlaceService = retrofit.create(PlaceService::class.java)
    val tips: TipsService = retrofit.create(TipsService::class.java)
    val photos: PhotosService = retrofit.create(PhotosService::class.java)
}

object LoginNetwork{
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val retrofit = Retrofit.Builder().baseUrl("https://foursquare.com/")
        .addConverterFactory(MoshiConverterFactory.create(moshi)).build()

    val oAuthService: OAuthService = retrofit.create(OAuthService::class.java)
}

interface OAuthService {
    @FormUrlEncoded
    @POST("oauth2/access_token")
    fun requestTokenForm(
        @Field("code") code: String?,
        @Field("client_id") client_id: String?,
        @Field("client_secret") client_secret: String?,
        @Field("grant_type") grant_type: String?,
        @Field("redirect_uri") redirect_uri: String?
    ): Call<OAuthToken>
}