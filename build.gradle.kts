import org.gradle.internal.extensions.stdlib.toDefaultLowerCase

plugins {
	alias(libs.plugins.kotlin)
	alias(libs.plugins.shadow)
	id("maven-publish")
}

group = "fr.bl4"
version = "1.2.0"
description = "A library for PaperMC Minecraft plugins."

repositories {
	mavenCentral()
	maven("https://repo.papermc.io/repository/maven-public/") { name = "papermc-repo" }
	maven("https://oss.sonatype.org/content/groups/public/") { name = "sonatype" }
	maven("https://repo.codemc.io/repository/maven-public/") { name = "CodeMC" }
	maven("https://repo.tcoded.com/releases") { name = "tcoded-repo" }
	maven("https://maven.devs.beer/")
}

dependencies {
	compileOnly(libs.paperApi)
	implementation(libs.kotlinStdlib)

	implementation(libs.inventoryFramework)

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
	val props = mapOf("version" to version, "description" to description)
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

	relocate("me.devnatan.inventoryframework", "fr.bl4.bpaperlib.external.inventoryframework")
	relocate("com.tcoded.folialib", "fr.bl4.bpaperlib.external.folialib")
}

publishing {
	publications {
		create<MavenPublication>("gpr") {
			groupId = project.group.toString()
			artifactId = project.name.toDefaultLowerCase()
			version = project.version.toString()

			artifact(tasks.shadowJar.get()) {
				classifier = null
			}

			pom {
				name.set(project.name)
				description.set("A library for PaperMC Minecraft plugins.")
				url.set("https://github.com/BL4-Freelance/BPaperLib")
				licenses {
					license {
						name.set("MIT License")
						url.set("https://opensource.org/licenses/MIT")
					}
				}
				developers {
					developer {
						id.set("bl4")
						name.set("Clems")
						email.set("bl4nshark@gmail.com")
					}
				}
				scm {
					connection.set("scm:git:git://github.com/BL4-Freelance/BPaperLib.git")
					developerConnection.set("scm:git:ssh://github.com:BL4-Freelance/BPaperLib.git")
					url.set("https://github.com/BL4-Freelance/BPaperLib")
				}
			}
		}
	}
	repositories {
		maven {
			name = "GitHubPackages"
			url = uri("https://maven.pkg.github.com/BL4-Freelance/BPaperLib")
			credentials {
				username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_USERNAME")
				password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
			}
		}
	}
}
