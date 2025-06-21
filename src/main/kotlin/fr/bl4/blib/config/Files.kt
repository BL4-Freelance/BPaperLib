package fr.bl4.blib.config

import java.io.File

operator fun File.get(path: String) = File(this, path)
operator fun File.contains(name: String) = name in list().orEmpty()