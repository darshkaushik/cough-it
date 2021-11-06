package com.ibmhack2021.coughit.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.transition.MaterialElevationScale
import com.ibmhack2021.coughit.R
import com.ibmhack2021.coughit.databinding.FragmentHomeBinding
import com.ibmhack2021.coughit.databinding.FragmentSplashBinding


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class HomeFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        exitTransition = MaterialElevationScale(true)
        enterTransition = MaterialElevationScale(true)


        handleFABOnScroll()



        return binding.root
    }

    fun handleFABOnScroll(){
        binding.run {
            nestedScrollView.setOnScrollChangeListener {
                    v, scrollX, scrollY, oldScrollX, oldScrollY ->
                if (scrollY > oldScrollY)
                    testFab.shrink()
                else if(scrollX == scrollY)
                    testFab.extend()
                else testFab.extend()


            }
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}