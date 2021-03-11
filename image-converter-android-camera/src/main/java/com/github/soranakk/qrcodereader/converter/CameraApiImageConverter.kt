package com.github.soranakk.qrcodereader.converter

import com.github.soranakk.oofqrreader.model.ImageData

class CameraApiImageConverter {
    fun convertImage(data: ByteArray, width: Int, height: Int): ImageData {
        return ImageData(
                data = data.copyOf(width * height),
                format = ImageData.ImageFormat.GRAY,
                width = width,
                height = height)
    }
}