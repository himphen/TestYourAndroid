package hibernate.v2.testyourandroid.ui.fragment

import android.Manifest
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.helper.UtilHelper.openErrorPermissionDialog
import kotlinx.android.synthetic.main.fragment_hardware_microphone.*
import java.io.File
import java.io.IOException

/**
 * Created by himphen on 21/5/16.
 */
class HardwareMicrophoneFragment : BaseFragment() {
    private var mMediaRecorder: MediaRecorder? = null
    private var mMediaPlayer: MediaPlayer? = null
    private var mIsRecording = false
    private var mIsPlaying = false
    private lateinit var mFile: File
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_hardware_microphone, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playBtn.isEnabled = false
        mFile = File(context?.filesDir, "TestYourAndroidMicTest.m4a")
        recordBtn.setOnClickListener {
            if (mIsRecording) {
                stopRecording()
            } else {
                startRecording()
            }
        }
        playBtn.setOnClickListener {
            if (mIsPlaying) {
                stopPlaying()
            } else {
                startPlaying()
            }
        }
        if (!isPermissionsGranted(PERMISSION_NAME)) {
            requestPermissions(PERMISSION_NAME, PERMISSION_REQUEST_CODE)
        }
    }

    override fun onPause() {
        super.onPause()
        stopRecording()
        stopPlaying()
    }

    private fun startPlaying() {
        try {
            if (mFile.exists()) {
                mMediaPlayer = MediaPlayer()
                mMediaPlayer?.let { mMediaPlayer ->
                    mMediaPlayer.setDataSource(mFile.absolutePath)
                    mMediaPlayer.setOnCompletionListener { stopPlaying() }
                    mMediaPlayer.prepare()
                    mMediaPlayer.start()
                    playBtn.setText(R.string.mic_stop)
                    recordBtn.isEnabled = false
                    mIsPlaying = true
                } ?: run {
                    Toast.makeText(context, "Fail", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: IOException) {
            Toast.makeText(context, "Fail", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startRecording() {
        try {
            mMediaRecorder = MediaRecorder()
            mMediaRecorder?.let { mMediaRecorder ->
                mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
                mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC_ELD)
                mMediaRecorder.setAudioEncodingBitRate(16)
                mMediaRecorder.setAudioSamplingRate(44100)
                mMediaRecorder.setOutputFile(mFile.absolutePath)
                mMediaRecorder.prepare()
                mMediaRecorder.start()
                recordBtn.setText(R.string.mic_stop)
                playBtn.isEnabled = false
                mIsRecording = true
            } ?: run {
                Toast.makeText(context, "Fail", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Fail", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopPlaying() {
        mMediaPlayer?.let { mMediaPlayer ->
            if (mIsPlaying) {
                mIsPlaying = false
                try {
                    mMediaPlayer.stop()
                } catch (ignored: Exception) {
                }
            }
            mMediaPlayer.release()
        }
        playBtn.isEnabled = true
        recordBtn.isEnabled = true
        playBtn.setText(R.string.mic_start_playing)
    }

    private fun stopRecording() {
        mMediaRecorder?.let { mMediaRecorder ->
            if (mIsRecording) {
                mIsRecording = false
                try {
                    mMediaRecorder.stop()
                } catch (ignored: Exception) {
                }
            }
            mMediaRecorder.release()
        }
        playBtn.isEnabled = true
        recordBtn.isEnabled = true
        recordBtn.setText(R.string.mic_start_recording)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (!hasAllPermissionsGranted(grantResults)) {
                openErrorPermissionDialog(context)
            }
        }
    }

    companion object {
        val PERMISSION_NAME = arrayOf(Manifest.permission.RECORD_AUDIO)
    }
}