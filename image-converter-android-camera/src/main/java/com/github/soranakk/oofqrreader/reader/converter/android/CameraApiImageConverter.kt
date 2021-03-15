package com.github.soranakk.oofqrreader.reader.converter.android

import com.github.soranakk.oofqrreader.model.ImageData

public class CameraApiImageConverter {
    public fun convertImage(data: ByteArray, width: Int, height: Int): ImageData {
        return ImageData(
                data = data.copyOf(width * height),
                format = ImageData.ImageFormat.GRAY,
                width = width,
                height = height)
    }
}