package com.ibmhack2021.coughit.ui.prediction

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.transition.MaterialFadeThrough
import com.google.firebase.auth.FirebaseAuth
import com.ibmhack2021.coughit.R
import com.ibmhack2021.coughit.databinding.FragmentPredictionBinding
import com.ibmhack2021.coughit.model.prediction.request.PredictionRequest
import com.ibmhack2021.coughit.repository.Repository
import com.ibmhack2021.coughit.util.Resource


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class PredictionFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    lateinit var predictViewModel: PredictViewModel

    // binding
    private var _binding : FragmentPredictionBinding? = null
    private val binding get() = _binding!!

    // arguments
    private val args: PredictionFragmentArgs by navArgs()

    // encoded string
    private var encodedString: String? = null

    // firebase
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        val repository = Repository(requireContext())
        val predictViewModelProviderFactory = PredictViewModelProviderFactory(repository)
        predictViewModel = ViewModelProvider(this, predictViewModelProviderFactory)
            .get(PredictViewModel::class.java)

        // init the firebase
        firebaseAuth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPredictionBinding.inflate(inflater, container, false)

        exitTransition = MaterialFadeThrough()
        enterTransition = MaterialFadeThrough()

        binding.progressBar.show()
        binding.nextButton.isEnabled = false

        // current user won't be null
        val currentUser = firebaseAuth.currentUser
        encodedString = args.encodedString

        // make the api call
        if (currentUser != null) {
            predictViewModel.getPrediction(PredictionRequest(encodedString!!, currentUser.email!!))
        }

        // observer the api call
        predictViewModel.predictedValue.observe(viewLifecycleOwner, Observer {
            when(it){
                is Resource.Success ->{
                    it.data?.let {
                        predictViewModel.getActualValue(prediction = it)
                        binding.progressBar.hide()
                        binding.nextButton.isEnabled = true
                    }
                }
            }

        })

        // then observe the required value parameter so that whenever the value gets updated we
        // get the value
        predictViewModel.requiredValue.observe(viewLifecycleOwner, Observer {
            // here I can set the text view
            Log.d("Prediction" , it.toString())
            binding.predictTextView.text = String.format("%.2f", it)
        })

        // navigate to home
        binding.nextButton.setOnClickListener {
            findNavController().navigate(R.id.action_predictionFragment_to_homeFragment)
        }




        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PredictionFragment().apply {
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