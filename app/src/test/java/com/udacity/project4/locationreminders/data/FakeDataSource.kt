package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result


//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource : ReminderDataSource {

    //Create a list to store reminders for the data source
    var reminders = mutableListOf<ReminderDTO>()

    //return reminders from the list
    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        reminders.let { return Result.Success(it) }
        return Result.Success(ArrayList(reminders))
    }

    //add reminder to fake data source
    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders?.add(reminder)
    }

    //get a reminder by id
    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        reminders.firstOrNull { it.id == id }?.let { return Result.Success(it) }
        return Result.Error("Reminder not found")
    }

    override suspend fun deleteAllReminders() {

    }

}