package com.ibmhack2021.coughit.ui.home.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.ibmhack2021.coughit.R
import com.ibmhack2021.coughit.databinding.FragmentAboutUsBinding


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class AboutUsFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    private var _binding : FragmentAboutUsBinding? = null
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
        _binding = FragmentAboutUsBinding.inflate(inflater, container, false)

        // open the cards
        binding.run {
            darshCard.setOnClickListener {
                val action = AboutUsFragmentDirections
                    .actionAboutUsFragmentToSocialMediaFragment(darsh_github, darsh_linkedin, darsh_email)
                findNavController().navigate(action)
            }
            gauravCard.setOnClickListener{
                val action = AboutUsFragmentDirections
                    .actionAboutUsFragmentToSocialMediaFragment(gaurav_github, gaurav_linkedin, gaurav_email)
                findNavController().navigate(action)
            }
            jyotimoyCard.setOnClickListener {
                val action = AboutUsFragmentDirections
                    .actionAboutUsFragmentToSocialMediaFragment(jyotimoy_github, jyotimoy_linkedin, jyotimoy_email)
                findNavController().navigate(action)
            }
        }


        return binding.root
    }

    companion object {

        // constants for this
        // darsh
        const val darsh_github = "https://github.com/darshkaushik"
        const val darsh_email = "darsh.kaushik@gmail.com"
        const val darsh_linkedin = "https://www.linkedin.com/in/darshkaushik/"


        // gaurav
        const val gaurav_github = "https://github.com/gauravdas014"
        const val gaurav_email = "gauravdas014@gmail.com"
        const val gaurav_linkedin = "https://www.linkedin.com/in/gauravdas014/"


        // jyotimoy
        const val jyotimoy_github = "https://github.com/JyotimoyKashyap"
        const val jyotimoy_email = "jyotimoykashyap123@gmail.com"
        const val jyotimoy_linkedin = "https://www.linkedin.com/in/jyotimoykashyap/"

        fun newInstance(param1: String, param2: String) =
            AboutUsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}