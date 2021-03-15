package com.github.soranakk.oofqrreader.filter

import org.opencv.core.Mat

public interface ImageFilter {
    public fun filter(image: Mat): Mat
}
