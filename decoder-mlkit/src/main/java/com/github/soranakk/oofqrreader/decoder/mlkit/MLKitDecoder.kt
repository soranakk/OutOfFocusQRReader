package com.github.soranakk.oofqrreader.decoder.mlkit

import com.github.soranakk.oofqrreader.decoder.QRCodeDecoder
import com.github.soranakk.oofqrreader.model.DecodeResult
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import org.opencv.core.Mat
import org.opencv.core.Point

public class MLKitDecoder : QRCodeDecoder {

    private val mlKitScanner = BarcodeScanning.getClient(
            BarcodeScannerOptions.Builder()
                    .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                    .build())

    override fun decode(grayImage: Mat): DecodeResult? {
        return try {
            val inputImage = grayImage.convertInputImage()
            val result = mlKitScanner.process(inputImage)
            Tasks.await(result)
            result.result.firstOrNull { it.rawValue != null && it.cornerPoints != null }
                    ?.let {
                        DecodeResult(code = it.rawValue!!, detectPoint = it.cornerPoints!!.map { p -> Point(p.x.toDouble(), p.y.toDouble()) })
                    }
        } catch (e: Exception) {
            null
        }
    }

    private fun Mat.convertInputImage(): InputImage {
        val grayData = ByteArray((this.total() * this.channels()).toInt())
        this.get(0, 0, grayData)

        return InputImage.fromByteArray(
                grayData,
                this.width(),
                this.height(),
                0,
                InputImage.IMAGE_FORMAT_NV21
        ).also { this.release() }
    }
}