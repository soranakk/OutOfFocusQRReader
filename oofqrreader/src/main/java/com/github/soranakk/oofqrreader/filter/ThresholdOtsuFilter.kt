package com.github.soranakk.oofqrreader.filter

import com.github.soranakk.oofqrreader.extension.thresholdOTSU
import org.opencv.core.Mat

class ThresholdOtsuFilter : ImageFilter {
    override fun filter(image: Mat): Mat = image.thresholdOTSU(false)
}
