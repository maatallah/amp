package com.automusicplayer.media

import android.content.Context
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FileLogger {
    private const val LOG_FILE_NAME = "amp_debug_logs.txt"

    private fun getLogFile(context: Context): File? {
        val dir = context.getExternalFilesDir(null) ?: return null
        if (!dir.exists()) dir.mkdirs()
        return File(dir, LOG_FILE_NAME)
    }

    @Synchronized
    fun log(context: Context, message: String) {
        try {
            val file = getLogFile(context) ?: return
            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US).format(Date())
            val writer = FileWriter(file, true)
            writer.append("$timestamp [INFO] $message\n")
            writer.close()
        } catch (e: Exception) {
            // Suppress internal logging errors to prevent secondary crashing
        }
    }

    @Synchronized
    fun logThrowable(context: Context, label: String, throwable: Throwable) {
        try {
            val file = getLogFile(context) ?: return
            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US).format(Date())
            val sw = StringWriter()
            throwable.printStackTrace(PrintWriter(sw))
            
            val writer = FileWriter(file, true)
            writer.append("$timestamp [ERROR] $label\n")
            writer.append("Exception: ${throwable.javaClass.name}: ${throwable.message}\n")
            writer.append("Stacktrace:\n$sw\n")
            writer.append("-------------------------------------------------------\n")
            writer.close()
        } catch (e: Exception) {
            // Suppress internal logging errors to prevent secondary crashing
        }
    }
}
