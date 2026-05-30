package com.example.airline.utils

import android.graphics.*
import androidx.camera.core.ImageProxy
import android.media.Image
import android.graphics.ImageFormat
import android.graphics.Rect
import java.io.ByteArrayOutputStream

object ImageUtils {
    fun imageProxyToBitmap(image: ImageProxy): Bitmap? {
        val planes = image.planes
        if (planes.size < 3) return null

        val yPlane = planes[0]
        val uPlane = planes[1]
        val vPlane = planes[2]

        val yBuffer = yPlane.buffer
        val uBuffer = uPlane.buffer.duplicate()
        val vBuffer = vPlane.buffer.duplicate()

        val ySize = yBuffer.remaining()
        val uvSize = yBuffer.remaining() / 2
        val nv21 = ByteArray(ySize + uvSize)

        yBuffer.get(nv21, 0, ySize)

        val width = image.width
        val height = image.height
        val chromaRowStride = uPlane.rowStride
        val chromaPixelStride = uPlane.pixelStride

        var offset = ySize
        for (row in 0 until height / 2) {
            for (col in 0 until width / 2) {
                val uvIndex = row * chromaRowStride + col * chromaPixelStride
                if (uvIndex < vBuffer.limit() && uvIndex < uBuffer.limit()) {
                    nv21[offset++] = vBuffer.get(uvIndex)
                    nv21[offset++] = uBuffer.get(uvIndex)
                }
            }
        }

        return try {
            val yuvImage = YuvImage(nv21, ImageFormat.NV21, width, height, null)
            val out = ByteArrayOutputStream()
            yuvImage.compressToJpeg(Rect(0, 0, width, height), 90, out)
            val bytes = out.toByteArray()
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (e: Exception) {
            null
        }
    }

    fun toGrayscale(src: Bitmap): Bitmap {
        val bmpGrayscale = Bitmap.createBitmap(src.width, src.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmpGrayscale)
        val paint = Paint()
        val cm = ColorMatrix()
        cm.setSaturation(0f)
        val f = ColorMatrixColorFilter(cm)
        paint.colorFilter = f
        canvas.drawBitmap(src, 0f, 0f, paint)
        return bmpGrayscale
    }

    fun adjustContrastBrightness(src: Bitmap, contrast: Float = 1.2f, brightness: Float = 0f): Bitmap {
        val cm = ColorMatrix(floatArrayOf(
            contrast, 0f, 0f, 0f, brightness,
            0f, contrast, 0f, 0f, brightness,
            0f, 0f, contrast, 0f, brightness,
            0f, 0f, 0f, 1f, 0f
        ))
        val ret = Bitmap.createBitmap(src.width, src.height, src.config ?: Bitmap.Config.ARGB_8888)
        val canvas = Canvas(ret)
        val paint = Paint()
        paint.colorFilter = ColorMatrixColorFilter(cm)
        canvas.drawBitmap(src, 0f, 0f, paint)
        return ret
    }

    fun rotate(src: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)
    }
}
