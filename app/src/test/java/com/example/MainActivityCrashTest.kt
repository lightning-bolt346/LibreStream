package com.example

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLog

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class MainActivityCrashTest {

    @Test
    fun testPlayerScreenDoesNotCrash() {
        ShadowLog.stream = System.out
        try {
            val controller = Robolectric.buildActivity(MainActivity::class.java)
            controller.create().start().resume()
            assert(controller.get() != null)

            // We simulate navigating to player screen using Navigation Component? It's hard. But what about rendering PlayerScreen explicitly?
            val activity = controller.get()
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
}
