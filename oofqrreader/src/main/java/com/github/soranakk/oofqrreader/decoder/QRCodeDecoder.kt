package com.github.soranakk.oofqrreader.decoder

import org.opencv.core.Mat

interface QRCodeDecoder {
    fun decode(grayImage: Mat): String?
}