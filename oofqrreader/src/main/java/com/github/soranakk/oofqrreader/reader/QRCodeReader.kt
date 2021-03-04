package com.github.soranakk.oofqrreader.reader

import com.github.soranakk.oofqrreader.model.ImageData
import org.opencv.core.Rect

interface QRCodeReader {
    fun readQRCord(image: ImageData): String?

    fun readQRCord(image: ImageData, rect: Rect): String?

    fun readQRCord(image: ImageData, rectList: Iterable<Rect>): String?
}
