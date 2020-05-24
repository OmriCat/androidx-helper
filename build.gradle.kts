import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4-M1"
}

group = "com.omricat"
version = "0.1"

repositories {
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.michael-bull.kotlin-result:kotlin-result:1.1.6")

    fun kotest(artifact: String): String = "io.kotest:$artifact:4.0.5"

    testImplementation(kotest("kotest-runner-junit5-jvm"))
    testImplementation(kotest("kotest-assertions-core-jvm"))
    testImplementation(kotest("kotest-property-jvm"))
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xinline-classes")
    }
}


