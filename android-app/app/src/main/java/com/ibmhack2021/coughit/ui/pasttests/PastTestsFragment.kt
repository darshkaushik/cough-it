package com.ibmhack2021.coughit.ui.pasttests

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.transition.MaterialFadeThrough
import com.google.firebase.auth.FirebaseAuth
import com.ibmhack2021.coughit.databinding.FragmentPastTestsBinding
import com.ibmhack2021.coughit.databinding.FragmentRecordBinding
import com.ibmhack2021.coughit.repository.Repository
import com.ibmhack2021.coughit.util.Resource
import java.util.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class PastTestsFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    // view model
    lateinit var pastTestsViewModel: PastTestsViewModel

    private var _binding: FragmentPastTestsBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        // init the repository
        val repository = Repository(requireContext())
        val recordViewModelProviderFactory = PastTestsViewModelProviderFactory(repository)
        pastTestsViewModel = ViewModelProvider(this, recordViewModelProviderFactory)
            .get(PastTestsViewModel::class.java)

        // init the firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPastTestsBinding.inflate(inflater, container, false)

        exitTransition = MaterialFadeThrough()
        enterTransition = MaterialFadeThrough()

        binding.run {

            // make the api call
//            pastTestsViewModel.getPastsTests(firebaseAuth.currentUser?.email!!)
            pastTestsViewModel.getPastsTests("gauravdas014@gmail.com")

            // observer
            pastTestsViewModel.pastTests.observe(viewLifecycleOwner, Observer {
                when(it){
                    is Resource.Success ->{
                        it.data?.let {
                            Log.d("reports" , it.data.get(0).prediction)
                        }
                    }
                }
            })
        }



        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PastTestsFragment().apply {
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