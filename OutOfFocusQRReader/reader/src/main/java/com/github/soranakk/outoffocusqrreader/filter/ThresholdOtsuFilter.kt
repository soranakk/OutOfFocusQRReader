package com.github.soranakk.outoffocusqrreader.filter

import com.github.soranakk.outoffocusqrreader.extension.thresholdOTSU
import org.opencv.core.Mat

class ThresholdOtsuFilter : ImageFilter {
    override fun filter(image: Mat): Mat = image.thresholdOTSU(false)
}
