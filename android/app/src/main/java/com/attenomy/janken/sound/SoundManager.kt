package com.attenomy.janken.sound

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

class SoundManager {
    private val scope = CoroutineScope(Dispatchers.Default)
    var isSoundEnabled: Boolean = true

    fun playTap() {
        if (!isSoundEnabled) return
        scope.launch {
            generateTone(frequency = 880.0, durationMs = 40, attackMs = 5, decayMs = 35)
        }
    }

    fun playSelect() {
        if (!isSoundEnabled) return
        scope.launch {
            generateTone(frequency = 660.0, durationMs = 60, attackMs = 10, decayMs = 50)
        }
    }

    fun playCountdownTick() {
        if (!isSoundEnabled) return
        scope.launch {
            generateTone(frequency = 523.25, durationMs = 80, attackMs = 5, decayMs = 75)
        }
    }

    fun playShoot() {
        if (!isSoundEnabled) return
        scope.launch {
            generateTone(frequency = 1046.5, durationMs = 120, attackMs = 5, decayMs = 115)
        }
    }

    fun playWin() {
        if (!isSoundEnabled) return
        scope.launch {
            // Arpeggio: C5 (523Hz), E5 (659Hz), G5 (784Hz), C6 (1046Hz)
            val notes = listOf(523.25, 659.25, 783.99, 1046.50)
            for (freq in notes) {
                generateTone(freq, 70, 5, 65)
            }
        }
    }

    fun playLose() {
        if (!isSoundEnabled) return
        scope.launch {
            // Sad tone: 440Hz down to 330Hz
            val notes = listOf(440.0, 392.0, 349.23, 293.66)
            for (freq in notes) {
                generateTone(freq, 90, 10, 80)
            }
        }
    }

    fun playDraw() {
        if (!isSoundEnabled) return
        scope.launch {
            generateTone(493.88, 60, 5, 55)
            generateTone(493.88, 80, 5, 75)
        }
    }

    private fun generateTone(
        frequency: Double,
        durationMs: Int,
        attackMs: Int = 10,
        decayMs: Int = durationMs - attackMs
    ) {
        try {
            val sampleRate = 44100
            val numSamples = (durationMs * sampleRate) / 1000
            val sample = ShortArray(numSamples)
            val attackSamples = (attackMs * sampleRate) / 1000
            val decaySamples = (decayMs * sampleRate) / 1000

            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val rawSine = sin(2.0 * Math.PI * frequency * t)

                // Linear envelope (attack + sustain + decay)
                val envelope = when {
                    i < attackSamples -> i.toDouble() / attackSamples.coerceAtLeast(1)
                    i > numSamples - decaySamples -> (numSamples - i).toDouble() / decaySamples.coerceAtLeast(1)
                    else -> 1.0
                }

                sample[i] = (rawSine * envelope * Short.MAX_VALUE * 0.45).toInt().toShort()
            }

            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(sample.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(sample, 0, sample.size)
            audioTrack.play()
            Thread.sleep(durationMs.toLong() + 10)
            audioTrack.release()
        } catch (_: Exception) {
            // Ignore audio generation failures gracefully
        }
    }
}
