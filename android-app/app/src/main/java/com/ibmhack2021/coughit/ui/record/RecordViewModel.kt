package com.ibmhack2021.coughit.ui.record

import android.content.Context
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.button.MaterialButton
import com.ibmhack2021.coughit.repository.Repository
import com.ibmhack2021.coughit.util.RecordingState
import kotlinx.coroutines.launch

class RecordViewModel(val repository: Repository) : ViewModel() {

    val countdownValue : MutableLiveData<String> = MutableLiveData()
    val flag : MutableLiveData<RecordingState> = MutableLiveData(RecordingState.IDLE)

    // filename for recording
    var filename: String? = null
    var encodedString: String? = null


    // countdown timer in view model
    fun startCountdown(
        materialButton: MaterialButton,
        context: Context,
        state: Boolean
    ) = viewModelScope.launch {

        // as soon as start countdown will run
        filename = repository.startRecording(context)
        flag.postValue(RecordingState.START)

        var timer = object : CountDownTimer(7000, 1000){
            override fun onTick(millisUntilFinished: Long) {
                Log.d("Timer", (millisUntilFinished/1000).toString())
                countdownValue.postValue((millisUntilFinished/1000).toString())
            }

            override fun onFinish() {
                repository.stopRecording(context = context)
                countdownValue.postValue("Time Out")

                Handler(Looper.getMainLooper()).postDelayed({
                    materialButton.isEnabled = true
                    countdownValue.postValue("7")
                    flag.postValue(RecordingState.STOP)
                }, 400)

                encodedString = repository.getFileFromFilePath(context = context)

            }
        }.start()

    }

    // encoded string
    fun getAudioString() : String?{
        return encodedString
    }
}