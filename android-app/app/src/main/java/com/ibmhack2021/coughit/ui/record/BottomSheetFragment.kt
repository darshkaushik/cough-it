package com.ibmhack2021.coughit.ui.record

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ibmhack2021.coughit.R
import com.ibmhack2021.coughit.databinding.FragmentBottomSheetBinding


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class BottomSheetFragment(dialogResponse: DialogResponse) : BottomSheetDialogFragment() {

    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentBottomSheetBinding? = null
    private val binding get() = _binding!!

    private val dialogResponse = dialogResponse

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
        _binding = FragmentBottomSheetBinding.inflate(inflater, container, false)

        binding.run {
            sendButton.setOnClickListener {
                dialogResponse.onButtonClick(true)
            }

            discardButton.setOnClickListener {
                dialogResponse.onButtonClick(false)
            }
        }

        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance(dialogResponse: DialogResponse) =
            BottomSheetFragment(dialogResponse)
    }
}