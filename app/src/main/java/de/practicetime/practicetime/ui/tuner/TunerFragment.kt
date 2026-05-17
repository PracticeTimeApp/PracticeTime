/*
 * This software is licensed under the MIT license
 *
 * Copyright (c) 2022, Javier Carbone, author Matthias Emde
 */

package de.practicetime.practicetime.ui.tuner

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import de.practicetime.practicetime.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.*

class TunerFragment : Fragment() {

    private lateinit var noteTextView: TextView
    private lateinit var frequencyTextView: TextView
    private lateinit var gaugeProgressBar: ProgressBar

    private var audioRecord: AudioRecord? = null
    private var recordingJob: Job? = null

    private val sampleRate = 44100
    private val bufferSize = AudioRecord.getMinBufferSize(
        sampleRate,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT
    ) * 4

    private val noteNames = arrayOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tuner, container, false)
        noteTextView = view.findViewById(R.id.tunerNote)
        frequencyTextView = view.findViewById(R.id.tunerFrequency)
        gaugeProgressBar = view.findViewById(R.id.tunerGauge)
        return view
    }

    override fun onStart() {
        super.onStart()
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 1001)
            return
        }
        startTuning()
    }

    override fun onStop() {
        super.onStop()
        stopTuning()
    }

    private fun startTuning() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )

        audioRecord?.startRecording()
        recordingJob = lifecycleScope.launch(Dispatchers.Default) {
            val audioData = ShortArray(bufferSize)
            while (audioRecord?.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                val readSize = audioRecord?.read(audioData, 0, bufferSize) ?: 0
                if (readSize > 0) {
                    val pitch = detectPitch(audioData, readSize)
                    if (pitch > 0) {
                        updateUI(pitch)
                    }
                }
                delay(50)
            }
        }
    }

    private fun stopTuning() {
        recordingJob?.cancel()
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
    }

    private suspend fun updateUI(pitch: Float) {
        val noteNum = 12 * log2(pitch / 440.0) + 69
        val roundedNoteNum = round(noteNum).toInt()
        val noteName = noteNames[roundedNoteNum % 12]
        val octave = (roundedNoteNum / 12) - 1
        val cents = (noteNum - roundedNoteNum) * 100

        withContext(Dispatchers.Main) {
            noteTextView.text = "$noteName$octave"
            frequencyTextView.text = String.format("%.1f Hz", pitch)
            gaugeProgressBar.progress = (cents + 50).toInt().coerceIn(0, 100)
        }
    }

    private fun detectPitch(data: ShortArray, size: Int): Float {
        // Simple Autocorrelation
        var maxCorr = 0.0
        var bestPeriod = -1

        val minPeriod = sampleRate / 1000 // 1000 Hz
        val maxPeriod = sampleRate / 50   // 50 Hz

        for (period in minPeriod..maxPeriod) {
            var corr = 0.0
            for (i in 0 until size - period) {
                corr += data[i].toDouble() * data[i + period].toDouble()
            }
            if (corr > maxCorr) {
                maxCorr = corr
                bestPeriod = period
            }
        }

        if (bestPeriod == -1) return -1f
        
        // Basic threshold to avoid noise
        if (maxCorr < 100000000.0) return -1f

        return sampleRate.toFloat() / bestPeriod
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1001 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startTuning()
        }
    }
}
