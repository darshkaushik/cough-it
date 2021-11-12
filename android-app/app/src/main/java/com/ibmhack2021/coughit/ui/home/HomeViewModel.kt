package com.ibmhack2021.coughit.ui.home

import android.Manifest
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ibmhack2021.coughit.model.pasttests.PastTests
import com.ibmhack2021.coughit.model.pasttests.Test
import com.ibmhack2021.coughit.repository.Repository
import com.ibmhack2021.coughit.util.Resource
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.vmadalin.easypermissions.EasyPermissions
import kotlinx.coroutines.launch
import retrofit2.Response

class HomeViewModel(val repository: Repository) : ViewModel() {


    // I have to provide the fragment with this value or value type
    val series: MutableLiveData<LineGraphSeries<DataPoint>> =
        MutableLiveData(
        // putting a random value as default
        LineGraphSeries(
            arrayOf<DataPoint>(
                DataPoint((0).toDouble(),(1).toDouble()),
                DataPoint((1).toDouble(),(5).toDouble()),
                DataPoint((2).toDouble(),(3).toDouble()),
                DataPoint((3).toDouble(),(2).toDouble()),
                DataPoint((4).toDouble(),(6).toDouble())
            )
        )
    )

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

    // I will get just a simple array in retrofit call
    // I have to create a fun to convert this data points to something like this

    fun convertToLineGraphSeries(array: List<Test>) : LineGraphSeries<DataPoint>{
        // val array = arrayOf<Double>(elements)
        val series = LineGraphSeries(arrayOf<DataPoint>())
        for(i in array.indices){
            series.appendData(DataPoint((i).toDouble(), (array[i].prediction).toDouble()), true, 20)
        }
        return series
    }


    fun handlePermissions(context: Context, perms: Array<out String>): Boolean {
        return EasyPermissions.hasPermissions(
            context,
            *perms
        )
    }

    fun requestPermissions(PERMISSION_CODE: Int, perms: Array<out String>, fragment: Fragment) =
        EasyPermissions.requestPermissions(
            fragment,
            "These permissions are necessary for total functionality of the app",
            PERMISSION_CODE,
            *perms
        )
}