package com.ibmhack2021.coughit.repository

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import android.widget.Toast
import com.ibmhack2021.coughit.api.RetrofitInstance
import com.ibmhack2021.coughit.model.prediction.request.PredictionRequest
import java.io.File
import java.io.IOException


class Repository(context: Context) {


    /**
     * All the below functions are for API calls
     */

    // prediction API
    suspend fun getPrediction(predictionRequest: PredictionRequest) =
        RetrofitInstance.api.getPrediction(predictionRequest = predictionRequest)







    /**
     * All the below functions are for the media recorder
     */

    private var output: String? = null
    private var mediaRecorder: MediaRecorder? = null
    private var state: Boolean = false
    private var recordingStopped: Boolean = false
    private var bufferSize : Int = 0
    private var thread: Thread? = null
    private var filename: String? = null
    lateinit var file: File
    private var directory: String? = null



    fun startRecording(context: Context) : String{

        mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        }else{
            MediaRecorder()
        }

        mediaRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)

        val root = context.getExternalFilesDir(null)
        val file = File((root?.absolutePath  + "/DataSamples"))
        if(!file.exists()) file.mkdirs()
        filename = root?.absolutePath + "/DataSamples" + (System.currentTimeMillis().toString()+ ".m4a" )
        mediaRecorder!!.setOutputFile(filename)
        mediaRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mediaRecorder!!.setAudioSamplingRate(22050)


        try {
            mediaRecorder!!.prepare()
            mediaRecorder!!.start()
            state = true
//            Toast.makeText(context, "Recording started !", Toast.LENGTH_SHORT).show()
        }catch (e : IllegalStateException){
            e.printStackTrace()
        }catch (e: IOException){
            e.printStackTrace()
        }

        return filename as String
    }

    fun stopRecording(context: Context){
        if(state){
            mediaRecorder!!.stop()
            mediaRecorder!!.release()
            state = false
            Toast.makeText(context, "File saved", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(context, "You are not recording right now!", Toast.LENGTH_SHORT).show()
        }

        mediaRecorder = null
    }

    fun deleteRecording(context: Context) : Boolean{
        return File(filename).delete()
    }

    // function to get the max amplitude
    fun getMaxAmplitude(): Int?{
        if(state){
            return mediaRecorder?.maxAmplitude
        }
        return null
    }

    // get the file from the location and convert that into base64 string
    fun getFileFromFilePath(context: Context): String {
        val file = File(filename!!)

        val byteArray = file.readBytes()

        val encoding = android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT)
        Log.d("encoding" , "encoding : $encoding")

        // save the file
//        val filename = "sample.txt"
//        var file2 = File(context.getExternalFilesDir(null)?.absolutePath + filename)
//        file2.writeText(encoding)

        return encoding
    }
}