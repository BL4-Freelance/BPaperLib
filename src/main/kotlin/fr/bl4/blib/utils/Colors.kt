package fr.bl4.blib.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage

fun String.colorize(): Component {
	val pattern = "[&§]([0-9a-fA-FkmolnrKMOLNR])".toRegex()
	val matcher = pattern.toPattern().matcher(this)

	val result = StringBuffer()
	while (matcher.find()) {
		val colorCode = matcher.group(1)
		val replacement = getColorReplacement(colorCode)
		matcher.appendReplacement(result, replacement)
	}
	matcher.appendTail(result)

	return MiniMessage.miniMessage()
		.deserialize(result.toString())
		.decoration(TextDecoration.ITALIC, false)
}

private fun getColorReplacement(code: String): String {
	return when (code.lowercase()) {
		"0" -> "<black>"
		"1" -> "<dark_blue>"
		"2" -> "<dark_green>"
		"3" -> "<dark_aqua>"
		"4" -> "<dark_red>"
		"5" -> "<dark_purple>"
		"6" -> "<gold>"
		"7" -> "<gray>"
		"8" -> "<dark_gray>"
		"9" -> "<blue>"
		"a" -> "<green>"
		"b" -> "<aqua>"
		"c" -> "<red>"
		"d" -> "<light_purple>"
		"e" -> "<yellow>"
		"f" -> "<white>"
		"r" -> "<reset>"
		"l" -> "<bold>"
		"n" -> "<underlined>"
		"m" -> "<strikethrough>"
		"o" -> "<italic>"
		"k" -> "<obfuscated>"
		else -> ""
	}
}
