package com.fahmi.aplikasistoryfahmi

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

//    @GET("detail/{id}")
//    fun getRestaurant(
//        @Path("id") id: String
//    ): Call<RestaurantResponse>

//    @FormUrlEncoded
//    @Headers("Authorization: token 12345")
//    @POST("review")
//    fun postReview(
//        @Field("id") id: String,
//        @Field("name") name: String,
//        @Field("review") review: String
//    ): Call<PostReviewResponse>


    @FormUrlEncoded
    @POST("/v1/register")
    fun registerPengguna(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterResponse>


    @FormUrlEncoded
        @POST("/v1/login")
        fun loginPengguna(
            @Field("email") email: String,
            @Field("password") password: String,
        ): Call<LoginResponse>


    @GET("/v1/stories")
    suspend fun getAllStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("location") id: Int? = null
    ): GetAllStoriesResponse


    @Multipart
    @POST("/v1/stories")
    fun addNewStory(
        @Header("Authorization") authorizationHeader: String,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody?,
        @Part("lon") lon: RequestBody?,
        @Part photo: MultipartBody.Part
    ): Call<ApiResponse<StoryResponse>>

    @GET("/v1/stories/{id}")
    fun getStorybyId(
        @Header("Authorization") Header_Token_authorization: String,
        @Path("id") id: String
    ): Call<StoryResponsez>



}

data class GetAllStoriesResponse(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("listStory")
    val listStory: List<StoryResponse>
)

data class Story(
    val id: String,
    val name: String,
    val description: String,
    val photoUrl: String,
    val createdAt: String,
    val lat: Float,
    val lon: Float
)

data class StoryResponsez(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("story")
    val story: Story?
)


data class StoryResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("photoUrl")
    val photoUrl: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("lat")
    val lat: Float,
    @SerializedName("lon")
    val lon: Float,
)


data class ApiResponse<T>(
    val error: Boolean,
    val message: String,
    val data: T?
)


data class LoginResponse(
    val error: Boolean,
    val message: String,
    val loginResult: LoginResult
)

data class LoginResult(
    val userId: String,
    val name: String,
    val token: String
)



data class RegisterResponse(
    val error: Boolean,
    val message: String
)

data class FileUploadResponse(
    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)
