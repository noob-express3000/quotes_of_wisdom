package com.shipaton.quotesofwisdom.ui.home

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlin.math.PI
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.tanh

class StreakHornPlayer {
    private val lock = Any()
    private val samples by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { buildHornSamples() }
    private var track: AudioTrack? = null

    fun play() {
        synchronized(lock) {
            val audioTrack = track ?: buildTrack(samples).also { track = it } ?: return
            try {
                if (audioTrack.playState == AudioTrack.PLAYSTATE_PLAYING) {
                    audioTrack.stop()
                }
                audioTrack.setPlaybackHeadPosition(0)
                audioTrack.play()
            } catch (_: Throwable) {
                try {
                    audioTrack.release()
                } catch (_: Throwable) {
                }
                track = null
            }
        }
    }

    fun release() {
        synchronized(lock) {
            val audioTrack = track ?: return
            try {
                if (audioTrack.playState == AudioTrack.PLAYSTATE_PLAYING) {
                    audioTrack.stop()
                }
            } catch (_: Throwable) {
            }
            try {
                audioTrack.release()
            } catch (_: Throwable) {
            }
            track = null
        }
    }

    private fun buildTrack(data: ShortArray): AudioTrack? = try {
        AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(SAMPLE_RATE)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setTransferMode(AudioTrack.MODE_STATIC)
            .setBufferSizeInBytes(data.size * 2)
            .build()
            .also { audioTrack ->
                audioTrack.write(data, 0, data.size, AudioTrack.WRITE_BLOCKING)
                audioTrack.setVolume(0.78f)
            }
    } catch (_: Throwable) {
        null
    }

    private fun buildHornSamples(): ShortArray {
        val sampleCount = (SAMPLE_RATE * DURATION_SECONDS).toInt()
        val result = ShortArray(sampleCount)
        var phase = 0.0

        for (index in 0 until sampleCount) {
            val time = index.toDouble() / SAMPLE_RATE
            val attack = min(1.0, time / 0.12)
            val release = min(1.0, (DURATION_SECONDS - time) / 0.34)
            val envelope = min(attack, release).coerceAtLeast(0.0)
            val vibrato = 1.0 + 0.0055 * sin(2.0 * PI * 5.1 * time)
            phase += 2.0 * PI * BASE_FREQUENCY * vibrato / SAMPLE_RATE

            val body =
                0.74 * sin(phase) +
                    0.31 * sin(phase * 2.0) +
                    0.18 * sin(phase * 3.0) +
                    0.10 * sin(phase * 4.0) +
                    0.055 * sin(phase * 5.0)
            val slowPulse = 0.93 + 0.07 * sin(2.0 * PI * 1.45 * time)
            val sample = tanh(body * 1.12) * envelope * slowPulse * 0.72

            result[index] = (sample.coerceIn(-1.0, 1.0) * Short.MAX_VALUE).toInt().toShort()
        }

        return result
    }

    private companion object {
        const val SAMPLE_RATE = 44_100
        const val DURATION_SECONDS = 1.8
        const val BASE_FREQUENCY = 116.54
    }
}
