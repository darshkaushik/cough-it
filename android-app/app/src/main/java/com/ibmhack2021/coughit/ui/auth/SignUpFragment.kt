package com.ibmhack2021.coughit.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ibmhack2021.coughit.R
import com.ibmhack2021.coughit.databinding.FragmentSignUpBinding
import com.ibmhack2021.coughit.model.login.request.LoginRequest
import com.ibmhack2021.coughit.repository.Repository
import com.ibmhack2021.coughit.util.Resource
import kotlin.math.sign


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class SignUpFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    // view mode
    lateinit var authViewModel: AuthViewModel



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        // init the firebase auth
        auth = Firebase.auth

        // init the repository & view model
        val repository = Repository(requireContext())
        val authViewModelProviderFactory = AuthViewModelProviderFactory(repository)
        authViewModel = ViewModelProvider(this, authViewModelProviderFactory)
            .get(AuthViewModel::class.java)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        // transitions
        enterTransition = MaterialFadeThrough()
        exitTransition = MaterialFadeThrough()

        // code to sign up
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        // sign in
        binding.run {
            // hide the progress bar
            progressBar.hide()


            signUpButton.setOnClickListener {
                progressBar.show()
                signIn()
            }
        }



        return binding.root
    }

    companion object {

        const val RC_SIGN_IN = 10

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SignUpFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    // function to sign in
    fun signIn(){
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val exception = task.exception
            if(task.isSuccessful){
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d("SignIn", "firebaseAuthWithGoogle:" + account.id)
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    Log.w("SignIn", "Google sign in failed", e)
                }
            }else{
                Log.w("SignIn", exception.toString())
                binding.progressBar.hide()
            }

        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("SignIn", "signInWithCredential:success")
                    val user = auth.currentUser

                    Log.d("currentuser" , "Name: " + user?.displayName
                            + "\n Email ID: " + user?.email
                            +  "\n Phone No. " + user?.phoneNumber
                            + "\n Photo URL: " + user?.photoUrl)

//                    findNavController().navigate(R.id.action_signUpFragment_to_homeFragment)
                    // here I have to call the api and make the request
                    authViewModel.createUser(
                        LoginRequest(
                            user?.email!!,
                            user.photoUrl.toString(),
                            user.uid
                        )
                    )

                    // observe the valu e
                    authViewModel.login.observe(viewLifecycleOwner, Observer {
                        when(it){
                            is Resource.Success ->{
                                it.data?.let {
                                    if(it.status.equals("success")){

                                        findNavController().navigate(R.id.action_signUpFragment_to_homeFragment)
                                    }
                                }
                            }
                            is Resource.Error ->{
                                auth.signOut()
                                Toast.makeText(requireContext(), "Try Again", Toast.LENGTH_SHORT).show()
                            }
                        }
                    })


                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("SignIn", "signInWithCredential:failure", task.exception)
                }
                binding.progressBar.hide()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}