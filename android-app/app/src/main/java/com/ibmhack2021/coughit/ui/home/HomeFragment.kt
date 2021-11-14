package com.ibmhack2021.coughit.ui.home

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough
import com.google.firebase.auth.FirebaseAuth
import com.ibmhack2021.coughit.R
import com.ibmhack2021.coughit.databinding.FragmentHomeBinding
import com.ibmhack2021.coughit.databinding.FragmentSplashBinding
import com.ibmhack2021.coughit.model.pasttests.Test
import com.ibmhack2021.coughit.repository.Repository
import com.ibmhack2021.coughit.util.Resource
import com.jjoe64.graphview.GridLabelRenderer
import com.jjoe64.graphview.LabelFormatter
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class HomeFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // take permissions for recording audio and read write storage
    private val permissions =
        arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

    // view model
    lateinit var homeViewModel: HomeViewModel

    // firebase auth
    private lateinit var firebaseAuth: FirebaseAuth
//    private var pastTestsList: List<Test>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        // init the repository
        val repository = Repository(requireContext())
        val homeViewModelProviderFactory = HomeViewModelProviderFactory(repository);
        homeViewModel = ViewModelProvider(this, homeViewModelProviderFactory)
            .get(HomeViewModel::class.java)

        // init the firebase
        firebaseAuth = FirebaseAuth.getInstance()

//        Log.d("appstatus", "on Create ")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

//        Log.d("appstatus", "binding successful")

        exitTransition = MaterialFadeThrough()
        enterTransition = MaterialFadeThrough()


        handleFABOnScroll()

        // make the api call
        homeViewModel.getPastsTests(firebaseAuth.currentUser?.email!!)
//        homeViewModel.getPastsTests("jyotimoykashyap123@gmail.com")


        binding.run {
            val gridLabelRenderer = graphView.gridLabelRenderer
            gridLabelRenderer.gridColor = requireContext().getColor(R.color.body_text_color)
            gridLabelRenderer.gridStyle = GridLabelRenderer.GridStyle.HORIZONTAL
            gridLabelRenderer.isVerticalLabelsVisible = true
            gridLabelRenderer.isHighlightZeroLines = false
            gridLabelRenderer.isHorizontalLabelsVisible = false
            gridLabelRenderer.verticalAxisTitle = "Percentage(%)"
            gridLabelRenderer.numHorizontalLabels = 10
            gridLabelRenderer.horizontalLabelsColor = requireContext().getColor(R.color.body_text_color)
            gridLabelRenderer.verticalAxisTitleColor = requireContext().getColor(R.color.body_text_color)


            graphView.viewport.isYAxisBoundsManual = true
            graphView.viewport.setMaxY((100).toDouble())
            graphView.viewport.setMinY((0).toDouble())
            graphView.viewport.isScrollable = true




            // observe the value
            homeViewModel.pastTests.observe(viewLifecycleOwner, Observer {
                when(it){
                    is Resource.Success ->{
                        it.data?.let {
                            // I will get all the data
                            if(it.data == null || it.data.isEmpty()){
                                homeViewModel.convertToLineGraphSeries(null, progressLoad)

                                dateTextView.text = "No data found"
                                timeTextView.text = "You have not taken any test till now."
                                tagChip.visibility = View.INVISIBLE
                                lottieRecent.visibility = View.INVISIBLE
                            }else{
                                tagChip.visibility = View.VISIBLE
                                lottieRecent.visibility = View.VISIBLE
                                homeViewModel.convertToLineGraphSeries(it.data, progressLoad)

                                // here I have to set the text views
                                val latest = it.data[it.data.size - 1]
                                dateTextView.text = homeViewModel.extractDate(latest.date)
                                val resultText = String.format("%.2f" , latest.prediction.toDouble()*100) + "%"
                                resultTextView.text = resultText
                                timeTextView.text = homeViewModel.extractTime(latest.date)
                                tagChip.text = if(latest.prediction.toDouble()*100 < 70) "Safe" else "Unsafe"
                            }
                        }
                    }
                }
            })

            homeViewModel.series.observe(viewLifecycleOwner, Observer {
                if(it != null){
                    noDataText.visibility = View.INVISIBLE
                    it.dataPointsRadius = 10f
                    it.setAnimated(true)
                    it.isDrawDataPoints = true
                    it.title = "Covid Prediction Summary"
                    it.color = requireContext().getColor(R.color.orange_primary)
                    graphView.addSeries(it)
                }else{
                    noDataText.visibility = View.VISIBLE
                }

            })



            //graphView.addSeries(series)


            // test button
            testFab.setOnClickListener {
                if(homeViewModel.handlePermissions(requireContext(), permissions)){
                    binding.root.findNavController().navigate(R.id.action_homeFragment_to_recordFragment)
                }else{
                    homeViewModel.requestPermissions(PERMISSION_REQUEST_CODE, permissions, this@HomeFragment)
                }

            }

            // past tests fragment
            pastTests.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_pastTestsFragment)
            }

            Glide.with(this@HomeFragment)
                .load(firebaseAuth.currentUser!!.photoUrl)
                .into(accountIcon)

            // account icon set up
            accountIcon.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
            }

            moreInfoButton.setOnClickListener {
                val uri = Uri.parse("https://www.who.int/emergencies/diseases/novel-coronavirus-2019/advice-for-public")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
        }



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

        const val PERMISSION_REQUEST_CODE = 1

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if(EasyPermissions.somePermissionDenied(this, perms.first())){
            SettingsDialog.Builder(requireActivity()).build().show()
        }else{
            // homeViewModel.requestPermissions(PERMISSION_REQUEST_CODE, permissions, this)
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        Toast.makeText(
            requireContext(),
            "Permissions Granted",
            Toast.LENGTH_SHORT
        ).show()
    }




}