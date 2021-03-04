package com.github.soranakk.oofqrreader.util

import com.github.soranakk.oofqrreader.model.ImageData
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc

object MatUtil {
    fun convertYuvToGray(image: ImageData): Mat {
        return Mat().apply {
            val yuv = Mat(image.height + image.height / 2, image.width, CvType.CV_8UC1).apply {
                put(0, 0, image.data)
            }
            Imgproc.cvtColor(yuv, this, Imgproc.COLOR_YUV2GRAY_NV21, 4)
            yuv.release()
        }
    }
}

