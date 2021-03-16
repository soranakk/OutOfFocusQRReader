package com.github.soranakk.oofqrreader.decoder.zxing

import com.github.soranakk.oofqrreader.decoder.QRCodeDecoder
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.LuminanceSource
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.GlobalHistogramBinarizer
import org.opencv.core.Mat

public class ZxingDecoder : QRCodeDecoder {

    private val qrCodeReader = MultiFormatReader().apply {
        val hints = hashMapOf(
                Pair(DecodeHintType.TRY_HARDER, false),
                Pair(DecodeHintType.POSSIBLE_FORMATS, listOf(BarcodeFormat.QR_CODE)))
        setHints(hints)
    }

    override fun decode(grayImage: Mat): String? {
        val binaryBitmap = BinaryBitmap(GlobalHistogramBinarizer(grayImage.convertLuminanceSource()))
        return try {
            qrCodeReader.decodeWithState(binaryBitmap).text
        } catch (e: Exception) {
            null
        }
    }

    private fun Mat.convertLuminanceSource(): LuminanceSource {
        val grayData = ByteArray((this.total() * this.channels()).toInt())
        this.get(0, 0, grayData)
        return PlanarYUVLuminanceSource(grayData,
                this.width(),
                this.height(),
                0,
                0,
                this.width(),
                this.height(),
                false).also { this.release() }
    }
}