import com.github.michaelbull.result.get
import com.omricat.androidxdash.generateFileSpecs
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    `java-gradle-plugin`
    id("com.dorongold.task-tree") version "1.5"
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
val outputDir: File = project.layout.buildDirectory.dir("generated/sources/androidxplugin/kotlin/main").get().asFile

sourceSets {
    main {
        java.srcDir(outputDir)
    }
}


val codegenTask: Task by tasks.register("codegen") {
    doLast {
        val fileSpecs = generateFileSpecs().get() ?: throw GradleException()
        fileSpecs.forEach {
            it.writeTo(outputDir)
        }
    }
}

tasks.withType<KotlinCompile> {
    dependsOn(codegenTask)
}

idea {
    module {
        generatedSourceDirs = generatedSourceDirs + file(outputDir)
    }
}
