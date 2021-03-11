package com.github.soranakk.oofqrreader.util

import com.github.soranakk.oofqrreader.model.ImageData
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc
import java.nio.ByteBuffer

object MatUtil {

    fun convertImageDataToGray(image: ImageData): Mat {
        return when (image.format) {
            ImageData.ImageFormat.YUV,
            ImageData.ImageFormat.GRAY -> convertGray(image)
            ImageData.ImageFormat.ARGB_8888 -> convertArgbToGray(image)
            ImageData.ImageFormat.RGB_888 -> convertRgbToGray(image)
        }
    }

    private fun convertGray(image: ImageData): Mat {
        return Mat(image.height, image.width, CvType.CV_8UC1).apply {
            put(0, 0, image.data)
        }
    }

    private fun convertArgbToGray(image: ImageData): Mat {
        return Mat(image.height, image.width, CvType.CV_8UC1).apply {
            val byteBuffer = ByteBuffer.allocateDirect(image.data.size).apply {
                put(image.data)
            }
            val argb = Mat(image.height, image.width, CvType.CV_8UC4, byteBuffer)
            Imgproc.cvtColor(argb, this, Imgproc.COLOR_RGBA2GRAY)
            argb.release()
        }
    }

    private fun convertRgbToGray(image: ImageData): Mat {
        return Mat(image.height, image.width, CvType.CV_8UC1).apply {
            val byteBuffer = ByteBuffer.allocateDirect(image.data.size).apply {
                put(image.data)
            }
            val rgb = Mat(image.height, image.width, CvType.CV_8UC3, byteBuffer)
            Imgproc.cvtColor(rgb, this, Imgproc.COLOR_RGB2GRAY)
            rgb.release()
        }
    }
}

