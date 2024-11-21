package com.dicoding.example.storyapp

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingPolicies
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.example.storyapp.R
import com.example.storyapp.utils.EspressoIdlingResource
import com.example.storyapp.view.ui.login.LoginActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeUnit

class LoginLogoutTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    @Before
    fun setUp() {
        
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingPolicies.setMasterPolicyTimeout(30, TimeUnit.SECONDS)
    }

    @After
    fun tearDown() {
        
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun testLoginAndLogout() {
        val dummyEmail = "khairunsyah8935@gmail.com"
        val dummyPassword = "kemas8935"

        onView(withId(R.id.ed_login_email)).perform(typeText(dummyEmail), closeSoftKeyboard())
        onView(withId(R.id.ed_login_password)).perform(scrollTo(),clearText(),typeText(dummyPassword), closeSoftKeyboard())
        onView(withId(R.id.loginButton)).perform(click())
        onView(withText(R.string.success)).inRoot(isDialog()).check(matches(isDisplayed()))
        onView(withText(R.string.next)).perform(click())
        onView(withId(R.id.homeLayout)).check(matches(isDisplayed()))
        Thread.sleep(1000)
        onView(withId(R.id.action_logout)).perform(click())
        onView(withId(R.id.background)).check(matches(isDisplayed()))
    }
}