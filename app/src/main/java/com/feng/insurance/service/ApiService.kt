package com.feng.insurance.service

import android.graphics.Bitmap
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.io.File

data class User(
    var id: Int,
    var email: String,
    var password: String,
    var name: String,
    var surname: String,
    var phone: String,
)

data class UserRegisterDto(
    var name: String,
    var surname: String,
    var email: String,
    var password: String,
    var phone: String,
)

data class LoginDto(
    var email: String,
    var password: String,
)

enum class EventStatus {
    PENDING_REVIEW,
    PENDING_PAYOUT,
    SETTLED,
    REJECTED,
}

enum class DamageType {
    BROKEN_WINDOWS,
    SMALL_HOLE_CEILING, //50-150
    MEDIUM_LARGE_HOLE_CEILING, //200-400
    REPLACE_CEILING, //
    MINOR_WATER_DAMAGE_CEILING, //200-500
    STRUCTURAL_DAMAGE_CEILING, //2000-8000
    WATER_DAMAGE_HARDWOOD_FLOOR, //8-100 SQFT
    REPLACE_HARDWOOD_FLOOR, //6-23 sqft
    REPLACE_LAMINATE_FLOOR, //3-8
    BURGLARY,
}

data class Event (
    var id: Int,
    var description: String,
    var insuranceId: Int,
    var payoutRange: String,
    var title: String,
    var status: EventStatus,
//    var file: MultipartBody,
    var damageType: List<DamageType>,
)

data class CreateEventDto (
    var description: String,
    var insuranceId: Int,
    var payoutRange: String,
    var title: String,
    var damageType: List<DamageType>,
)

enum class InsuranceStatus {
    PENDING,
    APPROVED,
    REJECTED,
}

data class Insurance(
    var address: String,
    var category: String,
    var id: Int,
    var status: InsuranceStatus,
    var title: String,
    var userId: Int,
    var events: MutableList<Event>
)

data class InsuranceDto(
    var address: String,
    var category: String,
    var status: InsuranceStatus,
    var title: String,
    var userId: Int,
)

const val BASE_URL = "http://bakalaurinisapi.us-east-1.elasticbeanstalk.com/"

interface APIService {
    @GET("/api/insurance/{id}/event")
    suspend fun getEventsList(@Path("id") id: Int): Response<List<Event>>

    @GET("/api/user/{id}/insurance")
    suspend fun getInsurancesList(@Path("id") id: Int): Response<List<Insurance>>

    @POST("/api/user/insurance/add")
    suspend fun createNewInsurance(@Body insuranceDto: InsuranceDto) : Response<Insurance>

    @JvmSuppressWildcards
    @Multipart
    @POST("/api/insurance/event/add")
    suspend fun createNewEvent(@Query("description") description: String,
                               @Part file: MultipartBody.Part,
                               @Query("damageType")  damageType:  List <DamageType>,
                               @Query("insuranceId") insuranceId: Int,
                               @Query("payoutRange")  payoutRange: String,
                               @Query("title") title: String) : Response<Event>

    @POST("register")
    suspend fun register(@Body registerDto: UserRegisterDto ) : Response<User>

    @POST("login")
    suspend fun login(@Body loginDto: LoginDto) : Response<User>

    companion object {
        var apiService: APIService? = null
        fun getInstance(): APIService {
            if (apiService == null) {
                apiService = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(APIService::class.java)
            }
            return apiService!!
        }
    }
}