package com.github.soranakk.outoffocusqrreader

import com.github.soranakk.model.ImageData
import org.opencv.core.Rect

interface QRCodeReader {
    fun readQRCord(image: ImageData): String?

    fun readQRCord(image: ImageData, rect: Rect): String?

    fun readQRCord(image: ImageData, rectList: Iterable<Rect>): String?
}
