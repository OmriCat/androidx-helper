
plugins {
    kotlin("jvm")
    `java-gradle-plugin`
    id("com.omricat.androidx-dash-plugin")
    id("com.dorongold.task-tree") version "1.5"
}

group = "com.omricat"

version = "0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(gradleApi())
}

sourceSets {
    main {
//        java.srcDir()
    }
}


//val codgenTask by tasks.register("codegen") {
//    doLast {
//        val fileSpecs = generateFileSpecs().get() ?: throw GradleException()
//        val outputDir = project.layout.buildDirectory.dir("generated/sources/androidx-dash/kotlin/main").get().asFile
//        fileSpecs.forEach {
//            it.writeTo(outputDir)
//        }
//
//    }
//}
