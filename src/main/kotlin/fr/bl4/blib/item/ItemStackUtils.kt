package fr.bl4.blib.item

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
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

fun ItemStack.isSameSpecificMeta(itemstack: ItemStack, specificMeta: ItemSpecificMeta): Boolean {
	val thisMeta = this.itemMeta ?: return false
	val itemstackMeta = itemstack.itemMeta ?: return false

	return when (specificMeta) {
		ItemSpecificMeta.TYPE -> this.type === itemstack.type
		ItemSpecificMeta.NAME -> {
			val thisName = PlainTextComponentSerializer.plainText().serialize(this.displayName())
			val itemstackName = PlainTextComponentSerializer.plainText().serialize(itemstack.displayName())

			thisName == itemstackName
		}
		ItemSpecificMeta.MODEL -> thisMeta.hasCustomModelData() == itemstackMeta.hasCustomModelData()
				&& thisMeta.customModelData == itemstackMeta.customModelData
		ItemSpecificMeta.FLAGS -> thisMeta.itemFlags == itemstackMeta.itemFlags
		ItemSpecificMeta.ENCHANTMENTS -> this.enchantments == itemstack.enchantments
	}
}

fun ItemStack?.isSame(itemStack: ItemStack?, specificMetas: List<ItemSpecificMeta>?): Boolean {
	if (this == null && itemStack == null) return true
	if (this == null || itemStack == null) return false

	if (specificMetas == null ||specificMetas.isEmpty())
		return this.isSimilar(itemStack)

	for (meta in specificMetas) {
		if (!this.isSameSpecificMeta(itemStack, meta))
			return false
	}

	return true
}
