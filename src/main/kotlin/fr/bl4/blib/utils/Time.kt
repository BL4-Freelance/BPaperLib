package fr.bl4.blib.utils

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.TimeZone

fun Long.toReadable(): String {
	if (this <= 0) return "Never"

	val timestampMillis = if (this < 10_000_000_000L) this * 1000 else this
	val serverZoneId = TimeZone.getDefault().toZoneId()

	val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
		.withZone(serverZoneId)

	return formatter.format(Instant.ofEpochMilli(timestampMillis))
}

fun getCurrentTime(zone: ZoneId = ZoneId.systemDefault()): Long = ZonedDateTime.now(zone).toEpochSecond()

fun Long.toStringTime(): String {
	val hours = this / 3600
	val minutes = this / 60
	val seconds = this % 60

	var str = ""
	if (hours > 0) str += "${hours}h"
	if (hours > 0 && (minutes > 0 || seconds > 0)) str += " "
	if (minutes > 0) str += "${minutes}m"
	if (seconds > 0 && (hours > 0 || minutes > 0)) str += " "
	if (seconds > 0) str += "${seconds}s"

	return str
}
