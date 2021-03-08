package com.github.soranakk.oofqrreader.reader

import com.github.soranakk.oofqrreader.model.ImageData
import org.opencv.core.Rect

interface QRCodeReader {
    fun readQRCode(image: ImageData): String?

    fun readQRCode(image: ImageData, rect: Rect): String?

    fun readQRCode(image: ImageData, rectList: Iterable<Rect>): String?
}
