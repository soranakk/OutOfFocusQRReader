package com.github.soranakk.oofqrreader.model

import org.opencv.core.Rect

public data class DetectionSettings(
        val includeFullScreen: Boolean = false,
        val isLimitedToSquaresOnly: Boolean = true,
        val minSizeRate: Double = 0.05,
        val maxSizeRate: Double = 0.95,
        val rectFilter: (rect: Rect) -> Boolean = { _ -> true }
)