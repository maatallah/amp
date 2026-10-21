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

    companion object {
        init {
            try {
                // Initialize at JVM class-load time before any native layouts or activities bind
                SoLoader.init(null as Context?, false)
            } catch (t: Throwable) {
                // Fail silently so secondary initialization cannot happen
            }
        }
    }

    override val reactNativeHost: ReactNativeHost =
        object : DefaultReactNativeHost(this@MainApplication) {
            override fun getPackages(): List<ReactPackage> =
                PackageList(this).packages

            override fun getJSMainModuleName(): String = "index"

            override fun getUseDeveloperSupport(): Boolean = BuildConfig.DEBUG
        }

    override val reactHost: ReactHost
        get() = getDefaultReactHost(applicationContext, reactNativeHost)

    override fun onCreate() {
        super.onCreate()
        FileLogger.log(this, "MainApplication container initialized cleanly.")
    }
}
