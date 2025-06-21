package fr.bl4.blib.player

import fr.bl4.blib.BLib
import fr.bl4.blib.item.isAirOrNull
import net.luckperms.api.model.user.User
import net.luckperms.api.node.NodeType
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.util.stream.Collectors

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
			BLib.instance.logger.severe("Couldn't drop item: no valid location and holder is not a Player. Item: $item")
		}
	}
}

fun Player.getPermissionLevel(base: String, max: Int = Int.MAX_VALUE): Int {
	val user: User = BLib.instance.luckPermsApi.userManager.getUser(this.uniqueId) ?: return 0

	val regex = Regex("^${Regex.escape(base)}\\.(\\d+)$")

	return user.cachedData.permissionData.permissionMap.keys.mapNotNull { permission ->
		regex.matchEntire(permission)?.groups?.get(1)?.value?.toIntOrNull()
	}.maxOrNull()?.coerceAtMost(max) ?: 0
}
