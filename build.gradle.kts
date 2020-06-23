import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4-M1"
    application
}

application {
    @Suppress("UnstableApiUsage")
    mainClass.set("com.omricat.androidxdash.MainKt")
}

group = "com.omricat"
version = "0.1"

repositories {
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation(gradleApi())

    implementation("com.michael-bull.kotlin-result:kotlin-result:1.1.6")

    implementation("io.reactivex.rxjava3:rxjava:3.0.3")
    implementation("io.reactivex.rxjava3:rxkotlin:3.0.0")

    fun retrofit(artifact: String) = "com.squareup.retrofit2:$artifact:2.9.0"

    implementation(retrofit("adapter-rxjava3"))
    implementation(retrofit("retrofit"))

    fun tinylog(artifact: String) = "org.tinylog:tinylog-$artifact:2.1.2"

    implementation(tinylog("api-kotlin"))
    implementation(tinylog("impl"))

    implementation("com.squareup:kotlinpoet:1.5.0")

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


