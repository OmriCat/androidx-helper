package com.omricat.androidxhelperplugin.codegen

import com.omricat.androidxhelperplugin.Group
import java.util.*

sealed class Vertex<out T> {
    abstract val children: List<Vertex<T>>
    abstract val pathComponent: String

    data class PathVertex<T>(override val pathComponent: String, override val children: List<Vertex<T>>) : Vertex<T>()
    data class ArtifactVertex<T>(
        override val pathComponent: String,
        override val children: List<Vertex<T>>,
        val value: T
    ) : Vertex<T>()
}

fun <T> Vertex<T>.asBfsIterable(): Iterable<Vertex<T>> = Iterable { bfsIterator() }

fun <T> Vertex<T>.bfsIterator(): Iterator<Vertex<T>> = object : AbstractIterator<Vertex<T>>() {

    val queue: Queue<Vertex<T>> = ArrayDeque(listOf(this@bfsIterator))

    override fun computeNext() {
        val next: Vertex<T>? = queue.poll()
        if (next == null)
            done()
        else {
            queue.addAll(next.children)
            setNext(next)
        }
    }
}

fun <S, T> Vertex<S>.map(transform: (S) -> T): Vertex<T> {
    val children = children.map { v -> v.map(transform) }

    return when (this) {
        is Vertex.PathVertex<S> -> Vertex.PathVertex(pathComponent, children)
        is Vertex.ArtifactVertex<S> -> Vertex.ArtifactVertex(pathComponent, children, transform(value))
    }
}

data class GroupPathTree(val root: Vertex<Group>, val packageName: String)

data class GroupHolder(val pathComponent: String, val group: Group)

fun Collection<GroupHolder>.tree(pathComponent: String = ""): Vertex<GroupHolder> {
    val children: List<Vertex<GroupHolder>> = filter { it.pathComponent.isNotBlank() }
        .sortedBy { it.pathComponent }
        .groupBy(
            { it.pathComponent.substringBefore(".") },
            { it.copy(pathComponent = it.pathComponent.substringAfter(".", "")) })
        .map { (path, list) -> list.tree(path) }

    val group = find { it.pathComponent.isBlank() }

    return if (group != null)
        Vertex.ArtifactVertex(pathComponent, children, group)
    else
        Vertex.PathVertex(pathComponent, children)
}

fun Collection<Group>.toPathTree(packageName: String): GroupPathTree =
    GroupPathTree(map { group -> GroupHolder(group.groupName.name, group) }.tree().map { it.group }, packageName)

