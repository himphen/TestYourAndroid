package hibernate.v2.testyourandroid.ui.hardware

import android.Manifest
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.databinding.FragmentHardwareMicrophoneBinding
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import hibernate.v2.testyourandroid.util.ext.isPermissionsGranted
import java.io.File
import java.io.IOException

/**
 * Created by himphen on 21/5/16.
 */
class HardwareMicrophoneFragment : BaseFragment<FragmentHardwareMicrophoneBinding>() {

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentHardwareMicrophoneBinding.inflate(inflater, container, false)

    private var mMediaRecorder: MediaRecorder? = null
    private var mMediaPlayer: MediaPlayer? = null
    private var mIsRecording = false
    private var mIsPlaying = false
    private lateinit var mFile: File

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding!!.playBtn.isEnabled = false
        mFile = File(context?.filesDir, "TestYourAndroidMicTest.m4a")
        viewBinding!!.recordBtn.setOnClickListener {
            if (mIsRecording) {
                stopRecording()
            } else {
                startRecording()
            }
        }
        viewBinding!!.playBtn.setOnClickListener {
            if (mIsPlaying) {
                stopPlaying()
            } else {
                startPlaying()
            }
        }
        if (!isPermissionsGranted(permissions)) {
            permissionLifecycleObserver?.requestPermissions(permissions)
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
                    viewBinding?.playBtn?.setText(R.string.mic_stop)
                    viewBinding?.recordBtn?.isEnabled = false
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
                viewBinding?.recordBtn?.setText(R.string.mic_stop)
                viewBinding?.playBtn?.isEnabled = false
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
        viewBinding?.playBtn?.isEnabled = true
        viewBinding?.recordBtn?.isEnabled = true
        viewBinding?.playBtn?.setText(R.string.mic_start_playing)
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
        viewBinding?.playBtn?.isEnabled = true
        viewBinding?.recordBtn?.isEnabled = true
        viewBinding?.recordBtn?.setText(R.string.mic_start_recording)
    }

    override val permissions = arrayOf(Manifest.permission.RECORD_AUDIO)
}