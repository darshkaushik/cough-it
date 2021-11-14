package com.ibmhack2021.coughit.ui.home

import android.Manifest
import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.gson.internal.bind.util.ISO8601Utils
import com.ibmhack2021.coughit.model.pasttests.PastTests
import com.ibmhack2021.coughit.model.pasttests.Test
import com.ibmhack2021.coughit.repository.Repository
import com.ibmhack2021.coughit.util.Resource
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.vmadalin.easypermissions.BuildConfig
import com.vmadalin.easypermissions.EasyPermissions
import kotlinx.coroutines.launch
import retrofit2.Response
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class HomeViewModel(val repository: Repository) : ViewModel() {


    // I have to provide the fragment with this value or value type
    val series: MutableLiveData<LineGraphSeries<DataPoint>> =
        MutableLiveData()

    // mutable live data for past tests
    val pastTests : MutableLiveData<Resource<PastTests>> = MutableLiveData()

    // api call to get the pasts tests
    fun getPastsTests(email: String) = viewModelScope.launch {
        pastTests.postValue(Resource.Loading())

        // make the network call
        val response = repository.getPastTests(email = email)
        pastTests.postValue(handleRestResponse(response))

//        Log.d("homefragment" , com.ibmhack2021.coughit.BuildConfig.BASE_URL)
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

    fun convertToLineGraphSeries(array: List<Test>, linearProgressIndicator: LinearProgressIndicator) = viewModelScope.launch {
        // val array = arrayOf<Double>(elements)
        val series2 = LineGraphSeries(arrayOf<DataPoint>())
        for(i in array.indices){
            series2.appendData(DataPoint((i).toDouble(), (array[i].prediction).toDouble().times(100)), true, 20)
            Log.d("homefragment", array[i].prediction + " Date stamp : " + array[i].date)
        }
        linearProgressIndicator.hide()
        series.postValue(series2)
    }

    fun extractDate(date : String) : String{
        // 2021-11-12T14:10:24.159Z --> format of the string
        val year = date.substring(0,4).toInt()
        val month = date.substring(5,7).toInt()
        val day = date.substring(8,10).toInt()

        Log.d("homefragment" ,"Extracted time : " + year + " " +  month + " " + day)

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, day)
        calendar.add(Calendar.MONTH, month)
        calendar.add(Calendar.YEAR, year)

        val date = Date(year-1900, month-1, day)
        return DateFormat.getDateInstance().format(date)


    }

    fun extractTime(time : String) : String{
        // 2021-11-12T14:10:24.159Z --> format of the string
        val year = time.substring(0,4).toInt()
        val month = time.substring(5,7).toInt()
        val day = time.substring(8,10).toInt()
        val hours = time.substring(11, 13).toInt()
        val min = time.substring(14, 16).toInt()
        val seconds = time.substring(17,19).toInt()

        val time = DateFormat.getTimeInstance()
        val date = Date(year, month, day, hours, min, seconds)

        val simpleDateFormat = SimpleDateFormat("EEEE")
        val dayOfWeek = simpleDateFormat.format(date)

        val string = DateFormat.getTimeInstance(DateFormat.MEDIUM).format(date) + ", " + dayOfWeek

        return string
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