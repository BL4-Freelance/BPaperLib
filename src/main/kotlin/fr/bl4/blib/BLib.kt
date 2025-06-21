package fr.bl4.blib

import net.luckperms.api.LuckPerms
import net.luckperms.api.LuckPermsProvider
import org.bukkit.plugin.java.JavaPlugin


class BLib : JavaPlugin() {

	companion object {
		@JvmStatic
		lateinit var instance: BLib
			private set
	}

	val luckPermsApi: LuckPerms by lazy {
		LuckPermsProvider.get()
	}

	override fun onEnable() {
		instance = this
		logger.info("BLib enabled")
	}
}
