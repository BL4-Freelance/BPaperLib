package fr.bl4.blib.config

import dev.lone.itemsadder.api.CustomStack
import fr.bl4.blib.BLib
import fr.bl4.blib.item.ItemBuilder
import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

/**
 * @return The itemstack created/modified by the ConfigurationSection instance.
 * @throws IllegalArgumentException If the itemstack cannot be created.
 */
fun ConfigurationSection.getItemStack2(path: String, def: ItemStack): ItemStack {
	val itemPath = this.getConfigurationSection(path) ?: return def

	val textureKey = itemPath.getString("texture") ?: run {
		BLib.instance.logger.warning("[${itemPath.currentPath ?: itemPath.name}] Missing texture key")
		return def
	}

	val isItemsAdderAvailable = Bukkit.getPluginManager().isPluginEnabled("ItemsAdder")
	val customStack = if (isItemsAdderAvailable) CustomStack.getInstance(textureKey) else null

	val itemBuilder = if (customStack != null) {
		ItemBuilder(customStack.itemStack)
	} else {
		try {
			val texture = Material.valueOf(textureKey.uppercase())
			ItemBuilder(texture)
		} catch (_: IllegalArgumentException) {
			BLib.instance.logger.warning("[${itemPath.currentPath ?: itemPath.name}] Unknown texture: $textureKey")
			return def
		}
	}

	itemPath.getInt("amount", 1).let { itemBuilder.setAmount(it) }
	itemPath.getString("name")?.let { itemBuilder.setName(it) }
	itemPath.getStringList("lore").takeIf { it.isNotEmpty() }?.let { itemBuilder.setLore(it) }
	itemPath.getInt("model").takeIf { it != 0 }?.let { itemBuilder.setCustomModelData(it) }

	itemPath.getStringList("flags").takeIf { it.isNotEmpty() }?.let { flagList ->
		val flags = mutableListOf<ItemFlag>()
		for (flagName in flagList) {
			val flag = runCatching { ItemFlag.valueOf(flagName.uppercase()) }
				.getOrElse {
					BLib.instance.logger.warning("[${itemPath.currentPath ?: itemPath.name}] Unknown item flag: $flagName")
					return def
				}
			flags.add(flag)
		}
		itemBuilder.addFlags(*flags.toTypedArray())
	}

	itemPath.getConfigurationSection("enchantments")?.let { enchantsSection ->
		val enchants = mutableMapOf<Enchantment, Int>()
		for (key in enchantsSection.getKeys(false)) {
			val enchant = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT)
				.get(NamespacedKey.minecraft(key.lowercase()))
				?: run {
					BLib.instance.logger.warning("[${itemPath.currentPath ?: itemPath.name}] Unknown enchantment: $key")
					return def
				}
			val level = enchantsSection.getInt(key, 1)
			enchants[enchant] = level
		}
		itemBuilder.addEnchantments(enchants)
	}

	itemPath.getBoolean("unbreakable", false).let { itemBuilder.setUnbreakable(it) }
	itemPath.getBoolean("glowing", false).takeIf { it }?.let { itemBuilder.addGlowing() }

	return itemBuilder.toItemStack()
}

fun ConfigurationSection.setItemStack(path: String, itemStack: ItemStack?) {
	if (itemStack == null) {
		this.set(path, null)
		return
	}

	val itemPath = this.createSection(path)

	val customStack = CustomStack.byItemStack(itemStack)
	if (customStack != null) {
		itemPath.set("texture", customStack.namespacedID)
	} else {
		itemPath.set("texture", itemStack.type.name.lowercase())
	}

	if (itemStack.amount != 1) {
		itemPath.set("amount", itemStack.amount)
	}

	val meta = itemStack.itemMeta
	if (meta != null) {
		meta.displayName()?.let { itemPath.set("name", it) }

		if (meta.lore() != null && meta.lore()!!.isNotEmpty()) {
			itemPath.set("lore", meta.lore())
		}

		meta.customModelData.takeIf { it != 0 }?.let { itemPath.set("model", it) }

		if (meta.itemFlags.isNotEmpty()) {
			val flags = meta.itemFlags.map { it.name.lowercase() }
			itemPath.set("flags", flags)
		}

		if (meta.enchants.isNotEmpty()) {
			val enchantsItemPath = itemPath.createSection("enchantments")
			for ((enchant, level) in meta.enchants) {
				enchantsItemPath.set(enchant.key.key, level)
			}
		}

		if (meta.isUnbreakable) {
			itemPath.set("unbreakable", true)
		}

		if (meta.hasEnchant(Enchantment.UNBREAKING) && meta.itemFlags.contains(ItemFlag.HIDE_ENCHANTS)) {
			itemPath.set("glowing", true)
		}
	}
}
