package com.github.soranakk.oofqrreader.reader

import com.github.soranakk.oofqrreader.decoder.QRCodeDecoder
import com.github.soranakk.oofqrreader.filter.GaussianThresholdFilter
import com.github.soranakk.oofqrreader.filter.ImageFilter
import com.github.soranakk.oofqrreader.filter.OverexposureFilter
import com.github.soranakk.oofqrreader.filter.StrongOverexposureFilter
import com.github.soranakk.oofqrreader.filter.ThresholdOtsuFilter
import com.github.soranakk.oofqrreader.model.ImageData
import com.github.soranakk.oofqrreader.util.MatUtil
import org.opencv.core.Mat
import org.opencv.core.Rect

class MultiFilterQRCodeReader(
    private val decorder: QRCodeDecoder,
    filters: List<ImageFilter> = listOf(),
    readerSettings: ReaderSettings = ReaderSettings()) : QRCodeReader {

    data class ReaderSettings(
            val useDefaultFilter: Boolean = true
    )

    private class NonFilter : ImageFilter {
        override fun filter(image: Mat): Mat = Mat().apply { image.copyTo(this) }
    }

    private val filters = mutableListOf<ImageFilter>()
            .apply { this.addAll(filters) }
            .apply {
                if (readerSettings.useDefaultFilter) this.addAll(listOf(
                        NonFilter(),
                        GaussianThresholdFilter(),
                        StrongOverexposureFilter(),
                        OverexposureFilter(),
                        ThresholdOtsuFilter()))
            }

    override fun readQRCord(image: ImageData) = readQRCord(image, Rect(0, 0, image.width, image.height))

    override fun readQRCord(image: ImageData, rect: Rect) = readQRCord(image, listOf(rect))

    override fun readQRCord(image: ImageData, rectList: Iterable<Rect>): String? {
        return rectList.asSequence()
                .map { rect -> read(image, rect) }
                .find { !it.isNullOrEmpty() }
    }

    private fun read(image: ImageData, rect: Rect): String? {
        val targetImage = MatUtil.convertYuvToGray(image).clipRect(rect)
        return filters.asSequence()
                .map { filter -> filter.filter(targetImage) }
                .map { filteredImage -> filteredImage.convertGray2Bitmap() }
                .map { filteredBitmap -> decorder.decode(filteredBitmap) }
                .find { !it.isNullOrEmpty() }
                .also { targetImage.release() }
    }
}
