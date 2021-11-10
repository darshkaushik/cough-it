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
import com.visualizer.amplitude.AudioRecordView
import kotlinx.coroutines.launch
import java.util.*

class RecordViewModel(val repository: Repository) : ViewModel() {

    val countdownValue : MutableLiveData<String> = MutableLiveData()
    val flag : MutableLiveData<RecordingState> = MutableLiveData(RecordingState.IDLE)
    val currentMaxAmplitude: MutableLiveData<Int> = MutableLiveData()

    // filename for recording
    var filename: String? = null
    var encodedString: String? = null

    // timer
    private var timer: Timer? = null


    // countdown timer in view model
    fun startCountdown(
        materialButton: MaterialButton,
        context: Context,
        state: Boolean,
        audioRecordView: AudioRecordView
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
                timer?.cancel()
                audioRecordView.recreate()
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

    // function to run the visualiser
    fun updateVisualiser(audioRecordView: AudioRecordView)
        = viewModelScope.launch {
        timer = Timer()
        timer?.schedule(object : TimerTask(){
            override fun run() {
                val currentMaxAmplitude = repository.getMaxAmplitude()
                audioRecordView.update(currentMaxAmplitude?:0)
            }

        },0,100)
    }




    // encoded string
    fun getAudioString() : String?{
        return encodedString
    }
}