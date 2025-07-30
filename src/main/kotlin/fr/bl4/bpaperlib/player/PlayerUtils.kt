package fr.bl4.bpaperlib.player

import fr.bl4.bpaperlib.BPaperLib
import fr.bl4.bpaperlib.item.isAirOrNull
import net.luckperms.api.model.user.User
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

fun Inventory.addItemOrDrop(item: ItemStack, dropLocation: Location? = null) {
	if (item.isAirOrNull()) return

	val remaining = this.addItem(item)

	if (remaining.isNotEmpty()) {
		val location = dropLocation ?: (this.holder as? Player)?.location
		if (location != null) {
			for ((_, leftoverItem) in remaining) {
				location.world?.dropItemNaturally(location, leftoverItem)
			}
		} else {
			BPaperLib.instance.logger.severe("Couldn't drop item: no valid location and holder is not a Player. Item: $item")
		}
	}
}

fun Player.getPermissionLevel(base: String, max: Int = Int.MAX_VALUE): Int {
	val user: User = BPaperLib.instance.luckPermsApi.userManager.getUser(this.uniqueId) ?: return 0

	val regex = Regex("^${Regex.escape(base)}\\.(\\d+)$")

	return user.cachedData.permissionData.permissionMap.keys.mapNotNull { permission ->
		regex.matchEntire(permission)?.groups?.get(1)?.value?.toIntOrNull()
	}.maxOrNull()?.coerceAtMost(max) ?: 0
}
