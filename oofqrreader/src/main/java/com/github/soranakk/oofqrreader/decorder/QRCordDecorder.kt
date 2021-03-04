package com.github.soranakk.oofqrreader.decorder

import android.graphics.Bitmap

interface QRCordDecorder {
    fun decord(bitmap: Bitmap): String?
}