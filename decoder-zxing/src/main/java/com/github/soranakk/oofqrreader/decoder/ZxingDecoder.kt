package com.github.soranakk.oofqrreader.decoder

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.LuminanceSource
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.GlobalHistogramBinarizer
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc

class ZxingDecoder : QRCodeDecoder {

    private val qrCodeReader = MultiFormatReader().apply {
        val hints = hashMapOf(
                Pair(DecodeHintType.TRY_HARDER, false),
                Pair(DecodeHintType.POSSIBLE_FORMATS, listOf(BarcodeFormat.QR_CODE)))
        setHints(hints)
    }

    override fun decode(grayImage: Mat): String? {
        val bitmap = grayImage.convertGray2Bitmap()
        val binaryBitmap = BinaryBitmap(GlobalHistogramBinarizer(bitmap.convertLuminanceSource()))
        return try {
            qrCodeReader.decodeWithState(binaryBitmap).text
        } catch (e: Exception) {
            null
        }
    }

    private fun Bitmap.convertLuminanceSource(): LuminanceSource {
        val pixels = IntArray(width * height)
        getPixels(pixels, 0, width, 0, 0, width, height)
        return RGBLuminanceSource(width, height, pixels).also { this.recycle() }
    }

    private fun Mat.convertGray2Bitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(this.width(), this.height(), Bitmap.Config.ARGB_8888)
        val rgba = Mat()
        Imgproc.cvtColor(this, rgba, Imgproc.COLOR_GRAY2RGBA)
        Utils.matToBitmap(rgba, bitmap)
        rgba.release()
        this.release()
        return bitmap
    }
}