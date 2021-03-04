package com.github.soranakk.oofqrreader.filter

import org.opencv.core.Mat

interface ImageFilter {
    fun filter(image: Mat): Mat
}
