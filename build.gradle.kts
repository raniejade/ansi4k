import org.gradle.internal.os.OperatingSystem
import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform") version "1.3.21"
    `maven-publish`
    id("com.jfrog.bintray") version "1.8.4"
    id("org.ajoberstar.reckon") version "0.9.0"
}

group = "com.github.raniejade"

repositories {
    jcenter()
}

kotlin {
    val configureNativeTarget: KotlinNativeTarget.() -> Unit = {
        binaries {
            staticLib()
            // testing only
            executable()
        }
    }
    jvm()
    linuxX64("linux", configureNativeTarget)
    macosX64("macos", configureNativeTarget)
    mingwX64("windows", configureNativeTarget)

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(kotlin("stdlib"))
            }
        }
    }
}

reckon {
    scopeFromProp()
    stageFromProp("alpha", "final")
}

tasks {
    val stubJavadoc by creating(Jar::class) {
        archiveClassifier.set("javadoc")
    }
}

val os = OperatingSystem.current()
val artifacts = when {
    os.isWindows -> listOf("windows")
    os.isMacOsX -> listOf("macos")
    os.isLinux -> listOf("metadata", "linux", "jvm")
    else -> throw AssertionError("Unsupported os: $os")
}

publishing {
    kotlin.targets.forEach { target ->
        publications.findByName(target.name)?.let {
            (it as MavenPublication).artifact(tasks["stubJavadoc"])
        }
    }
}

bintray {
    user = System.getenv("BINTRAY_USER")
    key = System.getenv("BINTRAY_API_KEY")
    dryRun = true
    publish = false
    with(pkg) {
        repo = "maven"
        desc = "Terminal colors"
        name = "termkolors"
        userOrg = "raniejade"
        setLicenses("MIT")
        setLabels("kotlin", "terminal", "color", "ansi")
        vcsUrl = "https://github.com/raniejade/termkolors.git"
        githubRepo = "raniejade/termkolors"
        with(version) {
            name = project.version.toString()
        }
    }

    setPublications(*artifacts.toTypedArray())
}
