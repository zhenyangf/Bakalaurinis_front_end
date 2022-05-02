package com.feng.insurance.model

import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.feng.insurance.service.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.InputStream

class LoginViewModel : ViewModel() {
//    val isSuccessLoading = mutableStateOf(value = false)
//    val imageErrorAuth = mutableStateOf(value = false)
//    val progressBar = mutableStateOf(value = false)

    private val _insuranceList = mutableStateListOf<Insurance>()
    private val _eventList = mutableStateListOf<Event>()
    val insuranceList: List<Insurance>
        get() = _insuranceList
    val eventList: List<Event>
        get() = _eventList

    fun getInsuranceList() {
        val id = user.value?.id

        if (id === null) {
            throw java.lang.Exception("Idk")
        }

        viewModelScope.launch {
            val apiService = APIService.getInstance()
            try {
                Log.d("Logging", "Trying to get insurances")
                val response = apiService.getInsurancesList(id)
                if (response.isSuccessful) {
                    Log.d("Logging", "Got insurances ${response.body()}")
                    _insuranceList.clear()
                    response.body()?.let { _insuranceList.addAll(it) }
                }
            } catch (e: Exception) {
                Log.d("Logging", "Error getting insurance", e)
//                errorMessage = e.message.toString()
            }
        }
    }

    fun createNewInsurance(insuranceDto: InsuranceDto) {
        viewModelScope.launch {
            val apiService = APIService.getInstance()
            try {
                Log.d("Logging", "Trying to create new insurance")
                val response = apiService.createNewInsurance(insuranceDto)
                if (response.isSuccessful) {
                    Log.d("Logging", "Created new insurance")
                    response.body()?.let { _insuranceList.add(it) }
                }
            } catch (e: Exception) {
                Log.d("Logging", "Error creating new insurance", e)
//                errorMessage = e.message.toString()
            }
        }
    }

    @JvmSuppressWildcards
    fun createNewEvent(description: String,file : MultipartBody.Part, damageType:  List<DamageType>, insuranceId: Int, payoutRange: String, title: String) {
        viewModelScope.launch {
            val apiService = APIService.getInstance()
            try {
                Log.d("Logging", "Trying to create new event $description")
                Log.d("Logging", "Trying to create new the damage is: $damageType")
                val response = apiService.createNewEvent(description,file,damageType,insuranceId,payoutRange,title)
                if (response.isSuccessful) {
                    Log.d("Logging", "Created new event")

                    response.body()?.let{ _eventList.add(it) }

                    val insuranceIndex = insuranceList.withIndex().first {
                        it.value.id == insuranceId
                    }.index

                    response.body()?.let {
                        val events = insuranceList[insuranceIndex].events.toMutableList()
                        events.add(it)
                        val foundInsuranceItem = insuranceList[insuranceIndex].copy(events = events)
                        _insuranceList[insuranceIndex] = foundInsuranceItem
                    }
                } else {
                    Log.d("Logging", "Failed in creating new event ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.d("Logging", "Error creating new event", e)
//                errorMessage = e.message.toString()
            }
        }
    }

    private val _user = mutableStateOf<User?>(null)

    val user: MutableState<User?>
        get() = _user

    fun logout() {
        _user.value = null
    }

    fun register(registerDto: UserRegisterDto) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val authService = APIService.getInstance()
                val response = authService.register(registerDto)

                if (response.isSuccessful) {
                    _user.value = response.body()
                } else {
                    Log.d("Logging", "got response HTTP error")
                }

            } catch (e: Exception) {
                Log.d("Logging", "Error Authentication", e)
//                progressBar.value = false
            }
        }
    }


    fun login(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
//                progressBar.value = true
                val authService = APIService.getInstance()
                val response = authService.login(LoginDto(email = email, password = password))

                if (response.isSuccessful) {
                    _user.value = response.body()
                } else {

                }

//                if (responseService.isSuccessful) {
//                    delay(1500L)
//                    isSuccessLoading.value = true
//                    responseService.body()?.let { tokenDto ->
//                        Log.d("Logging", "Response TokenDto: $tokenDto")
//                    }
//                } else {
//                    responseService.errorBody()?.let { error ->
//                        imageErrorAuth.value = true
//                        delay(1500L)
//                        imageErrorAuth.value = false
//                        error.close()
//                    }
//                }
//
//                loginRequestLiveData.postValue(responseService.isSuccessful)
//                progressBar.value = false
            } catch (e: Exception) {
                Log.d("Logging", "Error Authentication", e)
//                progressBar.value = false
            }
        }
    }
}