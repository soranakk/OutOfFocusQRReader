package com.github.soranakk.oofqrreader.demoapp

import androidx.multidex.MultiDexApplication
import timber.log.Timber
import timber.log.Timber.DebugTree

class App : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        System.loadLibrary("opencv_java4")
        Timber.plant(DebugTree())
    }
}