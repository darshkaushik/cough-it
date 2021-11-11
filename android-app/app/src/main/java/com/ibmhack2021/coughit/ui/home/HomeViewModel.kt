package com.ibmhack2021.coughit.ui.home

import android.Manifest
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ibmhack2021.coughit.repository.Repository
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.vmadalin.easypermissions.EasyPermissions

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

    // I will get just a simple array in retrofit call
    // I have to create a fun to convert this data points to something like this

    fun convertToLineGraphSeries(array: Array<Double>) : LineGraphSeries<DataPoint>{
        // val array = arrayOf<Double>(elements)
        val series = LineGraphSeries(arrayOf<DataPoint>())
        for(i in array.indices){
            series.appendData(DataPoint((i).toDouble(), (array[i]).toDouble()), true, 20)
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