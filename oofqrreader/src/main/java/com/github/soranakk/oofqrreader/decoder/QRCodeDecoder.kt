package com.github.soranakk.oofqrreader.decoder

import android.graphics.Bitmap

interface QRCodeDecoder {
    fun decode(bitmap: Bitmap): String?
}