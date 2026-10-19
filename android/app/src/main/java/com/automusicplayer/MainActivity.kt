package com.automusicplayer

import com.facebook.react.ReactActivity
import com.facebook.react.ReactActivityDelegate
import com.facebook.react.defaults.DefaultReactActivityDelegate

class MainActivity : ReactActivity() {

    /**
     * Returns the name of the main component registered from JavaScript.
     * This is used to schedule rendering of the component.
     */
    override fun getMainComponentName(): String = "AutoMusicPlayer"

    /**
     * Returns the instance of the [ReactActivityDelegate].
     * Hardcoding fabricEnabled to false ensures absolute runtime safety on 32-bit processors.
     */
    override fun createReactActivityDelegate(): ReactActivityDelegate {
        return DefaultReactActivityDelegate(
            this,
            mainComponentName,
            false // Forced to false to completely bypass Bridgeless/CxxInterop crashes
        )
    }
}
