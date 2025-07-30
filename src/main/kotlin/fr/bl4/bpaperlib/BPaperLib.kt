package fr.bl4.bpaperlib

import net.luckperms.api.LuckPerms
import net.luckperms.api.LuckPermsProvider
import org.bukkit.plugin.java.JavaPlugin


class BPaperLib : JavaPlugin() {

	companion object {
		@JvmStatic
		lateinit var instance: BPaperLib
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
