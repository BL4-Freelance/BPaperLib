package fr.bl4.blib.item

import fr.bl4.blib.utils.colorize
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

class ItemBuilder {
	private val itemStack: ItemStack

	/**
	 * Create a new ItemBuilder from scratch.
	 * @param material The material to create the ItemBuilder with.
	 */
	constructor(material: Material) : this(material, 1)

	/**
	 * Create a new ItemBuilder over an existing itemstack.
	 * @param itemStack The itemstack to create the ItemBuilder over.
	 */
	constructor(itemStack: ItemStack) {
		this.itemStack = itemStack
	}

	/**
	 * Create a new ItemBuilder from scratch.
	 * @param material The material of the item.
	 * @param amount   The amount of the item.
	 */
	constructor(material: Material, amount: Int) {
		itemStack = ItemStack(material, amount)
	}

	/**
	 * Clone the ItemBuilder into a new one.
	 * @return The cloned instance.
	 */
	fun clone(): ItemBuilder {
		return ItemBuilder(itemStack.clone())
	}

	/**
	 * Set the amount of the item.
	 * @param amount The amount to change it to.
	 */
	fun setAmount(amount: Int): ItemBuilder {
		itemStack.amount = amount
		return this
	}

	/**
	 * Set the display name of the item.
	 * @param name The name to change it to.
	 */
	fun setName(name: String): ItemBuilder {
		val itemMeta = itemStack.itemMeta ?: return this
		itemMeta.displayName(name.colorize())
		itemStack.itemMeta = itemMeta
		return this
	}

	/**
	 * Add an enchantment.
	 * @param enchantment The enchantment to add.
	 * @param level       The level of the enchantment.
	 */
	fun addEnchantment(enchantment: Enchantment, level: Int): ItemBuilder {
		itemStack.addUnsafeEnchantment(enchantment, level)
		return this
	}

	/**
	 * Remove an enchantment.
	 * @param enchantment The enchantment to remove
	 */
	fun removeEnchantment(enchantment: Enchantment): ItemBuilder {
		itemStack.removeEnchantment(enchantment)
		return this
	}

	/**
	 * Add multiple enchants at once.
	 * @param enchantments The enchantments to add.
	 */
	fun addEnchantments(enchantments: Map<Enchantment, Int>): ItemBuilder {
		itemStack.addEnchantments(enchantments)
		return this
	}

	/**
	 * Re-sets the lore.
	 * @param lore The lore to set it to.
	 */
	fun setLore(lore: List<String>): ItemBuilder {
		val itemMeta = itemStack.itemMeta ?: return this
		itemMeta.lore(lore.map { it.colorize() })
		itemStack.itemMeta = itemMeta
		return this
	}

	/**
	 * Add flags to the item
	 * @param flags The flags to add to the item.
	 */
	fun addFlags(vararg flags: ItemFlag): ItemBuilder {
		val itemMeta = itemStack.itemMeta ?: return this
		itemMeta.addItemFlags(*flags)
		itemStack.itemMeta = itemMeta
		return this
	}

	/**
	 * Sets the modelData of the item
	 * @param modelData The modelData to set on the item.
	 */
	fun setCustomModelData(modelData: Int): ItemBuilder {
		val itemMeta = itemStack.itemMeta ?: return this
		itemMeta.setCustomModelData(modelData)
		itemStack.itemMeta = itemMeta
		return this
	}

	/**
	 * Define if item is unbreakable
	 * @param unbreakable true if the item is unbreakable, otherwise false.
	 */
	fun setUnbreakable(unbreakable: Boolean): ItemBuilder {
		val itemMeta = itemStack.itemMeta ?: return this
		itemMeta.isUnbreakable = unbreakable
		itemStack.itemMeta = itemMeta
		return this
	}

	/**
	 * Define if item is glowing
	 */
	fun addGlowing(): ItemBuilder {
		addEnchantment(Enchantment.UNBREAKING, 1)
		addFlags(ItemFlag.HIDE_ENCHANTS)
		return this
	}

	fun setSkullOwner(player: OfflinePlayer): ItemBuilder {
		val skullMeta = itemStack.itemMeta as? SkullMeta ?: return this
		skullMeta.owningPlayer = player
		itemStack.itemMeta = skullMeta
		return this
	}

	/**
	 * Retrieves the itemstack from the ItemBuilder.
	 * @return The itemstack created/modified by the ItemBuilder instance.
	 */
	fun toItemStack(): ItemStack {
		return itemStack
	}
}
