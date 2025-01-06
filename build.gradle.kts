plugins {
    kotlin("jvm") version "2.1.0"
}

group = "dev.codebasedlearning.adventofcode.y2024"
version = "1.0.1"

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("com.github.codebasedlearning:adventofcode_commons:d2fd6fcf5d") {
        isChanging = true
    }
    testImplementation(kotlin("test"))
}

configurations.all {
    resolutionStrategy.cacheChangingModulesFor(0, "seconds")
}

tasks.test {
    useJUnitPlatform()
}

subprojects {
    //apply(plugin = "jvm")
    apply(plugin = "org.jetbrains.kotlin.jvm")

    repositories {
        mavenCentral() // Inherits repositories for dependency resolution
        maven { url = uri("https://jitpack.io") }

    }

    dependencies {
        //  ls -rm ~/.gradle/caches/modules-2/files-2.1/com.github.codebasedlearning/adventofcode_commons/main-SNAPSHOT/
        //implementation("com.github.rowlf:cbl_aoc_common:main-SNAPSHOT") // +
        implementation("com.github.codebasedlearning:adventofcode_commons:d2fd6fcf5d") {
            isChanging = true
        }
//
        testImplementation(kotlin("test"))
    }

}
