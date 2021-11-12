package com.ibmhack2021.coughit.ui.prediction

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ibmhack2021.coughit.model.prediction.request.PredictionRequest
import com.ibmhack2021.coughit.model.prediction.response.Prediction
import com.ibmhack2021.coughit.repository.Repository
import com.ibmhack2021.coughit.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

class PredictViewModel(val repository: Repository) : ViewModel() {

    // mutable observable data for prediction API
    val predictedValue : MutableLiveData<Resource<Prediction>> = MutableLiveData()
    val requiredValue : MutableLiveData<Double?> = MutableLiveData((0).toDouble())

    // api call for getting the prediction
    fun getPrediction(predictionRequest: PredictionRequest)
            = viewModelScope.launch {
        predictedValue.postValue(Resource.Loading())

        // then make the network call
        try{
            val response = repository.getPrediction(predictionRequest = predictionRequest)
            predictedValue.postValue(handleRestResponse(response))
        }catch (e : SocketTimeoutException){
            Log.d("Prediction" , "timeout")
        }

    }

    // function to filter the response and get the required value
    fun getActualValue(prediction: Prediction) = viewModelScope.launch {
        if(prediction.status.equals("success")){
            val data = prediction.data
            val listPredictions = data.predictions
            val values = listPredictions[0].values
            val predictionValue = values[0][0][0]
            requiredValue.postValue(predictionValue)
        }else{
            requiredValue.postValue(null)
        }
    }



    // handle api response for prediction
    private fun handleRestResponse(response: Response<Prediction>) : Resource<Prediction> {
        if(response.isSuccessful){
            response.body()?.let {
                return Resource.Success(it)
            }
        }

        return Resource.Error(response.message())
    }
}