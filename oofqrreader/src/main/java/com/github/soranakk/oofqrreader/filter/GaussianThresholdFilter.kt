package com.github.soranakk.oofqrreader.filter

import com.github.soranakk.oofqrreader.extension.gaussianThreshold
import org.opencv.core.Mat

public class GaussianThresholdFilter : ImageFilter {
    override fun filter(image: Mat): Mat = image.gaussianThreshold(false)
}
