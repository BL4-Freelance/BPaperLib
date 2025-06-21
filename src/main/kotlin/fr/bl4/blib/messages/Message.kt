package fr.bl4.blib.messages

import fr.bl4.blib.utils.colorize
import org.bukkit.command.CommandSender

fun CommandSender.message(vararg messages: String) {
	messages.forEach { message ->
		this.sendMessage(message.colorize())
	}
}
