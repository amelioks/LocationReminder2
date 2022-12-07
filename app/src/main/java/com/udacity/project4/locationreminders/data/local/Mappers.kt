package com.udacity.project4.locationreminders.data.local

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

fun ReminderDataItem.toReminderDTO(): ReminderDTO {
    return ReminderDTO(
        title = title,
        description = description,
        location = location,
        latitude = latitude,
        longitude = longitude,
        id = id
    )
}