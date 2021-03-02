package com.github.soranakk.outoffocusqrreader.filter

import org.opencv.core.Mat

interface ImageFilter {
    fun filter(image: Mat): Mat
}
