package com.github.soranakk.oofqrreader.model

import org.opencv.core.Point

public data class DecodeResult(val code: String, val detectPoint: List<Point>)