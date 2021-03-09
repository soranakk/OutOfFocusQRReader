package com.github.soranakk.oofqrreader.model

data class ImageData(
        val data: ByteArray,
        val format: ImageFormat,
        val width: Int,
        val height: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ImageData

        if (!data.contentEquals(other.data)) return false
        if (width != other.width) return false
        if (height != other.height) return false

        return true
    }

    override fun hashCode(): Int {
        var result = data.contentHashCode()
        result = 31 * result + width
        result = 31 * result + height
        return result
    }

    enum class ImageFormat {
        YUV,
        ARGB_8888,
        GRAY
    }
}