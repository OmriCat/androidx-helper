package com.omricat.androidxdash

import com.github.michaelbull.result.*
import com.omricat.androidxdash.codegen.asBfsIterable
import com.omricat.androidxdash.codegen.generateClasses
import com.omricat.androidxdash.codegen.toPathTree
import com.squareup.kotlinpoet.FileSpec
import io.reactivex.rxjava3.core.Single
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.StopExecutionException
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinBasePluginWrapper
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.File

class AndroidXCodegenPlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = with(project) {
        check(hasKotlinPlugin()) { "Kotlin plugin not found" }
        val outDir = layout.buildDirectory.get().dir("generated/androidxplugin/kotlin/main").asFile
        val codegenTask = tasks.register<DefaultTask>("codegen") {
            doLast {
                runCodeGen(outDir) }
        }
        tasks.withType<KotlinCompile> {
            dependsOn(codegenTask)
        }
        extensions.configure<KotlinProjectExtension> {
            sourceSets["main"].kotlin.srcDir(outDir)
        }

    }
}

private fun runCodeGen(outDir: File) {
    val fileSpecs = generateFileSpecs().get() ?: throw StopExecutionException()
    fileSpecs.forEach { fileSpec ->
        fileSpec.writeTo(outDir)
    }

}

fun generateFileSpecs(service: GoogleMaven = GoogleMaven.instance()): Result<Set<FileSpec>, Any> {
    val groupsResult = service.groupsIndex()
        .flatMap {
            when (it) {
                is Ok<GroupsList> -> it.value.groups.getDetails(service)
                is Err<Any> -> Single.just(it)
            }
        }
        .onErrorReturn { Err(it) }
        .blockingGet()


    val androidxTree = groupsResult.flatMap { groups ->
        val tree = groups
            .sortedBy { it.groupName.name }
            .toPathTree("com.omricat.androidxplugin")
        val androidxRoot =
            tree.root
                .asBfsIterable()
                .find { it.pathComponent.startsWith("androidx") }
        androidxRoot.toResultOr { "No androidx groups found" }
            .map { newRoot -> tree.copy(root = newRoot) }
    }

    return androidxTree.flatMap {
        runCatching { it.generateClasses() }
    }

}

private fun Project.hasKotlinPlugin(): Boolean =
    plugins.asSequence().mapNotNull { it as? KotlinBasePluginWrapper }
        .firstOrNull() != null
