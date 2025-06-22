plugins {
	kotlin("jvm") version "2.1.20"
	id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "fr.bl4"
version = "1.0"

repositories {
	mavenCentral()
	maven("https://repo.papermc.io/repository/maven-public/") {
		name = "papermc-repo"
	}
	maven("https://oss.sonatype.org/content/groups/public/") {
		name = "sonatype"
	}
	maven("https://repo.codemc.io/repository/maven-public/") {
		name = "CodeMC"
	}
	maven("https://maven.devs.beer/")
}

dependencies {
	compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

	compileOnly("de.tr7zw:item-nbt-api-plugin:2.15.0")
	compileOnly("dev.lone:api-itemsadder:4.0.10")
	compileOnly("net.luckperms:api:5.5")
	compileOnly(files("src/main/resources/SCore-5.25.6.21.jar"))
}

val targetJavaVersion = 21
kotlin {
	jvmToolchain(targetJavaVersion)
}

tasks.build {
	dependsOn("shadowJar")
}

tasks.processResources {
	val props = mapOf("version" to version)
	inputs.properties(props)
	filteringCharset = "UTF-8"
	filesMatching("plugin.yml") {
		expand(props)
	}
}

tasks.shadowJar {
	archiveFileName = "${project.name}-${project.version}.jar"
}

tasks.register<Copy>("copyToServer") {
	dependsOn("build")
	from(layout.buildDirectory.file("libs/${project.name}-$version.jar"))
	into(rootProject.layout.projectDirectory.dir("server/plugins"))
}
