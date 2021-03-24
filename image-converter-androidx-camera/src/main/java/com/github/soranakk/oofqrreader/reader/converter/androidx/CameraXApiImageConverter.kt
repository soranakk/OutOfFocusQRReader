package com.github.soranakk.oofqrreader.reader.converter.androidx

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.camera.core.ImageProxy
import com.github.soranakk.oofqrreader.model.ImageData
import java.nio.ByteBuffer

@RequiresApi(Build.VERSION_CODES.KITKAT)
public class CameraXApiImageConverter {
    public fun convertImage(image: ImageProxy): ImageData {
        return when (image.format) {
            ImageFormat.JPEG,
            ImageFormat.HEIC -> convertCompressedDataToImageData(image)
            ImageFormat.YUV_420_888,
            ImageFormat.YUV_422_888,
            ImageFormat.YUV_444_888 -> convertYuvToImageData(image)
            ImageFormat.FLEX_RGB_888 -> convertRgbToImageData(image)
            ImageFormat.FLEX_RGBA_8888 -> convertArgbToImageData(image)
            ImageFormat.RAW_SENSOR,
            ImageFormat.RAW_PRIVATE -> {
                throw NotImplementedError("Unsupported Image Format:${image.format}")
            }
            else -> {
                throw NotImplementedError("Unknown Image Format:${image.format}")
            }
        }
    }

    private fun convertCompressedDataToImageData(image: ImageProxy): ImageData {
        val compressedData = image.toByteArray()
        val bitmap = BitmapFactory.decodeByteArray(compressedData, 0, compressedData.size).let { bitmap ->
            if (bitmap.config != Bitmap.Config.ARGB_8888) {
                bitmap.copy(Bitmap.Config.ARGB_8888, false).also { bitmap.recycle() }
            } else bitmap
        }
        val width = bitmap.width
        val height = bitmap.height
        val argbBuf = ByteBuffer.allocate(bitmap.byteCount).apply {
            bitmap.copyPixelsToBuffer(this)
            bitmap.recycle()
        }
        return ImageData(argbBuf.array(), ImageData.ImageFormat.ARGB_8888, width, height)
    }

    private fun convertYuvToImageData(image: ImageProxy): ImageData {
        val gray = image.toByteArray()
        return ImageData(gray, ImageData.ImageFormat.GRAY, image.width, image.height)
    }

    private fun convertRgbToImageData(image: ImageProxy): ImageData {
        val rgb = image.toByteArray()
        return ImageData(rgb, ImageData.ImageFormat.RGB_888, image.width, image.height)
    }

    private fun convertArgbToImageData(image: ImageProxy): ImageData {
        val argb = image.toByteArray()
        return ImageData(argb, ImageData.ImageFormat.ARGB_8888, image.width, image.height)
    }

    private fun ImageProxy.toByteArray(): ByteArray {
        val buff = this.planes[0].buffer
        buff.rewind()
        return ByteArray(buff.remaining()).apply {
            buff.get(this)
        }
    }
}