package com.example.background.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.background.KEY_IMAGE_URI

class BlurWorker(
    appContext: Context,
    parameters: WorkerParameters
) : Worker(
    appContext,
    parameters
) {
    override fun doWork(): Result {
        val appContext = applicationContext

        val resourceUri = inputData.getString(KEY_IMAGE_URI)

        makeStatusNotification("Blurring image...", appContext)

        return try {

            if (TextUtils.isEmpty(resourceUri)) {
                Log.e("TAG", "Invalid input Uri")
                throw IllegalArgumentException("Invalid input Uri")
            }

            val resolver = appContext.contentResolver

            val picture = BitmapFactory.decodeStream(
                resolver.openInputStream(Uri.parse(resourceUri))
            )

            val output = blurBitmap(picture, appContext)

            val outputUri = writeBitmapToFile(applicationContext, output)

            val outputData = workDataOf(KEY_IMAGE_URI to outputUri.toString())

            makeStatusNotification("Output is $outputUri", appContext)

            Result.success(outputData)

        } catch (throwable: Throwable) {
            Log.e("TAG", "doWork: $throwable")
            Result.failure()
        }
    }
}