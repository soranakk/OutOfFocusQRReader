package com.github.soranakk.oofqrreader.reader

import com.github.soranakk.oofqrreader.decoder.QRCodeDecoder
import com.github.soranakk.oofqrreader.detector.QRCodeDetector
import com.github.soranakk.oofqrreader.extension.clipRect
import com.github.soranakk.oofqrreader.filter.GaussianThresholdFilter
import com.github.soranakk.oofqrreader.filter.ImageFilter
import com.github.soranakk.oofqrreader.filter.OverexposureFilter
import com.github.soranakk.oofqrreader.filter.StrongOverexposureFilter
import com.github.soranakk.oofqrreader.filter.ThresholdOtsuFilter
import com.github.soranakk.oofqrreader.model.DecodeResult
import com.github.soranakk.oofqrreader.model.DetectionSettings
import com.github.soranakk.oofqrreader.model.ImageData
import com.github.soranakk.oofqrreader.model.ReaderSettings
import com.github.soranakk.oofqrreader.util.MatUtil
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Rect

public class MultiFilterQRCodeReader(
        private val decoder: QRCodeDecoder,
        detectionSettings: DetectionSettings = DetectionSettings(),
        filters: List<ImageFilter> = listOf(),
        readerSettings: ReaderSettings = ReaderSettings()) : QRCodeReader {

    private val detector = QRCodeDetector(detectionSettings)

    private class NonFilter : ImageFilter {
        override fun filter(image: Mat): Mat = Mat().apply { image.copyTo(this) }
    }

    private val filters = mutableListOf<ImageFilter>()
            .apply { this.addAll(filters) }
            .apply {
                if (readerSettings.useNonFilter) this.add(NonFilter())
                if (readerSettings.useAllFilter) this.addAll(listOf(
                        GaussianThresholdFilter(),
                        StrongOverexposureFilter(),
                        OverexposureFilter(),
                        ThresholdOtsuFilter()))
            }

    public fun detectAndRead(image: ImageData): DecodeResult? {
        val rectList = detector.detectRectWhereQRExists(image)
        return readQRCode(image, rectList)
    }

    override fun readQRCode(image: ImageData): DecodeResult? = readQRCode(image, Rect(0, 0, image.width, image.height))

    override fun readQRCode(image: ImageData, rect: Rect): DecodeResult? = readQRCode(image, listOf(rect))

    override fun readQRCode(image: ImageData, rectList: Iterable<Rect>): DecodeResult? {
        return rectList.asSequence()
                .map { rect -> read(image, rect) }
                .find { it != null }
    }

    private fun read(image: ImageData, rect: Rect): DecodeResult? {
        val targetImage = MatUtil.convertImageDataToGray(image).clipRect(rect)
        return filters.asSequence()
                .map { filter -> filter.filter(targetImage) }
                .map { filteredImage -> decoder.decode(filteredImage) }
                .map { it?.copy(detectPoint = it.detectPoint.map { p -> Point(p.x + rect.x, p.y + rect.y) }) }
                .find { it != null }
                .also { targetImage.release() }
    }
}
