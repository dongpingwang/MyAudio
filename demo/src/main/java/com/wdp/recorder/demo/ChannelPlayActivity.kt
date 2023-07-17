package com.wdp.recorder.demo

import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wdp.common.Logger
import com.wdp.recorder.demo.databinding.ActivityChannelPlayingBinding

/**
 * 1.MediaPlayer.setAudioStreamType可以和通道关联起来
 * 2.不做处理，不同通道一起播放会混音
 */
class ChannelPlayActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "ChannelPlayActivity"
    }

    private lateinit var binding: ActivityChannelPlayingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChannelPlayingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnMusicChannelPlay.setOnClickListener {
            playMusicChannel()
        }

        binding.btnAlarmChannelPlay.setOnClickListener {
            playAlarmChannel()
        }
    }

    private fun playMusicChannel() {
        Logger.d(TAG, "playMusicChannel")
        MediaPlayer().apply {
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

    private fun playAlarmChannel() {
        Logger.d(TAG, "playAlarmChannel")
        MediaPlayer().apply {
            val afd = assets.openFd("mljyyj-qifenle.wav")
            setAudioStreamType(AudioManager.STREAM_ALARM)
            setDataSource(afd)
            prepareAsync()
            setOnPreparedListener {
                Logger.d(TAG, "ALARM.MediaPlayer.OnPrepared...")
                start()
            }
        }
    }
}