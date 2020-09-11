package com.omricat.androidxhelperplugin

import com.github.michaelbull.result.*
import com.omricat.androidxhelperplugin.codegen.asBfsIterable
import com.omricat.androidxhelperplugin.codegen.generateClasses
import com.omricat.androidxhelperplugin.codegen.toPathTree
import com.squareup.kotlinpoet.FileSpec
import io.reactivex.rxjava3.core.Single
import org.gradle.api.Plugin
import org.gradle.api.Project

fun generateFileSpecs(packageName: String, service: GoogleMaven = GoogleMaven.instance()): Result<Set<FileSpec>, Any> {
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
            .toPathTree(packageName)
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

class NoOpPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        // This Plugin class only exists to make it possible to apply the plugin in the plugins block
    }
}
