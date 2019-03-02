import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform") version "1.3.21"
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
    linuxX64(configure = configureNativeTarget)
    macosX64(configure = configureNativeTarget)
    mingwX64(configure = configureNativeTarget)

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(kotlin("stdlib"))
            }
        }
    }
}
