package fr.bl4.bpaperlib.messages

import fr.bl4.bpaperlib.utils.colorize
import org.bukkit.command.CommandSender

fun CommandSender.message(vararg messages: String) {
	messages.forEach { message ->
		this.sendMessage(message.colorize())
	}
}
