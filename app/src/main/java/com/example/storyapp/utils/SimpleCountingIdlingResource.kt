package com.example.storyapp.utils

import androidx.test.espresso.IdlingResource

class SimpleCountingIdlingResource(private val resourceName: String) : IdlingResource {
    @Volatile
    private var counter = 0
    private var resourceCallback: IdlingResource.ResourceCallback? = null

    override fun getName() = resourceName

    override fun isIdleNow() = counter == 0

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback) {
        resourceCallback = callback
    }

    @Synchronized
    fun increment() {
        counter++
    }

    @Synchronized
    fun decrement() {
        if (counter > 0) {
            counter--
            if (counter == 0) {
                resourceCallback?.onTransitionToIdle()
            }
        }
    }
}