package fr.bl4.blib.config

import fr.bl4.blib.BLib
import fr.bl4.blib.item.ItemBuilder
import fr.bl4.blib.utils.colorize
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import kotlin.reflect.KProperty

val YamlConfiguration.keys get() = getKeys(false)
fun YamlConfiguration.section(path: String) = getConfigurationSection(path)
val YamlConfiguration.sections get() = keys.map { section(it) }

val ConfigurationSection.keys get() = getKeys(false)
fun ConfigurationSection.section(path: String) = getConfigurationSection(path)
val ConfigurationSection.sections get() = keys.map { section(it) }

fun JavaPlugin.saveResource(resource: String, file: File) {
	if (file.exists()) return
	file.parentFile.mkdirs()
	file.createNewFile()
	getResource(resource)!!.copyTo(file.outputStream())
}

fun JavaPlugin.init(vararg configs: PluginConfigFile) {
	configs.forEach {
		if (it.file != null) throw Exception("Config is already initialized")
		val fileName = "${it.path}.yml"
		val file = dataFolder[fileName]
		saveResource(fileName, file)
		it.file = file
		it.onReload()
	}
}

fun JavaPlugin.reload(vararg configs: PluginConfigFile) = configs.forEach {
	if (it.file == null || !it.file?.exists()!!) {
		init(it)
		return@forEach
	}

	it.reloadConfigFile()

	try {
		config
		it.onReload()
		BLib.instance.logger.info("Config reloaded successfully: ${it.file!!.path}")
	} catch (e: Exception) {
		BLib.instance.logger.severe("Error reloading config: ${e.message}")
	}
}

abstract class Config {
	private val cache = hashMapOf<String, Any?>()

	fun clearCache() = cache.clear()

	abstract var config: ConfigurationSection

	open var autoSave = true

	open val sections get() = config.sections

	open operator fun contains(key: String) = config.contains(key)

	open operator fun get(key: String) = config[key]

	open operator fun set(key: String, value: Any?) {
		val config = config
		config.set(key, value)
		if (autoSave) this.config = config
	}

//	open inner class any(val path: String, val def: Any? = null) {
//		operator fun getValue(ref: Any?, prop: KProperty<*>) = config.get(path, def)
//		operator fun setValue(ref: Any?, prop: KProperty<*>, value: Any?) = set(path, value)
//	}

	open inner class list(val path: String, val def: List<Any> = emptyList()) {
		operator fun getValue(ref: Any?, prop: KProperty<*>): List<*> =
			(cache[path]
				?: config.getList(path).orEmpty()) as List<*>
		operator fun setValue(ref: Any?, prop: KProperty<*>, value: List<Any>) = set(path, value)
	}

	open inner class boolean(val path: String, val def: Boolean = false) {
		operator fun getValue(ref: Any?, prop: KProperty<*>): Boolean =
			(cache[path]
				?: config.getBoolean(path, def).also { cache[path] = it }) as Boolean
		operator fun setValue(ref: Any?, prop: KProperty<*>, value: Boolean) = set(path, value)
	}

//	open inner class booleanList(val path: String) {
//		operator fun getValue(ref: Any?, prop: KProperty<*>): List<Boolean> = config.getBooleanList(path)
//		operator fun setValue(ref: Any?, prop: KProperty<*>, value: List<Boolean>) = set(path, value)
//	}

	open inner class string(val path: String, val def: String = "") {
		operator fun getValue(ref: Any?, prop: KProperty<*>): String =
			(cache[path]
				?: config.getString(path, def).orEmpty()) as String
		operator fun setValue(ref: Any?, prop: KProperty<*>, value: String) = set(path, value)
	}

	@Suppress("UNCHECKED_CAST")
	open inner class stringList(val path: String, listOf: List<String>) {
		operator fun getValue(ref: Any?, prop: KProperty<*>): List<String> =
			(cache[path]
				?: config.getStringList(path).also { cache[path] = it }) as List<String>
		operator fun setValue(ref: Any?, prop: KProperty<*>, value: List<String>) = set(path, value)
	}

	open inner class int(val path: String, val def: Int = 0) {
		operator fun getValue(ref: Any?, prop: KProperty<*>): Int =
			(cache[path]
				?: config.getInt(path, def).also { cache[path] = it }) as Int
		operator fun setValue(ref: Any?, prop: KProperty<*>, value: Int) = set(path, value)
	}

//	open inner class intList(val path: String) {
//		operator fun getValue(ref: Any?, prop: KProperty<*>): List<Int> = config.getIntegerList(path)
//		operator fun setValue(ref: Any?, prop: KProperty<*>, value: List<Int>) = set(path, value)
//	}

	open inner class long(val path: String, val def: Long = 0L) {
		operator fun getValue(ref: Any?, prop: KProperty<*>): Long =
			(cache[path]
				?: config.getLong(path, def).also { cache[path] = it }) as Long
		operator fun setValue(ref: Any?, prop: KProperty<*>, value: Long) = set(path, value)
	}

//	open inner class longList(val path: String) {
//		operator fun getValue(ref: Any?, prop: KProperty<*>): List<Long> = config.getLongList(path)
//		operator fun setValue(ref: Any?, prop: KProperty<*>, value: List<Long>) = set(path, value)
//	}

//	open inner class double(val path: String, val def: Double = 0.0) {
//		operator fun getValue(ref: Any?, prop: KProperty<*>): Double = config.getDouble(path, def)
//		operator fun setValue(ref: Any?, prop: KProperty<*>, value: Double) = set(path, value)
//	}

//	open inner class doubleList(val path: String) {
//		operator fun getValue(ref: Any?, prop: KProperty<*>): List<Double> = config.getDoubleList(path)
//		operator fun setValue(ref: Any?, prop: KProperty<*>, value: List<Double>) = set(path, value)
//	}

//	open inner class byteList(val path: String) {
//		operator fun getValue(ref: Any?, prop: KProperty<*>): List<Byte> = config.getByteList(path)
//		operator fun setValue(ref: Any?, prop: KProperty<*>, value: List<Byte>) = set(path, value)
//	}

//	open inner class floatList(val path: String) {
//		operator fun getValue(ref: Any?, prop: KProperty<*>): List<Float> = config.getFloatList(path)
//		operator fun setValue(ref: Any?, prop: KProperty<*>, value: List<Float>) = set(path, value)
//	}

//	open inner class shortList(val path: String) {
//		operator fun getValue(ref: Any?, prop: KProperty<*>): List<Short> = config.getShortList(path)
//		operator fun setValue(ref: Any?, prop: KProperty<*>, value: List<Short>) = set(path, value)
//	}

//	open inner class charList(val path: String) {
//		operator fun getValue(ref: Any?, prop: KProperty<*>): List<Char> = config.getCharacterList(path)
//		operator fun setValue(ref: Any?, prop: KProperty<*>, value: List<Char>) = set(path, value)
//	}

//	open inner class color(val path: String, val def: Color? = null) {
//		operator fun getValue(ref: Any?, prop: KProperty<*>): Color? = config.getColor(path, def)
//		operator fun setValue(ref: Any?, prop: KProperty<*>, value: Color?) = set(path, value)
//	}

	open inner class item(val path: String, val def: ItemStack = ItemBuilder(Material.BARRIER).setName("Invalid").toItemStack()) {
		operator fun getValue(ref: Any?, prop: KProperty<*>): ItemStack =
			(cache[path]
				?: config.getItemStack2(path, def).also { cache[path] = it }) as ItemStack
		operator fun setValue(ref: Any?, prop: KProperty<*>, value: ItemStack?) = config.setItemStack(path, value)
	}

	open inner class message(val path: String, val def : String = "") {
		operator fun getValue(ref: Any?, prop: KProperty<*>): Component =
			(cache[path]
				?: config.getString(path, def)?.colorize().also { cache[path] = it }
				?: Component.empty()) as Component
		operator fun setValue(ref: Any?, prop: KProperty<*>, value: Component) = set(path, value)
	}

//	open inner class offlinePlayer(val path: String, val def: OfflinePlayer? = null) {
//		operator fun getValue(ref: Any?, prop: KProperty<*>): OfflinePlayer? = config.getOfflinePlayer(path, def)
//		operator fun setValue(ref: Any?, prop: KProperty<*>, value: OfflinePlayer?) = set(path, value)
//	}

//	open inner class vector(val path: String, val def: Vector? = null) {
//		operator fun getValue(ref: Any?, prop: KProperty<*>): Vector? = config.getVector(path, def)
//		operator fun setValue(ref: Any?, prop: KProperty<*>, value: Vector?) = set(path, value)
//	}

//	open inner class location(val path: String, val def: Location? = null) {
//		operator fun getValue(ref: Any?, prop: KProperty<*>): Location? = config.getLocation(path, def)
//		operator fun setValue(ref: Any?, prop: KProperty<*>, value: Location?) = set(path, value)
//	}

//	open inner class serializable<T : ConfigurationSerializable>(
//		val path: String,
//		val clazz: Class<T>,
//		val def: T? = null
//	) {
//		operator fun getValue(ref: Any?, prop: KProperty<*>): T? = config.getSerializable(path, clazz, def)
//		operator fun setValue(ref: Any?, prop: KProperty<*>, value: T?) = set(path, value)
//	}

//	open inner class mapList(val path: String) {
//		operator fun getValue(ref: Any?, prop: KProperty<*>): List<Map<*, *>> = config.getMapList(path)
//		operator fun setValue(ref: Any?, prop: KProperty<*>, value: List<Map<*, *>>) = set(path, value)
//	}

//	open inner class section(val path: String) {
//		operator fun getValue(ref: Any?, prop: KProperty<*>): ConfigurationSection? = config.getConfigurationSection(path)
//		operator fun setValue(ref: Any?, prop: KProperty<*>, value: ConfigurationSection?) = set(path, value)
//	}
}

open class ConfigFile(open var file: File?) : Config() {

	private var _config: ConfigurationSection? = null

	fun reloadConfigFile() {
		_config = null
		clearCache()
	}

	open fun onReload() {}

	override var config: ConfigurationSection
		get() {
			val file = file ?: throw Exception("Config is not initialized")
			return _config ?: YamlConfiguration.loadConfiguration(file).also { _config = it }
		}
		set(value) {
			val file = file ?: throw Exception("Config is not initialized")
			val config = value as? FileConfiguration
				?: throw Exception("Could not save ${config.name} to ${file.name}")
			config.save(file)
		}
}

open class PluginConfigFile(open var path: String) : ConfigFile(null)

open class ConfigSection(
	open var parent: Config, open var path: String
) : Config() {

	override var config: ConfigurationSection
		get() {
			val config = parent.config.getConfigurationSection(path)
			return config ?: throw Exception("Could not load $path from ${parent.config.name}")
		}
		set(value) {
			parent[path] = value
		}
}
