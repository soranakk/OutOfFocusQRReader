package com.github.soranakk.outoffocusqrreader.extension

import android.graphics.Bitmap
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.Point
import org.opencv.core.Rect
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

fun Mat.clipRect(rect: Rect, autoRelease: Boolean = true): Mat = Mat(this, rect).also { if (autoRelease) this.release() }

fun Mat.convertGray2Bitmap(autoRelease: Boolean = true): Bitmap {
    val bitmap = Bitmap.createBitmap(this.width(), this.height(), Bitmap.Config.ARGB_8888)
    val rgba = Mat()
    Imgproc.cvtColor(this, rgba, Imgproc.COLOR_GRAY2RGBA)
    Utils.matToBitmap(rgba, bitmap)
    rgba.release()
    if (autoRelease) this.release()
    return bitmap
}

fun Mat.erode(size: Size, iterations: Int = 10, autoRelease: Boolean = true): Mat {
    val result = Mat(this.height(), this.width(), this.type())
    val kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, size)
    Imgproc.erode(this, result, kernel, Point(-1.0, -1.0), iterations, Core.BORDER_CONSTANT, WHITE)
    kernel.release()
    if (autoRelease) this.release()
    return result
}

fun Mat.findContours(autoRelease: Boolean = true): List<MatOfPoint> {
    val result = mutableListOf<MatOfPoint>()
    val hierarchy = Mat()
    Imgproc.findContours(this, result, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE)
    hierarchy.release()
    if (autoRelease) this.release()
    return result
}

fun Mat.threshold(thresh: Double, autoRelease: Boolean = true): Mat {
    val result = Mat(this.height(), this.width(), this.type())
    Imgproc.threshold(this, result, thresh, 255.0, Imgproc.THRESH_BINARY)
    if (autoRelease) this.release()
    return result
}

fun Mat.thresholdOTSU(autoRelease: Boolean = true): Mat {
    val result = Mat(this.height(), this.width(), this.type())
    Imgproc.threshold(this, result, 0.0, 255.0, Imgproc.THRESH_BINARY or Imgproc.THRESH_OTSU)
    if (autoRelease) this.release()
    return result
}

fun Mat.gaussianThreshold(autoRelease: Boolean = true): Mat {
    val result = Mat(this.height(), this.width(), this.type())
    val blockSize = calcAdaptiveThresholdBlockSize(this.width(), this.height())
    Imgproc.adaptiveThreshold(this, result, 255.0, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, blockSize, 2.0)
    if (autoRelease) this.release()
    return result
}

private val WHITE = Scalar(255.0, 255.0, 255.0)

private fun calcAdaptiveThresholdBlockSize(width: Int, height: Int): Int {
    val min = width.coerceAtMost(height)
    return (min / 10).let {
        if (it % 2 == 0) it + 1
        else it
    }
}