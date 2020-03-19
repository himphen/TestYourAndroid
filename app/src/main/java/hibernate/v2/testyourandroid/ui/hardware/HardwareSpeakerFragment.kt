package hibernate.v2.testyourandroid.ui.hardware

import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_speaker.*

/**
 * Created by himphen on 21/5/16.
 */
class HardwareSpeakerFragment : BaseFragment() {
    private var vibrateType = 0
    private var isRinging = false
    private var isVibrating = false
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var vibratorService: Vibrator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_speaker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        context?.let { context ->
            vibratorService = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            val array = resources.getStringArray(R.array.vibrate_string_array)
            val adapter = ArrayAdapter(
                context,
                android.R.layout.simple_spinner_item, array
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            vibrateSpinner.adapter = adapter
            vibrateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>?, view: View,
                    position: Int, id: Long
                ) {
                    vibrateType = position
                    stopVibrate()
                }

                override fun onNothingSelected(arg0: AdapterView<*>?) {}
            }
            setListener()
        }
    }

    private fun startVibrate() {
        when (vibrateType) {
            0 -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibratorService.vibrate(
                        VibrationEffect.createOneShot(
                            30000,
                            VibrationEffect.DEFAULT_AMPLITUDE
                        )
                    )
                } else {
                    @Suppress("DEPRECATION")
                    vibratorService.vibrate(30000)
                }
                vibrateButton.setText(R.string.vibrate_stop_button)
                isVibrating = true
            }
            1 -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibratorService.vibrate(
                        VibrationEffect.createWaveform(
                            longArrayOf(100, 200, 100), 0
                        )
                    )
                } else {
                    @Suppress("DEPRECATION")
                    vibratorService.vibrate(longArrayOf(100, 200, 100), 0)
                }
                vibrateButton.setText(R.string.vibrate_stop_button)
                isVibrating = true
            }
        }
    }

    private fun setListener() {
        ringButton.setOnClickListener {
            if (isRinging) {
                stopPlayer()
            } else {
                startPlayer()
            }
        }
        vibrateButton.setOnClickListener {
            if (isVibrating) {
                stopVibrate()
            } else {
                startVibrate()
            }
        }
    }

    override fun onPause() {
        if (isVibrating) {
            stopVibrate()
        }
        if (isRinging) {
            stopPlayer()
        }
        super.onPause()
    }

    private fun startPlayer() {
        try {
            mediaPlayer = MediaPlayer.create(context, R.raw.testring)
            mediaPlayer?.let { mediaPlayer ->
                mediaPlayer.setOnCompletionListener { stopPlayer() }
                mediaPlayer.start()
                isRinging = true
                ringButton.setText(R.string.ring_stop_button)
            }
        } catch (e: Exception) {
            Toast.makeText(context, "ERROR", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopPlayer() {
        if (isRinging) {
            ringButton.setText(R.string.ring_button)
            isRinging = false
            mediaPlayer?.let { mediaPlayer ->
                mediaPlayer.stop()
                mediaPlayer.release()
            }
        }
    }

    private fun stopVibrate() {
        vibratorService.cancel()
        vibrateButton.setText(R.string.vibrate_button)
        isVibrating = false
    }
}