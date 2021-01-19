package com.company.mysapcpsdkprojectoffline.util.extension

import java.text.SimpleDateFormat
import java.util.*

/**
 * Pattern: yyyy-MM-dd HH:mm:ss
 */
fun Date.formatToServerDateTimeDefaults(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return sdf.format(this)
}

fun Date.formatToTruncatedDateTime(): String {
    val sdf = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
    return sdf.format(this)
}

/**
 * Pattern: yyyy-MM-dd
 */
fun Date.formatToServerDateDefaults(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return sdf.format(this)
}



/**
 * Pattern: HH:mm:ss
 */
fun Date.formatToServerTimeDefaults(): String {
    val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return sdf.format(this)
}

/**
 * Pattern: dd/MM/yyyy HH:mm:ss
 */
fun Date.formatToViewDateTimeDefaults(): String {
    val sdf = SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.getDefault())
    return sdf.format(this)
}

/**
* Pattern: dd/MM/yyyy, HH:mm:ss
*/
fun Date.formatToEventDateTimeDefaults(): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    return sdf.format(this)
}

/**
 * Pattern: dd
 */
fun Date.formatToViewOnlyDateDefaults(): String {
    val sdf = SimpleDateFormat("dd", Locale.getDefault())
    return sdf.format(this)
}

/**
 * Pattern: MMM,yyyy
 */
fun Date.formatToViewMonthAndYearDefaults(): String {
    val sdf = SimpleDateFormat("MMM,yyyy", Locale.getDefault())
    return sdf.format(this)
}

/**
 * Pattern: MMM
 */
fun Date.formatToViewMonthDateDefaults(): String {
    val sdf = SimpleDateFormat("MMM dd", Locale.getDefault())
    return sdf.format(this)
}



/**
 * Pattern: dd/MM/yyyy
 */
fun Date.formatToViewDateDefaults(): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.format(this)
}

/**
 * Pattern: HH:mm:ss
 */
fun Date.formatToViewTimeDefaults(): String {
    val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return sdf.format(this)
}

/**
 * Pattern: HH:mm:ss
 */
fun Date.formatCreatedDateDefaults(): String {
    val sdf = SimpleDateFormat("dd MMM,yyyy", Locale.getDefault())
    return sdf.format(this)
}

/**
 * Pattern: d[st,nd,rd,th] MMMM YYYY
 */
fun Date.formatToTripDateDefaults(dayOfMonth: Int): String {
    val sdf = if (dayOfMonth in 11..13) {
        SimpleDateFormat("d'th' MMMM YYYY", Locale.getDefault())
    } else when (dayOfMonth % 10) {
        1 -> SimpleDateFormat("d'st' MMMM YYYY", Locale.getDefault())
        2 -> SimpleDateFormat("d'nd' MMMM YYYY", Locale.getDefault())
        3 -> SimpleDateFormat("d'rd' MMMM YYYY", Locale.getDefault())
        else -> SimpleDateFormat("d'th' MMMM YYYY", Locale.getDefault())
    }
    return sdf.format(this)
}

fun Date.convertTimeToUTC(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    dateFormat.timeZone = TimeZone.getTimeZone("UTC")
    return dateFormat.format(this)
}

/**
 * Pattern: hh:mm a
 */
fun Date.formatToTripTimeDefaults(): String {
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return sdf.format(this)
}

/**
 * Add field date to current date
 */
fun Date.add(field: Int, amount: Int): Date {
    val cal = Calendar.getInstance()
    cal.time = this
    cal.add(field, amount)

    this.time = cal.time.time

    cal.clear()

    return this
}

fun Date.addYears(years: Int): Date {
    return add(Calendar.YEAR, years)
}

fun Date.addMonths(months: Int): Date {
    return add(Calendar.MONTH, months)
}

fun Date.addDays(days: Int): Date {
    return add(Calendar.DAY_OF_MONTH, days)
}

fun Date.addHours(hours: Int): Date {
    return add(Calendar.HOUR_OF_DAY, hours)
}

fun Date.addMinutes(minutes: Int): Date {
    return add(Calendar.MINUTE, minutes)
}

fun Date.addSeconds(seconds: Int): Date {
    return add(Calendar.SECOND, seconds)
}


