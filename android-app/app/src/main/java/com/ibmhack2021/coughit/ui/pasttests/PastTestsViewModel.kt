package com.ibmhack2021.coughit.ui.pasttests

import android.widget.ResourceCursorAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ibmhack2021.coughit.model.pasttests.PastTests
import com.ibmhack2021.coughit.model.prediction.response.Prediction
import com.ibmhack2021.coughit.repository.Repository
import com.ibmhack2021.coughit.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class PastTestsViewModel(val repository: Repository) : ViewModel() {

    // mutable live data for past tests
    val pastTests : MutableLiveData<Resource<PastTests>> = MutableLiveData()

    // api call to get the pasts tests
    fun getPastsTests(email: String) = viewModelScope.launch {
        pastTests.postValue(Resource.Loading())

        // make the network call
        val response = repository.getPastTests(email = email)
        pastTests.postValue(handleRestResponse(response))
    }



    // handle api response for prediction
    fun handleRestResponse(response: Response<PastTests>) : Resource<PastTests> {
        if(response.isSuccessful){
            response.body()?.let {
                return Resource.Success(it)
            }
        }

        return Resource.Error(response.message())
    }
}