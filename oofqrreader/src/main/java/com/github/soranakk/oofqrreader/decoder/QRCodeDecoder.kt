package com.github.soranakk.oofqrreader.decoder

import com.github.soranakk.oofqrreader.model.DecodeResult
import org.opencv.core.Mat

public interface QRCodeDecoder {
    public fun decode(grayImage: Mat): DecodeResult?
}