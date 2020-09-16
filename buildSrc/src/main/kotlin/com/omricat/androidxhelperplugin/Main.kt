package com.omricat.androidxhelperplugin

import com.omricat.androidxhelperplugin.codegen.GroupPathTree
import com.omricat.androidxhelperplugin.codegen.asBfsIterable
import com.omricat.androidxhelperplugin.codegen.generateClasses
import com.omricat.androidxhelperplugin.codegen.toPathTree
import com.squareup.kotlinpoet.FileSpec
import io.reactivex.rxjava3.core.Single

fun generateFileSpecs(
  packageName: String,
  service: GoogleMaven = GoogleMaven.instance()
): Single<Set<FileSpec>> {
  val groupsResult = service.groupsIndex()
    .flatMap { it.groups.getDetails(service) }

  val androidxTree = groupsResult.flatMap { groups ->
    val tree: GroupPathTree = groups
      .sortedBy { it.groupName.name }
      .toPathTree(packageName)
    val androidxRoot =
      tree.root
        .asBfsIterable()
        .find { it.pathComponent.startsWith("androidx") }
          ?: throw TODO() // Need to decide exception to throw if androidx isn't found

    Single.just(tree.copy(root = androidxRoot))
  }

  return androidxTree.map(GroupPathTree::generateClasses)
}

