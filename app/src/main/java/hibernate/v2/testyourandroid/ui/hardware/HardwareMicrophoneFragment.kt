package hibernate.v2.testyourandroid.ui.hardware

import android.Manifest
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.view.View
import android.widget.Toast
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.databinding.FragmentHardwareMicrophoneBinding
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import hibernate.v2.testyourandroid.util.viewBinding
import java.io.File
import java.io.IOException

/**
 * Created by himphen on 21/5/16.
 */
class HardwareMicrophoneFragment : BaseFragment(R.layout.fragment_hardware_microphone) {

    private val binding by viewBinding(FragmentHardwareMicrophoneBinding::bind)
    private var mMediaRecorder: MediaRecorder? = null
    private var mMediaPlayer: MediaPlayer? = null
    private var mIsRecording = false
    private var mIsPlaying = false
    private lateinit var mFile: File

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.playBtn.isEnabled = false
        mFile = File(context?.filesDir, "TestYourAndroidMicTest.m4a")
        binding.recordBtn.setOnClickListener {
            if (mIsRecording) {
                stopRecording()
            } else {
                startRecording()
            }
        }
        binding.playBtn.setOnClickListener {
            if (mIsPlaying) {
                stopPlaying()
            } else {
                startPlaying()
            }
        }
        if (!isPermissionsGranted(PERMISSION_NAME)) {
            requestMultiplePermissions.launch(PERMISSION_NAME)
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
                    binding.playBtn.setText(R.string.mic_stop)
                    binding.recordBtn.isEnabled = false
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
                binding.recordBtn.setText(R.string.mic_stop)
                binding.playBtn.isEnabled = false
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
        binding.playBtn.isEnabled = true
        binding.recordBtn.isEnabled = true
        binding.playBtn.setText(R.string.mic_start_playing)
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
        binding.playBtn.isEnabled = true
        binding.recordBtn.isEnabled = true
        binding.recordBtn.setText(R.string.mic_start_recording)
    }

    companion object {
        val PERMISSION_NAME = arrayOf(Manifest.permission.RECORD_AUDIO)
    }
}