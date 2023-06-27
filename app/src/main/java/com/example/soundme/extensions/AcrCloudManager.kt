package com.example.soundme.extensions

import android.content.ContentValues.TAG
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.content.Context
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.acrcloud.rec.ACRCloudClient
import com.acrcloud.rec.ACRCloudConfig
import com.acrcloud.rec.ACRCloudResult
import com.acrcloud.rec.IACRCloudListener
import com.acrcloud.rec.utils.ACRCloudLogger
import com.example.soundme.DataListener
import com.example.soundme.R
import model.TrackFragment
import org.json.JSONException
import org.json.JSONObject

class AcrCloudManager(
    private val context: Context,
    private val volumeTextView: TextView
) : IACRCloudListener {

    private var mClient: ACRCloudClient? = null
    private var mProcessing = false
    private var startTime: Long = 0
    private var mConfig: ACRCloudConfig? = null
    private lateinit var dataList: MutableList<String>
    var trackFragment: TrackFragment? = null
    private val resultTextView: TextView? = null
    private var dataListener: DataListener? = null



    init {
        mConfig = ACRCloudConfig()
        mConfig!!.acrcloudListener = this
        mConfig!!.context = context

        mConfig!!.host = "identify-eu-west-1.acrcloud.com"
        mConfig!!.accessKey = "023a07d0004951ef779398c54bb4c99f"
        mConfig!!.accessSecret = "R7yvUuDhP7voiSBaFd4T2x4Z4apTUhIgoxx0E1h2"

        mConfig!!.recorderConfig.rate = 8000
        mConfig!!.recorderConfig.channels = 1
        mConfig!!.recorderConfig.isVolumeCallback = true
        dataList = mutableListOf()


        mClient = ACRCloudClient()
        ACRCloudLogger.setLog(true)
        val initState = mClient!!.initWithConfig(mConfig)
        if (!initState) {
            Toast.makeText(context, "init error", Toast.LENGTH_SHORT).show()
        }


    }

    fun setDataListener(listener: DataListener) {
        dataListener = listener
    }


    fun openAutoRecognize() {
        val str = context.getString(R.string.suss)
        if (!mProcessing) {
            mProcessing = true
            resultTextView!!.text = ""
            if (mClient == null || !mClient!!.startRecognize()) {
                mProcessing = false
                resultTextView.text = "start error!"
            }
            startTime = System.currentTimeMillis()
        }
        /*Toast.makeText(context, str, Toast.LENGTH_SHORT).show()*/
    }

    fun closeAutoRecognize() {
        val str = context.getString(R.string.suss)
        if (mProcessing && mClient != null) {
            mClient!!.cancel()
            reset()
            Toast.makeText(context, str, Toast.LENGTH_SHORT).show()
        }
    }


    private fun reset() {
        resultTextView!!.text = ""
        mProcessing = false
    }

    override fun onResult(results: ACRCloudResult) {
        reset()

        val result = results.result

        try {
            val j = JSONObject(result)
            val j1 = j.getJSONObject("status")
            val j2 = j1.getInt("code")
            if (j2 == 0) {
                val metadata = j.getJSONObject("metadata")
                if (metadata.has("music")) {
                    val musics = metadata.getJSONArray("music")
                    for (i in 0 until musics.length()) {
                        val tt = musics[i] as JSONObject
                        val title = tt.getString("title")
                        val artistt = tt.getJSONArray("artists")
                        val art = artistt[0] as JSONObject
                        val artist = art.getString("name")
                        val album = tt.getJSONObject("album").getString("name")
                        /*val formattedString = """
                    |$title
                    |$artist
                    |Album: $album
                    """.trimMargin()*/
                        dataList.add(title)
                        Log.d(TAG, "Title log = $title")
                        dataList.add(artist)
                        Log.d(TAG, "Artist log = $artist")
                        dataList.add(album)
                        Log.d(TAG, "Album log = $album")

                    }
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        trackFragment?.setDataList(dataList)

//        resultTextView.text = dataList.joinToString("\n")
        startTime = System.currentTimeMillis()

        // Разрешение выделения текста
        resultTextView!!.setTextIsSelectable(true)
        resultTextView.movementMethod = LinkMovementMethod.getInstance()
        Linkify.addLinks(resultTextView, Linkify.ALL)
    }


    override fun onVolumeChanged(volume: Double) {
        val time = (System.currentTimeMillis() - startTime) / 1000
        volumeTextView.text = """${context.resources.getString(R.string.volume)}$volume

Time: $time s"""
    }

    fun release() {
        mClient?.release()
        mClient = null
    }

}
