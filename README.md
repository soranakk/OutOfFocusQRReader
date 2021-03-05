# OutOfFocusQRReader

OutOfFocusQRReader is a library for reading QR codes from out-of-focus images on Android.

This library uses the OpenCV library.

# Install

## Download OpenCV

Download the latest version of the OpenCV Android library from the official OpenCV web page.

https://opencv.org/releases/

## Extract zip and move SDK folder

Extract the files the downloaded zip file and move the SDK folder to your project root folder.

## Install OpenCV SDK

Edit the `settings.gradle` file in your project and sync your project:

```gradle
// add
include ':sdk'
```

## Install OutOfFocusQRReader

Edit the `build.gradle` file in your project:

```gradle
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

# Usage

## Create ImageData

```kotlin
val image = ImageData(image_byte_array, format, width, height)
```

## Detect the rect where QR exists

```kotlin
val detector = QRCodeDetector()
val rectList = detector.detectRectWhereQRExists(image)
```

## Create QRCordDecorder

Example using [ZXing](https://github.com/zxing/zxing):

```kotlin
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
