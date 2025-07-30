plugins {
	alias(libs.plugins.kotlin)
	alias(libs.plugins.shadow)
}

group = "fr.bl4"
version = "1.0.0"

repositories {
	mavenCentral()
	maven("https://repo.papermc.io/repository/maven-public/") { name = "papermc-repo" }
	maven("https://oss.sonatype.org/content/groups/public/") { name = "sonatype" }
	maven("https://repo.codemc.io/repository/maven-public/") { name = "CodeMC" }
	maven("https://maven.devs.beer/")
}

dependencies {
	compileOnly(libs.paperApi)
	implementation(libs.kotlinStdlib)

	compileOnly(libs.itemNbtApi)
	compileOnly(libs.itemsAdder)
	compileOnly(libs.luckpermsApi)
}

kotlin {
	jvmToolchain(libs.versions.java.get().toInt())
}

tasks.build {
	dependsOn(tasks.shadowJar)
}

tasks.processResources {
	val props = mapOf("version" to version)
	inputs.properties(props)
	filteringCharset = "UTF-8"
	duplicatesStrategy = DuplicatesStrategy.INCLUDE

	from(sourceSets.main.get().resources) {
		include("plugin.yml")
		expand(props)
	}

	from(sourceSets.main.get().resources) {
		exclude("plugin.yml")
	}
}

tasks.shadowJar {
	archiveFileName.set("${project.name}-${project.version}.jar")
}
