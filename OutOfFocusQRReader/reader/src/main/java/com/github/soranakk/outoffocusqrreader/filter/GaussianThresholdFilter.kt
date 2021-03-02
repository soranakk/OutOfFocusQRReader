package com.github.soranakk.outoffocusqrreader.filter

import com.github.soranakk.outoffocusqrreader.extension.gaussianThreshold
import org.opencv.core.Mat

class GaussianThresholdFilter : ImageFilter {
    override fun filter(image: Mat): Mat = image.gaussianThreshold(false)
}
