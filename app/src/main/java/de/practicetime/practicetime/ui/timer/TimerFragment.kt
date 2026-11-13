/*
 * This software is licensed under the MIT license
 */

package de.practicetime.practicetime.ui.timer

import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.switchmaterial.SwitchMaterial
import de.practicetime.practicetime.R
import java.util.Locale

class TimerFragment : Fragment() {

    private lateinit var countdownText: TextView
    private lateinit var inputMinutes: EditText
    private lateinit var inputSeconds: EditText
    private lateinit var startBtn: Button
    private lateinit var stopBtn: Button
    private lateinit var loopSwitch: SwitchMaterial
    private lateinit var beepSwitch: SwitchMaterial

    private var timer: CountDownTimer? = null
    private var isRunning = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_timer, container, false)
        countdownText = view.findViewById(R.id.timerCountdown)
        inputMinutes = view.findViewById(R.id.timerInputMinutes)
        inputSeconds = view.findViewById(R.id.timerInputSeconds)
        startBtn = view.findViewById(R.id.timerStartBtn)
        stopBtn = view.findViewById(R.id.timerStopBtn)
        loopSwitch = view.findViewById(R.id.timerLoopSwitch)
        beepSwitch = view.findViewById(R.id.timerBeepSwitch)

        startBtn.setOnClickListener { startTimer() }
        stopBtn.setOnClickListener { stopTimer() }

        return view
    }

    private fun startTimer() {
        val minutes = inputMinutes.text.toString().toLongOrNull() ?: 0L
        val seconds = inputSeconds.text.toString().toLongOrNull() ?: 30L
        val totalMillis = (minutes * 60 + seconds) * 1000

        if (totalMillis <= 0) return

        isRunning = true
        startBtn.isEnabled = false
        stopBtn.isEnabled = true
        inputMinutes.isEnabled = false
        inputSeconds.isEnabled = false
        loopSwitch.isEnabled = false
        beepSwitch.isEnabled = false

        // Keep screen on while timer is running
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        timer = object : CountDownTimer(totalMillis, 100) {
            override fun onTick(millisUntilFinished: Long) {
                val sec = (millisUntilFinished / 1000) % 60
                val min = (millisUntilFinished / 1000) / 60
                countdownText.text = String.format(Locale.getDefault(), "%02d:%02d", min, sec)
            }

            override fun onFinish() {
                if (beepSwitch.isChecked) {
                    playBeep()
                }
                if (loopSwitch.isChecked && isRunning) {
                    startTimer() // Restart for endless loop
                } else {
                    stopTimer()
                }
            }
        }.start()
    }

    private fun playBeep() {
        try {
            MediaPlayer.create(requireContext(), R.raw.custom_beep)?.apply {
                setOnCompletionListener { mp -> mp.release() }
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopTimer() {
        isRunning = false
        timer?.cancel()
        timer = null
        startBtn.isEnabled = true
        stopBtn.isEnabled = false
        inputMinutes.isEnabled = true
        inputSeconds.isEnabled = true
        loopSwitch.isEnabled = true
        beepSwitch.isEnabled = true
        
        val defaultDuration = getString(R.string.goalDialogDefaultDuration)
        countdownText.text = String.format(Locale.getDefault(), "%s:%s", defaultDuration, defaultDuration)
        
        // Allow screen to turn off when timer stops
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}
