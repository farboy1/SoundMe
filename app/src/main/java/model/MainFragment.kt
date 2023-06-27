package model

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.soundme.MainActivity
import com.example.soundme.R
import com.example.soundme.extensions.AcrCloudManager
import viewModel.MainViewModel


class MainFragment : Fragment() {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var acrCloudManager: AcrCloudManager
    private lateinit var trackFragment: TrackFragment

    private lateinit var autoSwitch: Switch
    private lateinit var resultTextView: TextView
    private lateinit var volumeTextView: TextView
    private lateinit var recognizeButton: Button
    private lateinit var resetButton: Button
    private lateinit var settingsButton: Button
    private lateinit var dataList: MutableList<String>

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_main, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        autoSwitch = view.findViewById(R.id.auto_switch)
        resultTextView = view.findViewById(R.id.result)
        volumeTextView = view.findViewById(R.id.volume)
        recognizeButton = view.findViewById(R.id.start)
        resetButton = view.findViewById(R.id.cancel)
        settingsButton = view.findViewById(R.id.settingsButton)

        acrCloudManager = AcrCloudManager(
            requireContext(),
            volumeTextView
        )
        acrCloudManager.setDataListener(this)


        override fun onDataUpdated(dataList: List<String>) {
            if (dataList.isNotEmpty()) {
                val action = MainFragmentDirections.actionMainFragmentToTrackFragment(dataList.toTypedArray())
                navController.navigate(action)
            }
        }


        recognizeButton.setOnClickListener {
            acrCloudManager.openAutoRecognize()
        }

        resetButton.setOnClickListener {
            acrCloudManager.closeAutoRecognize()
        }

        autoSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                acrCloudManager.openAutoRecognize()
            } else {
                acrCloudManager.closeAutoRecognize()
            }
        }

        settingsButton.setOnClickListener {
            openAppSettings()
        }

        verifyPermissions()
    }

    private fun verifyPermissions() {
        for (i in PERMISSIONS.indices) {
            val permission =
                ActivityCompat.checkSelfPermission(requireContext(), PERMISSIONS[i])
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), PERMISSIONS, REQUEST_EXTERNAL_STORAGE)

                break
            }
        }
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", requireActivity().packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        mainViewModel.setCurrentFragment(MainFragment::class.java)

        acrCloudManager.release()
    }

    companion object {
        private const val REQUEST_EXTERNAL_STORAGE = 1
        private val PERMISSIONS = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
        )
    }
}
