package com.wdp.audio.monitor

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioFocusInfo
import android.media.AudioManager
import android.media.audiopolicy.AudioPolicy
import android.util.Log
import androidx.core.content.getSystemService
import java.util.concurrent.CopyOnWriteArrayList

class AudioFocusMonitor private constructor(private val context: Context) {

    companion object {
        private const val TAG = "AudioFocusMonitor"

        @SuppressLint("StaticFieldLeak")
        private lateinit var audioFocusMonitor: AudioFocusMonitor

        fun getInstance(context: Context): AudioFocusMonitor {
            if (!this::audioFocusMonitor.isInitialized) {
                audioFocusMonitor = AudioFocusMonitor(context)
            }
            return audioFocusMonitor
        }
    }

    private val monitors by lazy { CopyOnWriteArrayList<IAudioFocusMonitor>() }

    private val audioPolicy by lazy {
        val builder = AudioPolicy.Builder(context)
            .setIsAudioFocusPolicy(false)
        builder.setAudioPolicyFocusListener(object :
            AudioPolicy.AudioPolicyFocusListener() {
            override fun onAudioFocusGrant(
                afi: AudioFocusInfo?,
                requestResult: Int
            ) {
                Log.d(TAG, "onAudioFocusGrant: pkg=${afi?.packageName} result=$requestResult")
                monitors.forEach { it.onAudioFocusGrant(afi, requestResult) }
            }

            override fun onAudioFocusLoss(
                afi: AudioFocusInfo?,
                wasNotified: Boolean
            ) {
                Log.d(TAG, "onAudioFocusLoss: pkg=${afi?.packageName} wasNotified=$wasNotified")
                monitors.forEach { it.onAudioFocusLoss(afi, wasNotified) }
            }

            override fun onAudioFocusRequest(
                afi: AudioFocusInfo?,
                requestResult: Int
            ) {
                Log.d(TAG, "onAudioFocusRequest: pkg=${afi?.packageName} result=$requestResult")
                monitors.forEach { it.onAudioFocusRequest(afi, requestResult) }
            }

            override fun onAudioFocusAbandon(afi: AudioFocusInfo?) {
                Log.d(TAG, "onAudioFocusAbandon: pkg=${afi?.packageName}")
                monitors.forEach { it.onAudioFocusAbandon(afi) }
            }
        })
        builder.build()
    }

    fun registerAudioFocusMonitor(monitor: IAudioFocusMonitor): Boolean {
        if (monitors.isEmpty()) {
            registerAudioPolicy()
        }
        return monitors.contains(monitor) || monitors.add(monitor)
    }

    fun unregisterAudioFocusMonitor(monitor: IAudioFocusMonitor): Boolean {
        val result = monitors.remove(monitor)
        if (monitors.isEmpty()) {
            unregisterAudioPolicy()
        }
        return result
    }

    private fun registerAudioPolicy() {
        context.getSystemService<AudioManager>()?.apply {
            val registerAudioPolicy =
                javaClass.getDeclaredMethod(
                    "registerAudioPolicy",
                    AudioPolicy::class.java
                )
            registerAudioPolicy.isAccessible = true
            val result = registerAudioPolicy.invoke(this, audioPolicy) as Int
            Log.d(TAG, "registerAudioPolicy: result=$result")
        }
    }

    private fun unregisterAudioPolicy() {
        context.getSystemService<AudioManager>()?.apply {
            val unregisterAudioPolicy =
                javaClass.getDeclaredMethod(
                    "unregisterAudioPolicy",
                    AudioPolicy::class.java
                )
            unregisterAudioPolicy.isAccessible = true
            val result = unregisterAudioPolicy.invoke(this, audioPolicy) as Int
            Log.d(TAG, "unregisterAudioPolicy: result=$result")
        }
    }

    interface IAudioFocusMonitor {
        fun onAudioFocusGrant(afi: AudioFocusInfo?, requestResult: Int)
        fun onAudioFocusLoss(afi: AudioFocusInfo?, wasNotified: Boolean)
        fun onAudioFocusRequest(afi: AudioFocusInfo?, requestResult: Int)
        fun onAudioFocusAbandon(afi: AudioFocusInfo?)
    }
}