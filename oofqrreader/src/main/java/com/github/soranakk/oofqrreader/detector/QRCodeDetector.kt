package com.github.soranakk.oofqrreader.detector

import com.github.soranakk.oofqrreader.extension.erode
import com.github.soranakk.oofqrreader.extension.findContours
import com.github.soranakk.oofqrreader.extension.thresholdOTSU
import com.github.soranakk.oofqrreader.model.DetectionSettings
import com.github.soranakk.oofqrreader.model.ImageData
import com.github.soranakk.oofqrreader.util.MatUtil
import org.opencv.core.MatOfPoint
import org.opencv.core.Rect
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

internal class QRCodeDetector(private val settings: DetectionSettings = DetectionSettings()) {

    private companion object {
        private const val MIN_WIDTH = 32
        private const val MIN_HEIGHT = 32
        private const val erodeSizeWidth = 5.0
        private const val erodeSizeHeight = 5.0
    }

    internal fun detectRectWhereQRExists(image: ImageData): List<Rect> {
        val imageSize = Size(image.width.toDouble(), image.height.toDouble())
        return MatUtil.convertImageDataToGray(image)
                .thresholdOTSU()
                .erode(Size(erodeSizeWidth, erodeSizeHeight))
                .findContours()
                .boundingRect()
                .filterNot { it.isInvalid(imageSize, settings.minSizeRate, settings.maxSizeRate, settings.isLimitedToSquaresOnly) }
                .filter { settings.rectFilter(it) }
                .let {
                    if (settings.includeFullScreen) {
                        it.toMutableList().apply { add(Rect(0, 0, image.width, image.height)) }
                    } else it
                }
                .sortedBy { it.width * it.height }
    }

    private fun List<MatOfPoint>.boundingRect(): Iterable<Rect> {
        return this.map { point -> Imgproc.boundingRect(point).also { point.release() } }
    }

    private fun Rect.isInvalid(imageSize: Size, minSizeRate: Double, maxSizeRate: Double, isLimitedToSquaresOnly: Boolean) =
            this.width < MIN_WIDTH ||
                    this.height < MIN_HEIGHT ||
                    this.width < imageSize.minWidth(minSizeRate) ||
                    this.height < imageSize.minHeight(minSizeRate) ||
                    this.width > imageSize.maxWidth(maxSizeRate) ||
                    this.height > imageSize.maxHeight(maxSizeRate) ||
                    (isLimitedToSquaresOnly && !this.isSquare())

    private fun Size.minWidth(minSizeRate: Double) = (this.width * minSizeRate).toInt()
    private fun Size.minHeight(minSizeRate: Double) = (this.height * minSizeRate).toInt()
    private fun Size.maxWidth(maxSizeRate: Double) = (this.width * maxSizeRate).toInt()
    private fun Size.maxHeight(maxSizeRate: Double) = (this.height * maxSizeRate).toInt()
    private fun Rect.isSquare() = (this.height / this.width.toDouble()).let { 0.75 < it && it < 1.25 }
}
