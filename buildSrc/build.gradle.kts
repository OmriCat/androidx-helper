import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.0"
    id("com.adarshr.test-logger") version "2.1.0"
}


repositories {
    mavenCentral()
}

dependencies {

    implementation(gradleApi())
    implementation(gradleKotlinDsl())

    implementation(platform(kotlin("bom")))
    implementation(kotlin("gradle-plugin"))
    implementation(kotlin("reflect"))

    implementation("com.michael-bull.kotlin-result:kotlin-result:1.1.6")

    implementation("io.reactivex.rxjava3:rxjava:3.0.3")
    implementation("io.reactivex.rxjava3:rxkotlin:3.0.0")

    fun retrofit(artifact: String) = "com.squareup.retrofit2:$artifact:2.9.0"

    implementation(retrofit("adapter-rxjava3"))
    implementation(retrofit("retrofit"))

    implementation("io.github.microutils:kotlin-logging:1.8.3")
    implementation("org.slf4j:slf4j-simple:1.7.30")

    implementation("com.squareup:kotlinpoet:1.5.0")

    fun kotest(artifact: String): String = "io.kotest:$artifact:4.0.5"

    testImplementation(kotest("kotest-runner-junit5-jvm"))
    testImplementation(kotest("kotest-assertions-core-jvm"))
    testImplementation(kotest("kotest-property-jvm"))

    testImplementation("com.github.tschuchortdev:kotlin-compile-testing:1.2.10")

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

