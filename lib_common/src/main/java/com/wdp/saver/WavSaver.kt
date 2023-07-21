package com.wdp.saver

import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.Executors

/**
 * 作者：王东平
 * 功能：
 * 说明：
 * 版本：1.0.0
 */
class WavSaver(
    private val path: String
) {
    private companion object {
        const val TAG = "WavSaver"
    }

    private val executor by lazy { Executors.newSingleThreadExecutor() }
    private var fos: FileOutputStream? = null
    private var sampleRate = 0L
    private var channels = 0

    fun init() {
        Log.d(TAG, "init: $path")
        deleteExitsFile()
    }

    fun setRecorderConfig(sampleRate: Int, channels: Int) {
        Log.d(TAG, "setRecorderConfig: [sampleRate:$sampleRate,channels:$channels]")
        this.sampleRate = sampleRate.toLong()
        this.channels = channels
    }

    private fun deleteExitsFile() {
        kotlin.runCatching {
            executor.execute {
                val file = File(path)
                if (file.exists()) {
                    file.delete()
                    Log.i(TAG, "delete old file")
                }
            }
        }.exceptionOrNull()?.printStackTrace()
    }

    fun saveData(data: ByteArray) {
        kotlin.runCatching {
            executor.execute {
                if (fos == null) {
                    fos = FileOutputStream(path, true).also {
                        val byteRate = 16 * sampleRate * channels / 8
                        it.write(createWavHeader(sampleRate, channels, byteRate))
                    }
                }
                fos?.write(data, 0, data.size)
                fos?.flush()
            }
        }.exceptionOrNull()?.printStackTrace()
    }

    fun getPath(): String = path

    fun close() {
        kotlin.runCatching {
            fos?.close()
            fos = null
        }.also {
            it.exceptionOrNull()?.printStackTrace()
        }
    }

    private fun createWavHeader(sampleRate: Long, channels: Int, byteRate: Long): ByteArray {
        return ByteArray(44).apply {
            this[0] = 'R'.code.toByte() // RIFF/WAVE this
            this[1] = 'I'.code.toByte()
            this[2] = 'F'.code.toByte()
            this[3] = 'F'.code.toByte()
            this[4] = 0  // RIFF chunk size *PLACEHOLDER*
            this[5] = 0
            this[6] = 0
            this[7] = 0
            this[8] = 'W'.code.toByte() // WAVE
            this[9] = 'A'.code.toByte()
            this[10] = 'V'.code.toByte()
            this[11] = 'E'.code.toByte()
            this[12] = 'f'.code.toByte() // 'fmt ' chunk
            this[13] = 'm'.code.toByte()
            this[14] = 't'.code.toByte()
            this[15] = ' '.code.toByte()
            this[16] = 16 // 4 bytes: size of 'fmt ' chunk
            this[17] = 0
            this[18] = 0
            this[19] = 0
            this[20] = 1 // format = 1
            this[21] = 0
            this[22] = channels.toByte()
            this[23] = 0
            this[24] = (sampleRate and 0xffL).toByte()
            this[25] = (sampleRate shr 8 and 0xffL).toByte()
            this[26] = (sampleRate shr 16 and 0xffL).toByte()
            this[27] = (sampleRate shr 24 and 0xffL).toByte()
            this[28] = (byteRate and 0xffL).toByte()
            this[29] = (byteRate shr 8 and 0xffL).toByte()
            this[30] = (byteRate shr 16 and 0xffL).toByte()
            this[31] = (byteRate shr 24 and 0xffL).toByte()
            this[32] = (2 * 16 / 8).toByte() // block align
            this[33] = 0
            this[34] = 16 // bits per sample
            this[35] = 0
            this[36] = 'd'.code.toByte() // data
            this[37] = 'a'.code.toByte()
            this[38] = 't'.code.toByte()
            this[39] = 'a'.code.toByte()
            this[40] = 0  // data chunk size *PLACEHOLDER*
            this[41] = 0
            this[42] = 0
            this[43] = 0
        }
    }
}