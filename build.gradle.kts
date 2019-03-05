import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.3.11"
}

group = "com.sudzhaev"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    compile(kotlin("stdlib-jdk8"))
    compile("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.8")
    testCompile("io.kotlintest:kotlintest-assertions:3.3.1")
    testCompile("io.kotlintest:kotlintest-runner-junit5:3.3.0")

}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Test> {
    useJUnitPlatform()
}
