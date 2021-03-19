package com.github.soranakk.oofqrreader.decoder.opencv

import com.github.soranakk.oofqrreader.decoder.QRCodeDecoder
import com.github.soranakk.oofqrreader.model.DecodeResult
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.objdetect.QRCodeDetector

public class OpenCVDecoder : QRCodeDecoder {

    private val qrCodeReader = QRCodeDetector()

    override fun decode(grayImage: Mat): DecodeResult? {
        val point = Mat()
        val result = qrCodeReader.detectAndDecode(grayImage, point)
        grayImage.release()
        return if (result.isNullOrEmpty()) null else DecodeResult(code = result, detectPoint = point.toPointList())
    }

    private fun Mat.toPointList(): List<Point> {
        val list = mutableListOf<Point>()
        for (i in 0 until this.rows()) {
            for (j in 0 until this.cols()) {
                val p = this.get(i, j)
                list.add(Point(p[0], p[1]))
            }
        }
        this.release()
        return list
    }
}