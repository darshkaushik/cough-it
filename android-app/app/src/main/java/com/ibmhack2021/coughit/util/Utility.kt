package com.ibmhack2021.coughit.util

import android.util.Log
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class Utility{

    companion object{
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
    }

}

