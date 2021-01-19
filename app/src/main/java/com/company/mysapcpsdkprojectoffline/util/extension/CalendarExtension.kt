package com.company.mysapcpsdkprojectoffline.util.extension

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.fragment.app.FragmentManager
import java.util.*


fun openCalendar(context: Context, startDate: Date?, dateCallback: (Calendar) -> Unit, theme: Int) {
    val cal = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        theme,
        { _, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            dateCallback(cal)
        },
        // set DatePickerDialog to point to today's date when it loads up
        cal.get(Calendar.YEAR),
        cal.get(Calendar.MONTH),
        cal.get(Calendar.DAY_OF_MONTH)
    )

    if (startDate != null) {
        datePickerDialog.datePicker.minDate = startDate.time
    }

    datePickerDialog.show()
}

fun openTimePicker(
    context: Context,
    timeCallback: (Int, Int) -> Unit,
    theme: Int
) {
    val mCurrentTime = Calendar.getInstance()

    val hour = mCurrentTime[Calendar.HOUR_OF_DAY]
    val minute = mCurrentTime[Calendar.MINUTE]
    val mTimePicker: TimePickerDialog
    mTimePicker = TimePickerDialog(
        context,
        theme,
        { _, selectedHour, selectedMinute ->
            timeCallback(selectedHour, selectedMinute)
        },
        hour,
        minute,
        false
    ) //Yes 24 hour time

    mTimePicker.show()

}

fun openCalendarPast(context: Context, endDate: Date, dateCallback: (Calendar) -> Unit) {
    val cal = Calendar.getInstance()
    val dateSetListener =
        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            dateCallback(cal)
        }

    val datePicker = DatePickerDialog(
        context,
        dateSetListener,
        // set DatePickerDialog to point to today's date when it loads up
        cal.get(Calendar.YEAR),
        cal.get(Calendar.MONTH),
        cal.get(Calendar.DAY_OF_MONTH)
    )
    datePicker.datePicker.maxDate = endDate.time
    datePicker.show()

}

fun openCalendarFuture(context: Context, startDate: Date, dateCallback: (Calendar) -> Unit) {
    val cal = Calendar.getInstance()
    val dateSetListener =
        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            dateCallback(cal)
        }

    val datePicker = DatePickerDialog(
        context,
        dateSetListener,
        // set DatePickerDialog to point to today's date when it loads up
        cal.get(Calendar.YEAR),
        cal.get(Calendar.MONTH),
        cal.get(Calendar.DAY_OF_MONTH)
    )
    datePicker.datePicker.minDate = startDate.time
    datePicker.show()

}

fun openDateTimePicker(
    context: Context,
    startDate: Date?,
    dateTimeCallback: (Calendar) -> Unit
) {
    val cal = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context, { view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val mTimePicker: TimePickerDialog
            mTimePicker = TimePickerDialog(
                context, { timePicker, selectedHour, selectedMinute ->

                    cal.set(Calendar.HOUR_OF_DAY, selectedHour)
                    cal.set(Calendar.MINUTE, selectedMinute)
                    dateTimeCallback(cal)
                },
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                false
            ) //Yes 24 hour time

            mTimePicker.show()
        },
        // set DatePickerDialog to point to today's date when it loads up
        cal.get(Calendar.YEAR),
        cal.get(Calendar.MONTH),
        cal.get(Calendar.DAY_OF_MONTH)
    )

    if (startDate != null) {
        datePickerDialog.datePicker.minDate = startDate.time
    }

    datePickerDialog.show()
}

