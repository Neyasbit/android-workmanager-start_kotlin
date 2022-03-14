package com.example.background.workers

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.background.OUTPUT_PATH
import java.io.File

private const val TAG = "CleanupWorker"

class CleanupWorker(
    appContext: Context,
    parameters: WorkerParameters
) : Worker(
    appContext,
    parameters
) {
    override fun doWork(): Result {

        makeStatusNotification("Cleaning up old temporary files", applicationContext)

        sleep()

        return try {
            val outputDirectory = File(applicationContext.filesDir, OUTPUT_PATH)
            if (outputDirectory.exists()) {
                val entries = outputDirectory.listFiles()
                if (entries != null) {
                    for (entrie in entries) {
                        val name = entrie.name
                        if (name.isNotEmpty() && name.endsWith(".png")) {
                            val deleted = entrie.delete()
                            Log.i(TAG, "Deleted $name - $deleted")
                        }
                    }
                }
            }
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
}