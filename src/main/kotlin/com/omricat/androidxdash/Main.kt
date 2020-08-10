package com.omricat.androidxdash

import com.github.michaelbull.result.*
import com.omricat.androidxdash.codegen.asBfsIterable
import com.omricat.androidxdash.codegen.generateClasses
import com.omricat.androidxdash.codegen.toPathTree
import io.reactivex.rxjava3.core.Single

private val divider = "-".repeat(16)

fun main() {

    val service = GoogleMaven.instance()

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

    val fileSpecs = androidxTree.flatMap {
        runCatching { it.generateClasses() }
    }

    println(
        when (fileSpecs) {
            is Err -> "Error: $fileSpecs"
            is Ok -> fileSpecs.value.map { spec ->
                """$divider
                |${spec.name}
                |$divider
                |$spec""".trimMargin()
            }.joinToString("\n")
            }
    )

}
