package com.github.soranakk.oofqrreader.decoder.zxing

import com.github.soranakk.oofqrreader.decoder.QRCodeDecoder
import com.github.soranakk.oofqrreader.model.DecodeResult
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.LuminanceSource
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.ResultPoint
import com.google.zxing.common.GlobalHistogramBinarizer
import org.opencv.core.Mat
import org.opencv.core.Point

public class ZxingDecoder : QRCodeDecoder {

    private val qrCodeReader = MultiFormatReader().apply {
        val hints = hashMapOf(
                Pair(DecodeHintType.TRY_HARDER, false),
                Pair(DecodeHintType.POSSIBLE_FORMATS, listOf(BarcodeFormat.QR_CODE)))
        setHints(hints)
    }

    override fun decode(grayImage: Mat): DecodeResult? {
        val binaryBitmap = BinaryBitmap(GlobalHistogramBinarizer(grayImage.convertLuminanceSource()))
        return try {
            val result = qrCodeReader.decodeWithState(binaryBitmap)
            DecodeResult(code = result.text,
                    detectPoint = result.resultPoints.toPointList())
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

    private fun Array<out ResultPoint>.toPointList(): List<Point> =
            this.map { Point(it.x.toDouble(), it.y.toDouble()) }.toList()
}