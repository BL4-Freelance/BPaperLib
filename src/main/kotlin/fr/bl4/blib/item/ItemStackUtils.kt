package fr.bl4.blib.item

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

private val gsonSerializer = GsonComponentSerializer.gson()

fun ItemStack.replacePlaceholders(placeholders: Map<String, String>): ItemStack {
	val meta = this.itemMeta ?: return this

	meta.apply {
		displayName()?.let { display ->
			this.displayName(display.replaces(placeholders))
		}

		lore()?.let { lore ->
			this.lore(lore.map { it.replaces(placeholders) })
		}
	}

	this.itemMeta = meta
	return this
}

fun Component.replaces(placeholders: Map<String, String>): Component {
	val json = gsonSerializer.serialize(this)
	val replaced = placeholders.entries.fold(json) { acc, (key, value) ->
		acc.replace(key, value)
	}
	return gsonSerializer.deserialize(replaced)
}

/**
 * Returns true if the ItemStack is null or [ItemStack.getType] is [Material.AIR]
 */
fun ItemStack?.isAirOrNull(): Boolean = this == null || this.type == Material.AIR
