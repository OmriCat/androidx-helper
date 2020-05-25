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

    implementation("io.reactivex.rxjava3:rxjava:3.0.3")

    fun retrofit(artifact: String) = "com.squareup.retrofit2:$artifact:2.9.0"

    implementation(retrofit("adapter-rxjava3"))
    implementation(retrofit("retrofit"))

    fun kotest(artifact: String): String = "io.kotest:$artifact:4.0.5"

    testImplementation(kotest("kotest-runner-junit5-jvm"))
    testImplementation(kotest("kotest-assertions-core-jvm"))
    testImplementation(kotest("kotest-property-jvm"))

    testImplementation("com.squareup.okhttp3:mockwebserver:4.7.2")
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


