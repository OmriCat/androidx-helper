package com.omricat.androidxhelperplugin

import com.omricat.asElementList
import com.omricat.document
import com.omricat.normalized


data class GroupName(val name: String) : CharSequence by name

fun GroupName.asPath(): String = name.replace(oldValue = ".", newValue = "/")

data class Artifact(val name: String) : CharSequence by name

data class Version(val versionString: String) : CharSequence by versionString

class GroupsList private constructor(val groups: Set<GroupName>) {

  companion object {
    fun parseFromString(string: String): GroupsList {
      val documentElement = document(string).documentElement
      if (documentElement.tagName != "metadata")
        throw MetadataElementMissingException(string)
      return GroupsList(
        documentElement.normalized()
          .childNodes.asElementList()
          .map { GroupName(it.nodeName) }
          .toSet()
      )
    }
  }
}

data class Group(
  val groupName: GroupName,
  val artifactsToVersions: Map<Artifact, List<Version>>
) {

  constructor(
    groupName: String,
    artifactsToVersions: Map<Artifact, List<Version>>
  ) : this(
    GroupName(groupName),
    artifactsToVersions
  )

  val artifacts: Set<Artifact> get() = artifactsToVersions.keys

  operator fun get(artifact: Artifact): List<Version> =
    artifactsToVersions[artifact] ?: emptyList()

  companion object {
    fun parseFromString(string: String): Group {
      val documentElement = document(string).documentElement
      val artifacts = documentElement.childNodes.asElementList()
        .map { elt ->
          Artifact(elt.tagName) to
              elt.getAttribute("versions").split(",")
                .map(::Version)
        }
        .sortedBy { it.first.name }
        .toMap()

      return Group(GroupName(documentElement.tagName), artifacts)
    }

  }

}

data class MetadataElementMissingException(val input: String) : RuntimeException()

