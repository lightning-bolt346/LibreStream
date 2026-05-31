package com.example

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class MainActivityRobolectricTest {

    @Test
    fun testActivityLaunch() {
        try {
            val controller = Robolectric.buildActivity(MainActivity::class.java).create().start().resume()
            assert(controller.get() != null)
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
}
