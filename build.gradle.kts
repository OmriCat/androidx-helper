import com.github.michaelbull.result.get
import com.omricat.androidxhelperplugin.generateFileSpecs
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") //version comes from buildSrc
    `java-gradle-plugin`
    id("com.dorongold.task-tree") version "1.5"
    `maven-publish`
    idea
}

group = "com.omricat"

version = "0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(gradleApi())
}
val generatedDir: File = file("$buildDir/generated/sources/androidxplugin/kotlin/main")

sourceSets {
    main {
        java.srcDir(generatedDir)
    }
}


val codegenTask: Task by tasks.register("codegen") {
    doLast {
        val fileSpecs = generateFileSpecs("com.omricat.androidxhelperplugin").get() ?: throw GradleException()
        fileSpecs.forEach {
            it.writeTo(generatedDir)
        }
    }
}

tasks.withType<KotlinCompile> {
    dependsOn(codegenTask)
}

idea {
    module {
        generatedSourceDirs = generatedSourceDirs + file(generatedDir)
    }
}

gradlePlugin {
    plugins.create("androidxGradleHelperPlugin") {
        id = "com.omricat.androidx-helper-plugin"
        displayName = "AndroidX Gradle Helper Plugin"
        implementationClass = "com.omricat.androidxhelperplugin.NoOpPlugin"
    }
}
