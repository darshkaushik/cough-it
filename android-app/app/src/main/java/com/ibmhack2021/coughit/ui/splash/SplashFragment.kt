package com.ibmhack2021.coughit.ui.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.google.android.material.transition.MaterialElevationScale
import com.google.firebase.auth.FirebaseAuth
import com.ibmhack2021.coughit.R
import com.ibmhack2021.coughit.databinding.FragmentSplashBinding


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class SplashFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        mAuth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        // your code goes here
        exitTransition = MaterialElevationScale(true)

        // here I have to call firebase and then verify if the user has signed in
        // if the user has signed in already then move to home fragment directlly
        // todo : add firebase logic here
        val currentUser = mAuth.currentUser

        if(currentUser != null){
            // move to next screen
            Handler(Looper.getMainLooper()).postDelayed({
                binding.root.findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
            }, 600)
        }else{
            // move to next screen
            Handler(Looper.getMainLooper()).postDelayed({
                binding.root.findNavController().navigate(R.id.action_splashFragment_to_signUpFragment)
            }, 600)
        }







        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SplashFragment().apply {
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