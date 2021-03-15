package com.github.soranakk.oofqrreader.decoder

import org.opencv.core.Mat

public interface QRCodeDecoder {
    public fun decode(grayImage: Mat): String?
}