plugins {
    kotlin("jvm") version "1.7.22"
}

repositories {
    mavenCentral()
}

tasks {
    sourceSets {
        main {
            kotlin.srcDirs("src")
        }
        test {
            kotlin.srcDirs("test")
        }
    }

    wrapper {
        gradleVersion = "7.3"
    }
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
    implementation("org.choco-solver:choco-solver:4.10.14")
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.2")
    testImplementation("org.hamcrest:hamcrest:3.0")

}
