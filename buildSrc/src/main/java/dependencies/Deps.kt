package dependencies


@Suppress("unused")
internal object Deps {

    internal const val androidGradlePlugin = "com.android.tools.build:gradle:${Versions.androidGradlePlugin}"

    internal object Kotlin {
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}"
        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    }

    internal object AndroidX {
        const val coreKtx = "androidx.core:core-ktx:${Versions.AndroidX.core_ktx}"
        const val cameraXCore = "androidx.camera:camera-core:${Versions.AndroidX.cameraX}"
    }

    internal const val zxing = "com.google.zxing:core:${Versions.zxing}"
    internal const val mlkit = "com.google.mlkit:barcode-scanning:${Versions.mlkit}"
}