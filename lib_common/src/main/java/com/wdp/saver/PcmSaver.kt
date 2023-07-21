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
class PcmSaver(
    private val path: String
) {
    private val executor by lazy { Executors.newSingleThreadExecutor() }

    private companion object {
        const val TAG = "PcmSaver"
    }

    private var fos: FileOutputStream? = null

    fun init() {
        Log.d(TAG, "init: $path")
        deleteExitsFile()
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
                    fos = FileOutputStream(path, true)
                }
                fos?.write(data, 0, data.size)
                fos?.flush()
            }
        }.exceptionOrNull()?.printStackTrace()
    }

    fun close() {
        kotlin.runCatching {
            fos?.close()
            fos = null
        }.also {
            it.exceptionOrNull()?.printStackTrace()
        }
    }
}