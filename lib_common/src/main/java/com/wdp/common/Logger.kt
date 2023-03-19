package com.wdp.common

import android.util.Log
import android.util.Log.ASSERT
import android.util.Log.ERROR
import android.util.Log.WARN
import android.util.Log.INFO
import android.util.Log.DEBUG
import android.util.Log.VERBOSE
import androidx.annotation.IntDef
import org.json.JSONArray
import org.json.JSONObject
import java.io.StringReader
import java.io.StringWriter
import javax.xml.transform.*
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

@Retention(AnnotationRetention.SOURCE)
@IntDef(ASSERT, ERROR, WARN, INFO, DEBUG, VERBOSE, flag = true)
annotation class LogLevel

interface ILog {
    fun log(@LogLevel priority: Int, tag: String, msg: String, tr: Throwable?)
    fun isLoggable(@LogLevel priority: Int): Boolean
}

data class LogConfig(val prefix: String? = null, val saveLogFile: Boolean = false)

object Logger {

    private const val LOG_MAX_LENGTH = 2000
    private const val JSON_INDENT = 2

    private var logger: ILog = AndroidLog()
    private var config: LogConfig? = null
    private val fileLogger: ILog by lazy { FileLog() }

    fun setLog(logger: ILog) {
        Logger.logger = logger
    }

    fun setConfig(config: LogConfig) {
        Logger.config = config
    }

    private fun log(@LogLevel priority: Int, tag: String, msg: String, tr: Throwable? = null) {
        val finalTag = config?.prefix + tag
        logger.log(priority, finalTag, msg, tr)

        config?.let {
            if (it.saveLogFile) {
                fileLogger.log(priority, tag, msg, tr)
            }
        }
    }

    fun v(tag: String, msg: String, tr: Throwable? = null) = log(VERBOSE, tag, msg, tr)

    fun d(tag: String, msg: String, tr: Throwable? = null) = log(DEBUG, tag, msg, tr)

    fun i(tag: String, msg: String, tr: Throwable? = null) = log(INFO, tag, msg, tr)

    fun w(tag: String, msg: String, tr: Throwable? = null) = log(WARN, tag, msg, tr)

    fun e(tag: String, msg: String, tr: Throwable? = null) = log(ERROR, tag, msg, tr)

    fun wtf(tag: String, msg: String, tr: Throwable? = null) = log(ASSERT, tag, msg, tr)

    fun longLog(tag: String, msg: String) {
        for (start in 0..msg.length step LOG_MAX_LENGTH) {
            val end = msg.length.coerceAtMost(start + LOG_MAX_LENGTH)
            val text = msg.substring(start)
            d(tag, "[$start-$end] -> $text")
        }
    }

    fun xml(tag: String, msg: String) {
        try {
            val xmlInput: Source = StreamSource(StringReader(msg))
            val xmlOutput = StreamResult(StringWriter())
            val transformer: Transformer = TransformerFactory.newInstance().newTransformer()
            transformer.setOutputProperty(OutputKeys.INDENT, "yes")
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
            transformer.transform(xmlInput, xmlOutput)
            d(tag, xmlOutput.writer.toString().replaceFirst(">", ">\n"))
        } catch (e: TransformerException) {
            e(tag, "Invalid Xml")
        }
    }

    fun json(tag: String, msg: String) {
        var json = msg
        if (json.isNotEmpty()) {
            json = json.trim()
            if (json.startsWith("{")) {
                try {
                    d(tag, JSONObject(json).toString(JSON_INDENT))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else if (json.startsWith("[")) {
                try {
                    d(tag, JSONArray(json).toString(JSON_INDENT))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                e(tag, "Invalid Json")
            }
        } else {
            e(tag, "Invalid Json")
        }
    }
}


private class AndroidLog : ILog {

    override fun log(@LogLevel priority: Int, tag: String, msg: String, tr: Throwable?) {
        if (priority == ASSERT) {
            Log.wtf(tag, msg, tr)
        } else {
            Log.println(priority, tag, msg + "\n" + getStackTraceString(tr))
        }
    }

    override fun isLoggable(@LogLevel priority: Int) = true

    private fun getStackTraceString(tr: Throwable?): String {
        return Log.getStackTraceString(tr)
    }
}

private class FileLog : ILog {

    override fun log(priority: Int, tag: String, msg: String, tr: Throwable?) {
    }

    // 可以做剩余空间检查等等
    override fun isLoggable(priority: Int): Boolean = true
}

