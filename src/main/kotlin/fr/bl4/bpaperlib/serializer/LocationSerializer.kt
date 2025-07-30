package fr.bl4.bpaperlib.serializer

import org.bukkit.Bukkit
import org.bukkit.Location

/**
 * Converts a [Location] to a string
 * @see String.toLocation
 */
fun Location.toSerializedString(): String {
	return "${world?.name ?: "world"};$x;$y;$z;$yaw;$pitch"
}

/**
 * Converts a string to a [Location]
 * @see Location.toSerializedString
 * @throws IllegalArgumentException If the string is invalid
 */
fun String.toLocation(): Location {
	val parts = split(";")
	if (parts.size != 6) throw IllegalArgumentException("Invalid location string format")

	val world = Bukkit.getWorld(parts[0]) ?: throw IllegalArgumentException("World not found: ${parts[0]}")
	val x = parts[1].toDoubleOrNull() ?: throw IllegalArgumentException("Invalid x coordinate: ${parts[1]}")
	val y = parts[2].toDoubleOrNull() ?: throw IllegalArgumentException("Invalid y coordinate: ${parts[2]}")
	val z = parts[3].toDoubleOrNull() ?: throw IllegalArgumentException("Invalid z coordinate: ${parts[3]}")
	val yaw = parts[4].toFloatOrNull() ?: throw IllegalArgumentException("Invalid yaw: ${parts[4]}")
	val pitch = parts[5].toFloatOrNull() ?: throw IllegalArgumentException("Invalid pitch: ${parts[5]}")

	return Location(world, x, y, z, yaw, pitch)
}
