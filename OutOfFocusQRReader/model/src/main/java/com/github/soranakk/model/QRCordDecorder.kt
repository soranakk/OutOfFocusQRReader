package com.github.soranakk.model

import android.graphics.Bitmap

interface QRCordDecorder {
    fun decord(bitmap: Bitmap): String?
}