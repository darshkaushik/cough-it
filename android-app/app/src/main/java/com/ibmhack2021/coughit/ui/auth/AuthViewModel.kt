package com.ibmhack2021.coughit.ui.auth


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ibmhack2021.coughit.model.login.request.LoginRequest
import com.ibmhack2021.coughit.model.login.response.LoginResponse
import com.ibmhack2021.coughit.repository.Repository
import com.ibmhack2021.coughit.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response


class AuthViewModel(val repository: Repository) : ViewModel() {

    // live data variable for login request
    val login : MutableLiveData<Resource<LoginResponse>> = MutableLiveData()

    // api call to make the user
    fun createUser(loginRequest: LoginRequest) = viewModelScope.launch {
        login.postValue(Resource.Loading())

        // then make the network call
        val response = repository.loginToServer(loginRequest)
        login.postValue(handleRestResponse(response))
    }

    // handle api response for prediction
    private fun handleRestResponse(response: Response<LoginResponse>) : Resource<LoginResponse> {
        if(response.isSuccessful){
            response.body()?.let {
                return Resource.Success(it)
            }
        }

        return Resource.Error(response.message())
    }
}