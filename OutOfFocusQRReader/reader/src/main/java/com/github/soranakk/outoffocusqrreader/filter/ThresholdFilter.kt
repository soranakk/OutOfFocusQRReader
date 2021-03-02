package com.github.soranakk.outoffocusqrreader.filter

import com.github.soranakk.outoffocusqrreader.extension.threshold
import org.opencv.core.Mat

open class ThresholdFilter(private val thresh: Double) : ImageFilter {
    override fun filter(image: Mat): Mat = image.threshold(thresh, false)
}