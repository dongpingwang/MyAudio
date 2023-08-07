package com.wdp.recorder.demo

import android.annotation.SuppressLint
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.VolumeShaper
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wdp.common.Logger
import com.wdp.recorder.demo.databinding.ActivityVolumeShaperBinding


/**
 *1.上层可以通过VolumeShaper来压低声音
 *
 */
@SuppressLint("NewApi")
class VolumeShaperActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "VolumeShaperActivity"
    }

    private var player: MediaPlayer? = null

    private lateinit var binding: ActivityVolumeShaperBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVolumeShaperBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnPlay.setOnClickListener {
            play()
        }

        binding.btnVolumeShaper.setOnClickListener {
            shaper()
        }

        binding.btnDuck.setOnClickListener {
            duck()
        }
    }

    private fun play() {
        Logger.d(TAG, "play---------")
        player = MediaPlayer().apply {
            val afd = assets.openFd("liuhuan-fenhuanyufei.mp3")
            setAudioStreamType(AudioManager.STREAM_MUSIC)
            setDataSource(afd)
            prepareAsync()
            setOnPreparedListener {
                Logger.d(TAG, "MUSIC.MediaPlayer.OnPrepared...")
                start()
            }
        }
    }


    private fun shaper() {
        Logger.d(TAG, "shaper---------")
        val config: VolumeShaper.Configuration = VolumeShaper.Configuration.Builder()
            .setDuration(500)
            .setCurve(floatArrayOf(0f, 1f), floatArrayOf(1f, 0.1f))
            .setInterpolatorType(VolumeShaper.Configuration.INTERPOLATOR_TYPE_LINEAR)
            .build()
        player?.createVolumeShaper(config)?.apply(VolumeShaper.Operation.PLAY) ?: kotlin.run {
            Logger.e(TAG, "player is null")
        }
    }

    private val VOLUME_SHAPER_SYSTEM_DUCK_ID = 1
    val OPTION_FLAG_CLOCK_TIME = 1 shl 1

    protected fun getFocusRampTimeMs(focusGain: Int, attr: AudioAttributes): Int {
        return when (attr.usage) {
            AudioAttributes.USAGE_MEDIA, AudioAttributes.USAGE_GAME -> 1000
            AudioAttributes.USAGE_ALARM, AudioAttributes.USAGE_NOTIFICATION_RINGTONE, AudioAttributes.USAGE_ASSISTANT, AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY, AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE -> 700
            AudioAttributes.USAGE_VOICE_COMMUNICATION, AudioAttributes.USAGE_VOICE_COMMUNICATION_SIGNALLING, AudioAttributes.USAGE_NOTIFICATION, AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_REQUEST, AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_INSTANT, AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_DELAYED, AudioAttributes.USAGE_NOTIFICATION_EVENT, AudioAttributes.USAGE_ASSISTANCE_SONIFICATION -> 500
            AudioAttributes.USAGE_UNKNOWN -> 0
            else -> 0
        }
    }

    private fun duck() {
        Logger.d(TAG, "duck---------")
        val builder = VolumeShaper.Configuration.Builder()

//        val setId = builder.javaClass.getDeclaredMethod("setId", Int::class.java)
//        setId.isAccessible = true
//        setId.invoke(builder,VOLUME_SHAPER_SYSTEM_DUCK_ID)
//
//        val setOptionFlags = builder.javaClass.getDeclaredMethod("setOptionFlags", Int::class.java)
//        setOptionFlags.isAccessible = true
//        setOptionFlags.invoke(builder,OPTION_FLAG_CLOCK_TIME)

        builder.setCurve(floatArrayOf(0f, 1f), floatArrayOf(1f, 0.1f))
            .setDuration(500)
        val DUCK_VSHAPE: VolumeShaper.Configuration = builder.build()
        player?.createVolumeShaper(DUCK_VSHAPE)?.apply(VolumeShaper.Operation.PLAY) ?: kotlin.run {
            Logger.e(TAG, "player is null")
        }
    }

}