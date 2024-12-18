package com.dicoding.picodiploma.loginwithanimation.view.signup

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.response.RegisterResponse
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class SignupViewModel(private val repository: UserRepository): ViewModel() {
    private val _registerResponse = MutableStateFlow<RegisterResponse?>(null)
    val registerResponse: StateFlow<RegisterResponse?> = _registerResponse

    private val _errorMassage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMassage

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = repository.register(name, email, password)
                _registerResponse.value = response
                Log.d("SignupViewModel", "Register Success: ${response.message}")
            } catch (e: HttpException) {
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, RegisterResponse::class.java)
                _errorMassage.value = errorBody?.message?: "Terjadi kesalahan, mohon coba lagi"
                Log.e("SignupViewModel", "Register error: ${_errorMassage.value}")
            }
            catch (e:Exception) {
                _errorMassage.value = "Terjadi kesalahan"
                Log.e("SignupViewModel", "Register error: ${e.message}")
            }
        }
    }
}