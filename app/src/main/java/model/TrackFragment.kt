package model

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.soundme.R
import com.example.soundme.extensions.AcrCloudManager
import viewModel.MainViewModel

class TrackFragment : Fragment() {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var acrCloudManager: AcrCloudManager
    private lateinit var titleTextView: TextView
    private lateinit var artistTextView: TextView
    private lateinit var albumTextView: TextView

    private var trackTitle: String? = null
    private var trackArtist: String? = null
    private var trackAlbum: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_track, container, false)
    }

    fun setDataList(dataList: List<String>) {
        if (dataList.size >= 3) {
            trackTitle = dataList[0]
            trackArtist = dataList[1]
            trackAlbum = dataList[2]
            displayTrackInfo()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        titleTextView = view.findViewById(R.id.track_title)
        artistTextView = view.findViewById(R.id.track_artist)
        albumTextView = view.findViewById(R.id.track_album)

        val volumeTextView: TextView? = null
        acrCloudManager = AcrCloudManager(
            requireContext(),
            volumeTextView!!
        )
        acrCloudManager.trackFragment = this

    }
    @SuppressLint("StringFormatInvalid")
    private fun displayTrackInfo() {
        titleTextView.text = trackTitle
        artistTextView.text = trackArtist
        albumTextView.text = getString(R.string.album, trackAlbum)
    }

    companion object {
        private const val ARG_TRACK_TITLE = "track_title"
        private const val ARG_TRACK_ARTIST = "track_artist"
        private const val ARG_TRACK_ALBUM = "track_album"

        fun newInstance(title: String?, artist: String?, album: String?): TrackFragment {
            val fragment = TrackFragment()
            val args = Bundle()
            args.putString(ARG_TRACK_TITLE, title)
            args.putString(ARG_TRACK_ARTIST, artist)
            args.putString(ARG_TRACK_ALBUM, album)
            fragment.arguments = args
            return fragment
        }
    }
}
