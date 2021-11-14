package com.ibmhack2021.coughit.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.MaterialFadeThrough
import com.google.firebase.auth.FirebaseAuth
import com.ibmhack2021.coughit.R
import com.ibmhack2021.coughit.databinding.FragmentSettingsBinding


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class SettingsFragment : BottomSheetDialogFragment() {

    private var param1: String? = null
    private var param2: String? = null

    private var _binding : FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        firebaseAuth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        exitTransition = MaterialFadeThrough()
        enterTransition = MaterialFadeThrough()

        val currentUser = firebaseAuth.currentUser

        // load the account icon
        binding.run {

            // load the profile icon
            Glide.with(requireActivity())
                .load(currentUser?.photoUrl)
                .into(profileImage)

            // fill the name and email
            userName.text = currentUser?.displayName
            emailId.text = currentUser?.email

            // sign out
            logout.setOnClickListener {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Sign Out")
                    .setMessage("Are you sure you want to sign out ?")
                    .setPositiveButton("Sign Out"){
                            dialog, which ->
                        firebaseAuth.signOut()
                        findNavController().navigate(R.id.action_settingsFragment_to_splashFragment)
                        dismiss()
                    }
                    .show()
            }

            // about us
            aboutUs.setOnClickListener {
                findNavController().navigate(R.id.action_settingsFragment_to_aboutUsFragment)
            }

            // contribute us
            contributeUs.setOnClickListener {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Contribute Us")
                    .setMessage(resources.getString(R.string.contribute_us))
                    .setPositiveButton("OK"){
                        dialog, which ->
                        dismiss()
                    }
                    .show()
            }
        }


        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingsFragment().apply {
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