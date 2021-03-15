package com.github.soranakk.oofqrreader.reader

import com.github.soranakk.oofqrreader.model.ImageData
import org.opencv.core.Rect

public interface QRCodeReader {
    public fun readQRCode(image: ImageData): String?

    public fun readQRCode(image: ImageData, rect: Rect): String?

    public fun readQRCode(image: ImageData, rectList: Iterable<Rect>): String?
}
