package com.github.soranakk.oofqrreader.util

import com.github.soranakk.oofqrreader.model.ImageData
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc

object MatUtil {

    fun convertImageDataToGray(image: ImageData): Mat {
        return when (image.format) {
            ImageData.ImageFormat.YUV,
            ImageData.ImageFormat.GRAY -> convertGray(image)
            ImageData.ImageFormat.ARGB_8888 -> convertArgbToGray(image)
        }
    }

    private fun convertGray(image: ImageData): Mat {
        return Mat(image.height, image.width, CvType.CV_8UC1).apply {
            put(0, 0, image.data)
        }
    }

    private fun convertArgbToGray(image: ImageData): Mat {
        return Mat().apply {
            val argb = Mat().apply {
                put(0, 0, image.data)
            }
            Imgproc.cvtColor(argb, this, Imgproc.COLOR_RGBA2GRAY)
            argb.release()
        }
    }
}

