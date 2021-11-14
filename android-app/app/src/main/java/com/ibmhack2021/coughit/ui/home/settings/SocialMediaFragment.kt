package com.ibmhack2021.coughit.ui.home.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ibmhack2021.coughit.R
import com.ibmhack2021.coughit.databinding.FragmentSocialMediaBinding


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class SocialMediaFragment : BottomSheetDialogFragment() {

    private var param1: String? = null
    private var param2: String? = null

    private var _binding : FragmentSocialMediaBinding? = null
    private val binding get() = _binding!!

    private val args : SocialMediaFragmentArgs by navArgs()

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
        _binding = FragmentSocialMediaBinding.inflate(inflater, container, false)


        binding.run {

            // github button
            githubButton.setOnClickListener {
                openUrl(args.github)
            }

            // linkedin button
            linkedinButton.setOnClickListener {
                openUrl(args.linkedin)
            }

            // instagram button
            instagramButton.setOnClickListener {
                openEmail(args.email)
            }

        }


        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SocialMediaFragment().apply {
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

    fun openUrl(url : String){
        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

    fun openEmail(email : String){
        val intent = Intent(Intent.ACTION_SEND)
        intent.setType("plain/text")
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf( email ))
        startActivity(Intent.createChooser(intent, "Send Mail"))
    }

}