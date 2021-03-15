package com.github.soranakk.oofqrreader.filter

import com.github.soranakk.oofqrreader.extension.threshold
import org.opencv.core.Mat

public open class ThresholdFilter(private val thresh: Double) : ImageFilter {
    override fun filter(image: Mat): Mat = image.threshold(thresh, false)
}