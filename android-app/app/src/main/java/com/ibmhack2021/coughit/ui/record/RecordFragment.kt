package com.ibmhack2021.coughit.ui.record

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialFadeThrough
import com.ibmhack2021.coughit.R
import com.ibmhack2021.coughit.databinding.FragmentHomeBinding
import com.ibmhack2021.coughit.databinding.FragmentRecordBinding
import com.ibmhack2021.coughit.repository.Repository
import com.ibmhack2021.coughit.util.RecordingState
import java.util.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class RecordFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    // view model
    lateinit var recordViewModel: RecordViewModel

    private var _binding: FragmentRecordBinding? = null
    private val binding get() = _binding!!

    private var encodedString: String? = null
    private var state: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        // init the repository
        val repository = Repository(requireContext())
        val recordViewModelProviderFactory = RecordViewModelProviderFactory(repository)
        recordViewModel = ViewModelProvider(this, recordViewModelProviderFactory)
            .get(RecordViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRecordBinding.inflate(inflater, container, false)

        exitTransition = MaterialFadeThrough()
        enterTransition = MaterialFadeThrough()

        // code goes here
        binding.run {
            // start the timer on button click
            playButton.setOnClickListener {
                state = true
                recordViewModel.startCountdown(playButton, requireContext(), state, audioRecordView)
                recordViewModel.updateVisualiser(audioRecordView)
                playButton.isEnabled = false
            }

            recordViewModel.countdownValue.observe(viewLifecycleOwner, Observer {
                countdownTimer.text = it
            })


            recordViewModel.flag.observe(viewLifecycleOwner, Observer {
                if(RecordingState.STOP == it){
                    encodedString = recordViewModel.getAudioString()
                    val action = RecordFragmentDirections.
                    actionRecordFragmentToPredictionFragment(encodedString = encodedString!!)
                    findNavController().navigate(action)
                }
            })
        }



        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RecordFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}