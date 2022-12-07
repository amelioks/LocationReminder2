package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.local.toReminderDTO
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.*
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var saveReminderViewModel: SaveReminderViewModel
    private lateinit var fakeReminderDataSource: FakeDataSource

    @Before
    fun setupSaveReminderViewModel() {
        fakeReminderDataSource = FakeDataSource()
        saveReminderViewModel = SaveReminderViewModel(
            ApplicationProvider.getApplicationContext(),
            fakeReminderDataSource
        )
    }

    private fun createFakeReminderDataItem(): ReminderDataItem {
        return ReminderDataItem(
            "title todo",
            "description todo",
            "location todo",
            100.00,
            50.00
        )
    }

    private fun createFakeErrorReminderDataItem(): ReminderDataItem {
        return ReminderDataItem(
            "title todo",
            "description todo",
            "",
            100.00,
            50.00
        )
    }

    @Test
    fun addNewReminder_checkSaveReminderToDataSource() =
        runBlockingTest {
            val item = createFakeReminderDataItem()
            saveReminderViewModel.saveReminder(item)
            assert(fakeReminderDataSource.reminders.contains(item.toReminderDTO()))
            Assert.assertEquals(false, saveReminderViewModel.showLoading.getOrAwaitValue())
        }

    @Test
    fun addNewIncompleteReminder_checkSaveReminderToDataSource_ReturnError() =
        runBlockingTest {
            val item = createFakeErrorReminderDataItem()
            saveReminderViewModel.validateEnteredData(item)
            Assert.assertFalse(fakeReminderDataSource.reminders.contains(item.toReminderDTO()))
            Assert.assertEquals(R.string.err_select_location, saveReminderViewModel.showSnackBarInt.getOrAwaitValue())

        }
}