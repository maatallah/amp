package com.automusicplayer

import android.app.Application
import android.content.Context
import com.automusicplayer.media.FileLogger
import com.facebook.react.PackageList
import com.facebook.react.ReactApplication
import com.facebook.react.ReactHost
import com.facebook.react.ReactNativeHost
import com.facebook.react.ReactPackage
import com.facebook.react.defaults.DefaultReactHost.getDefaultReactHost
import com.facebook.react.defaults.DefaultReactNativeHost
import com.facebook.soloader.SoLoader

class MainApplication : Application(), ReactApplication {

    override val reactNativeHost: ReactNativeHost =
        object : DefaultReactNativeHost(this@MainApplication) {
            override fun getPackages(): List<ReactPackage> =
                PackageList(this).packages

            override fun getJSMainModuleName(): String = "index"

            override fun getUseDeveloperSupport(): Boolean = BuildConfig.DEBUG
        }

    override val reactHost: ReactHost
        get() = getDefaultReactHost(applicationContext, reactNativeHost)

    override fun attachBaseContext(base: Context?) {
        base?.let { 
            FileLogger.log(it, "========= APPLICATION ENGINE BOOT =========")
            FileLogger.log(it, "Attempting early native library loader binding...")
        }
        
        try {
            SoLoader.init(base, false)
            base?.let { FileLogger.log(it, "SoLoader bound successfully.") }
        } catch (t: Throwable) {
            base?.let { FileLogger.logThrowable(it, "CRITICAL LIFE CYCLE EXCEPTION: Early SoLoader binding aborted!", t) }
        }
        
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        FileLogger.log(this, "MainApplication onCreate() reached cleanly.")
    }
}
