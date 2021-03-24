package com.github.soranakk.oofqrreader.demoapp.ui.main

import android.content.Context
import android.util.Size
import android.view.Surface
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.soranakk.oofqrreader.decoder.mlkit.MLKitDecoder
import com.github.soranakk.oofqrreader.decoder.opencv.OpenCVDecoder
import com.github.soranakk.oofqrreader.decoder.zxing.ZxingDecoder
import com.github.soranakk.oofqrreader.demoapp.R
import com.github.soranakk.oofqrreader.model.DecodeResult
import com.github.soranakk.oofqrreader.model.ImageData
import com.github.soranakk.oofqrreader.reader.MultiFilterQRCodeReader
import com.github.soranakk.oofqrreader.reader.converter.androidx.CameraXApiImageConverter
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer
import org.opencv.core.Point
import timber.log.Timber
import java.util.concurrent.Executors

class MainViewModel : ViewModel() {

    companion object {
        private const val LOG_TAG = "QrReader"
    }

    private val cameraExecutor = Executors.newSingleThreadExecutor()

    private val imageConverter = CameraXApiImageConverter()
    private val oofQRReaderZxingDecorder = MultiFilterQRCodeReader(ZxingDecoder())
    private val oofQRReaderOpenCVDecorder = MultiFilterQRCodeReader(OpenCVDecoder())
    private val oofQRReaderMLKitDecorder = MultiFilterQRCodeReader(MLKitDecoder())
    private val zxingReader = MultiFormatReader().apply {
        val hints = hashMapOf(
                Pair(DecodeHintType.TRY_HARDER, false),
                Pair(DecodeHintType.POSSIBLE_FORMATS, listOf(BarcodeFormat.QR_CODE)))
        setHints(hints)
    }
    private val mlKitScanner = BarcodeScanning.getClient(
            BarcodeScannerOptions.Builder()
                    .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                    .build())

    override fun onCleared() {
        cameraExecutor.shutdown()
        super.onCleared()
    }

    val readerName = MutableLiveData<String>()
    val decodeResult = MutableLiveData<Pair<Size, DecodeResult?>>()

    fun startCamera(context: Context, previewView: PreviewView, lifecycleOwner: LifecycleOwner) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener(Runnable {
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            // Preview
            val preview = Preview.Builder()
                    .setTargetRotation(Surface.ROTATION_0)
                    .setTargetResolution(Size(1280, 720))
                    .build()
                    .also { it.setSurfaceProvider(previewView.surfaceProvider) }

            // Analysis
            val imageAnalysis = ImageAnalysis.Builder()
                    .setTargetRotation(Surface.ROTATION_0)
                    .setTargetResolution(Size(1280, 720))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(cameraExecutor, ImageAnalysis.Analyzer { image ->
                            analysis(context, image)
                            image.close()
                        })
                    }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageAnalysis)
            } catch (exc: Exception) {
                Timber.tag(LOG_TAG).e(exc, "Use case binding failed")
            }
        }, ContextCompat.getMainExecutor(context))
    }

    private fun analysis(context: Context, image: ImageProxy) {
        val imageData = imageConverter.convertImage(image)
        val start = System.currentTimeMillis()
        val r = context.resources
        val result = when (readerName.value) {
            r.getString(R.string.reader_oofreader_zxing) -> detectOofQRReaderZxingDecorder(imageData)
            r.getString(R.string.reader_oofreader_opencv) -> detectOofQRReaderOpenCVDecorder(imageData)
            r.getString(R.string.reader_oofreader_mlkit) -> detectOofQRReaderMLKitDecorder(imageData)
            r.getString(R.string.reader_only_zxing) -> detectZxingReader(imageData)
            r.getString(R.string.reader_only_mlkit) -> detectMLKitReader(imageData)
            else -> throw NotImplementedError("unknown reader name:${readerName.value}")
        }
        val end = System.currentTimeMillis()
        Timber.tag(LOG_TAG).d("time:${String.format("%.3f", (end - start) / 1000.0)} : ${result?.code}")

        decodeResult.postValue(Pair(Size(image.width, image.height), result?.copy(
                detectPoint = result.detectPoint.inverseY(image.height))))
    }

    private fun List<Point>.inverseY(height: Int) = this.map { p ->
        Point(p.x, height - p.y)
    }

    private fun detectOofQRReaderZxingDecorder(image: ImageData): DecodeResult? {
        return oofQRReaderZxingDecorder.detectAndRead(image)
    }

    private fun detectOofQRReaderOpenCVDecorder(image: ImageData): DecodeResult? {
        return oofQRReaderOpenCVDecorder.detectAndRead(image)
    }

    private fun detectOofQRReaderMLKitDecorder(image: ImageData): DecodeResult? {
        return oofQRReaderMLKitDecorder.detectAndRead(image)
    }

    private fun detectZxingReader(image: ImageData): DecodeResult? {
        try {
            val r = zxingReader.decodeWithState(BinaryBitmap(HybridBinarizer(PlanarYUVLuminanceSource(image.data,
                    image.width,
                    image.height,
                    0,
                    0,
                    image.width,
                    image.height,
                    false))))
            return DecodeResult(
                    code = r.text,
                    detectPoint = r.resultPoints.map { Point(it.x.toDouble(), it.y.toDouble()) }.toList()
            )
        } catch (e: Exception) {
            return null
        }
    }

    private fun detectMLKitReader(image: ImageData): DecodeResult? {
        val inputImage = InputImage.fromByteArray(
                image.data,
                image.width,
                image.height,
                0,
                InputImage.IMAGE_FORMAT_NV21
        )
        val result = mlKitScanner.process(inputImage)
        Tasks.await(result)
        return result.result.firstOrNull { it.rawValue != null && it.cornerPoints != null }
                ?.let {
                    DecodeResult(code = it.rawValue!!, detectPoint = it.cornerPoints!!.map { p -> Point(p.x.toDouble(), p.y.toDouble()) })
                }
    }
}
