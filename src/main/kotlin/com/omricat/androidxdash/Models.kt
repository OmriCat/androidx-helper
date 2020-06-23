package com.omricat.androidxdash

import com.github.michaelbull.result.*
import com.omricat.asElementList
import com.omricat.document


data class GroupName(val name: String) : CharSequence by name

fun GroupName.asPath() = name.replace(oldValue = ".", newValue = "/")

data class Artifact(val name: String) : CharSequence by name

data class Version(val versionString: String) : CharSequence by versionString

class GroupsList private constructor(val groups: Set<GroupName>) {

    companion object {
        fun parseFromString(string: String): Result<GroupsList, ParseError> = document(string)
            .mapError { ParseError(it.message ?: "Unknown error", string) }
            .flatMap { document ->
                if (document.documentElement.tagName == "metadata")
                    Ok(
                        GroupsList(document.documentElement.run {
                                normalize()
                                childNodes.asElementList()
                            }.map { GroupName(it.nodeName) }
                            .toSet())
                    )
                else
                    Err(ParseError("Missing metadata root element", string))
            }

    }

    data class ParseError(val message: String, val input: String)

}

data class Group(val groupName: GroupName, val artifactsToVersions: Map<Artifact, List<Version>>) {

    val artifacts: Set<Artifact> get() = artifactsToVersions.keys

    operator fun get(artifact: Artifact): List<Version> = artifactsToVersions[artifact] ?: emptyList()

    companion object {
        fun parseFromString(string: String): Result<Group, ParseError> = document(string)
            .mapError { ParseError(it.message ?: "Unknown error", string) }
            .flatMap { doc ->
                val artifacts = doc.documentElement.childNodes.asElementList()
                    .map { elt ->
                        Artifact(elt.tagName) to
                                elt.getAttribute("versions").split(",")
                                    .map(::Version)
                    }
                    .sortedBy { it.first.name }
                    .toMap()

                Ok(Group(GroupName(doc.documentElement.tagName), artifacts))
            }

    }

    data class ParseError(val message: String, val input: String)

}

data class GroupsMap(val map: Map<GroupName, Group>)
