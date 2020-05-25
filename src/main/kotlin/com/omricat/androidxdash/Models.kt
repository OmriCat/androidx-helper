package com.omricat.androidxdash

import com.github.michaelbull.result.*
import com.omricat.asElementList
import com.omricat.document
import org.w3c.dom.Document


data class GroupName(val name: String)

fun GroupName.asPath() = name.replace(oldValue = ".", newValue = "/")

inline class Artifact(val name: String)

inline class Version(val versionString: String)

data class GroupsDoc(private val document: Document) {

    val groups: Set<GroupName>
        get() = document.documentElement.run {
                normalize()
                childNodes.asElementList()
            }
            .map { GroupName(it.nodeName) }
            .toSet()
    companion object {
        fun parseFromString(string: String): Result<GroupsDoc, ParseError> = document(string)
            .mapError { ParseError(it.message ?: "Unknown error", string) }
            .flatMap { document ->
                if (document.documentElement.tagName == "metadata")
                    Ok(GroupsDoc(document))
                else
                    Err(ParseError("Missing metadata root element", string))
            }

    }
    data class ParseError(val message: String, val input: String)

}
data class Group(val name: GroupName, val artifactsToVersions: Map<Artifact, List<Version>>) {

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
                    }.toMap()
                Ok(Group(GroupName(doc.documentElement.tagName), artifacts))
            }

    }
    data class ParseError(val message: String, val input: String)

}
