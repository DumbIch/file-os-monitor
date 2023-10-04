plugins {
    kotlin("jvm") version "1.9.10"
    application
}

group = "ru.dumdumbich"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("commons-io:commons-io:2.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.8.10")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

application {
    mainClass.set("ru.dumdumbich.fileosmonitor.AppKt")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "ru.dumdumbich.fileosmonitor.AppKt"
    }
    configurations["compileClasspath"].forEach { file: File ->
        from(zipTree(file.absoluteFile))
    }
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}
