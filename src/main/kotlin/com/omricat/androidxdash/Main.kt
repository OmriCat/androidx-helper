package com.omricat.androidxdash

import com.github.michaelbull.result.*
import com.omricat.androidxdash.codegen.asBfsIterable
import com.omricat.androidxdash.codegen.generateClasses
import com.omricat.androidxdash.codegen.toPathTree
import io.reactivex.rxjava3.core.Single
import java.nio.file.Paths

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

    fileSpecs.onSuccess {
        val outDir = Paths.get("").resolve("out").normalize()
        it.forEach { fileSpec -> fileSpec.writeTo(outDir) }
    }

    fileSpecs.onFailure {
        println(it)
    }

}
