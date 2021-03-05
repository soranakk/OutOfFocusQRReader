# OutOfFocusQRReader

OutOfFocusQRReader is a library for reading QR codes from out-of-focus images on Android.

This library use OpenCV library.

# Install

## Download OpenCV

Go to the official OpenCV web page and download the latest OpenCV Android library.

https://opencv.org/releases/

## Extract zip and move sdk folder

Extract the downloaded zip and move sdk folder to your project root folder.

## Install OpenCV sdk

Edit settings.gradle and sync gradle.

```
// add
include ':sdk'
```

## Install OutOfFocusQRReader

Edit build.gradle of your app.

```
repositories {
    mavenCentral()
}

dependencies {
    // OpenCV sdk
    implementation project(':sdk')

    // core
    implementation 'com.github.soranakk:oofqrreader:0.0.1'
}
```

# How to use

## Create ImageData

```
val image = ImageData(image_byte_array, format, width, height)
```

## Detect rect where QR exists

```
val detector = QRCodeDetector()
val rectList = detector.detectRectWhereQRExists(image)
```

## Create QRCordDecorder

for example use zxing.
```
class ZxingDecorder : QRCordDecorder {
    private val qrCodeReader = MultiFormatReader().apply {
        val hints = hashMapOf(
                Pair(DecodeHintType.TRY_HARDER, false),
                Pair(DecodeHintType.POSSIBLE_FORMATS, listOf(BarcodeFormat.QR_CODE)))
        setHints(hints)
    }

    override fun decord(bitmap: Bitmap): String? {
        val binaryBitmap = BinaryBitmap(GlobalHistogramBinarizer(convertLuminanceSource(bitmap)))
        return try {
            qrCodeReader.decodeWithState(binaryBitmap).text
        } catch (e: Exception) {
            null
        }
    }

    private fun convertLuminanceSource(bitmap: Bitmap): LuminanceSource {
        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        return RGBLuminanceSource(bitmap.width, bitmap.height, pixels)
    }
}
val zxingDecorder = ZxingDecorder()
```

## Read QR code

```
val qrReader = MultiFilterQRCodeReader(zxingDecorder)
val result = qrReader.readQRCord(image, rectList)
```
