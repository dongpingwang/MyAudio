package com.wdp.recorder.demo

import android.media.AudioFocusInfo
import android.media.AudioManager
import android.media.audiopolicy.AudioPolicy
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import com.wdp.audio.monitor.AudioFocusMonitor
import com.wdp.common.Logger
import com.wdp.recorder.demo.databinding.ActivityAudioFocusBinding

/**
 * 1.可以注册外部的AudioPolicy来监听AudioFocus变化
 * 2.需要系统权限，有些系统可能对这个做了限制
 */
class AudioFocusActivity : AppCompatActivity() {

    companion object {
        const val TAG = "AudioFocusActivity"
    }

    private lateinit var binding: ActivityAudioFocusBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioFocusBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AudioFocusMonitor.getInstance(this)
            .registerAudioFocusMonitor(object : AudioFocusMonitor.IAudioFocusMonitor {
                override fun onAudioFocusGrant(afi: AudioFocusInfo?, requestResult: Int) {
                    Logger.d(TAG, "onAudioFocusGrant ==> ${afi?.packageName} // $requestResult")
                }

                override fun onAudioFocusLoss(afi: AudioFocusInfo?, wasNotified: Boolean) {
                    Logger.d(TAG, "onAudioFocusLoss ==> ${afi?.packageName} // $wasNotified")
                }

                override fun onAudioFocusRequest(afi: AudioFocusInfo?, requestResult: Int) {
                    Logger.d(TAG, "onAudioFocusRequest ==> ${afi?.packageName} // $requestResult")
                }

                override fun onAudioFocusAbandon(afi: AudioFocusInfo?) {
                    Logger.d(TAG, "onAudioFocusAbandon ==> ${afi?.packageName}")
                }
            })
        binding.btnAudioFocus1.setOnClickListener {
            requestAudioFocus1()

        }
        binding.btnAudioFocus2.setOnClickListener {
            requestAudioFocus2()
        }
    }

    private fun requestAudioFocus1() {
        getSystemService<AudioManager>()?.apply {
            val result = requestAudioFocus({
                Logger.d(TAG, "onAudioFocusChange1: $it", Throwable())
            }, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
            Logger.d(TAG, "requestAudioFocus1 ==> $result")
        }
    }

    private fun requestAudioFocus2() {
        getSystemService<AudioManager>()?.apply {
            val result = requestAudioFocus({
                Logger.d(TAG, "onAudioFocusChange2: $it")
            }, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
            Logger.d(TAG, "requestAudioFocus2 ==> $result")
        }
    }
}