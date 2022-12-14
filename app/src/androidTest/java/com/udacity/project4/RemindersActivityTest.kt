package com.udacity.project4

import android.app.Activity
import android.app.Application
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.udacity.project4.locationreminders.RemindersActivity
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.util.DataBindingIdlingResource
import com.udacity.project4.util.monitorActivity
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get

@RunWith(AndroidJUnit4::class)
@LargeTest
//END TO END test to black box test the app
class RemindersActivityTest :
    AutoCloseKoinTest() {// Extended Koin Test - embed autoclose @after method to close Koin after every test

    private lateinit var repository: ReminderDataSource
    private lateinit var appContext: Application
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    /**
     * As we use Koin as a Service Locator Library to develop our code, we'll also use Koin to test our code.
     * at this step we will initialize Koin related code to be able to use it in out testing.
     */

    // provides reference to activity from activity scenario
    private fun getActivity(activityScenario: ActivityScenario<RemindersActivity>): Activity? {
        var activity: Activity? = null
        activityScenario.onActivity {
            activity = it
        }
        return activity
    }

    @Before
    fun init() {
        stopKoin()//stop the original app koin
        appContext = getApplicationContext()
        val myModule = module {
            viewModel {
                RemindersListViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single {
                SaveReminderViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single { RemindersLocalRepository(get()) as ReminderDataSource }
            single { LocalDB.createRemindersDao(appContext) }
        }
        //declare a new koin module
        startKoin {
            modules(listOf(myModule))
        }
        //Get our real repository
        repository = get()

        //clear the data to start fresh
        runBlocking {
            repository.deleteAllReminders()
        }
    }
    //Idling resources tell that the app is idle or busy
    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    //Unregister your idling resource so it can be garbage collected and does not leak any memory.
    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    @Test
    fun addingNewReminderFlow_givenValidForm_showsSuccessMessageAndNewReminderInReminderList() = runBlocking {
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        val activity = getActivity(activityScenario)!!

        onView(ViewMatchers.withId(R.id.addReminderFAB)).perform(click())
        onView(ViewMatchers.withId(R.id.reminderTitle))
            .perform(replaceText("Title todo"))
        onView(ViewMatchers.withId(R.id.reminderDescription))
            .perform(replaceText("Description todo"))
        onView(ViewMatchers.withId(R.id.selectLocation)).perform(click())
        onView(ViewMatchers.withId(R.id.map_selectlocation))
            .perform(longClick())
        onView(ViewMatchers.withId(R.id.button_saveLocation)).perform(click())
        onView(ViewMatchers.withId(R.id.button_saveReminder)).perform(click())

        onView(withText(R.string.reminder_saved))
            .inRoot(RootMatchers.withDecorView(CoreMatchers.not(CoreMatchers.`is`(activity.window.decorView))))
            .check(
                matches(
                    isDisplayed()
                )
            )
        onView(withText("Title todo")).check(matches(isDisplayed()))
        activityScenario.close()
    }

    @Test
    fun addingNewReminderFlow_givenInvalidForm_showsErrorMessage() = runBlocking {
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(ViewMatchers.withId(R.id.addReminderFAB)).perform(click())
        onView(ViewMatchers.withId(R.id.reminderTitle)).perform(replaceText("Title todo"))
        onView(ViewMatchers.withId(R.id.reminderDescription)).perform(replaceText("Description todo"))
        onView(ViewMatchers.withId(R.id.button_saveReminder)).perform(click())

        onView(
            CoreMatchers.allOf(
                ViewMatchers.withId(com.google.android.material.R.id.snackbar_text),
                withText(R.string.err_select_location)
            )
        ).check(matches(isDisplayed()))
        activityScenario.close()
    }

}
