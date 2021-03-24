package com.github.soranakk.oofqrreader.reader

import com.github.soranakk.oofqrreader.model.DecodeResult
import com.github.soranakk.oofqrreader.model.ImageData
import org.opencv.core.Rect

public interface QRCodeReader {
    public fun readQRCode(image: ImageData): DecodeResult?

    public fun readQRCode(image: ImageData, rect: Rect): DecodeResult?

    public fun readQRCode(image: ImageData, rectList: Iterable<Rect>): DecodeResult?
}
