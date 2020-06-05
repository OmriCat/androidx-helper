package com.omricat.androidxdash.codegen

import com.omricat.androidxdash.Group
import com.squareup.kotlinpoet.*
import org.gradle.api.artifacts.dsl.DependencyHandler

private const val packageName: String = "com.omricat.androidxhelper"

private fun FileSpec.Builder.nothingToInline() = addAnnotation(
    AnnotationSpec.builder(Suppress::class)
        .addMember(CodeBlock.of("%S", "NOTHING_TO_INLINE"))
        .useSiteTarget(AnnotationSpec.UseSiteTarget.FILE)
        .build()
)


fun generateClass(tree: GroupPathTree): Set<FileSpec> {
    val rootFile: FileSpec.Builder = FileSpec.builder(packageName, "Dependencies.kt")
//        .nothingToInline()

    val root = tree.root
    val rootClassName = ClassName(packageName, root.pathComponent.capitalize())

    val rootFileClassBuilder = TypeSpec.classBuilder(rootClassName)
        .internalConstructor()

    rootFileClassBuilder.addProperty(prefixProperty(root.pathComponent))

    val childrenFiles = root.children.map {
        it.run {

            rootFileClassBuilder.addProperty(generateProperty())
            generateChildOfRootFile()
        }
    }

    // TODO: Deal with case where root vertex is ArtifactVertex

    rootFile.addType(rootFileClassBuilder.build())
    val dependencyHandlerExtension = PropertySpec.builder(root.pathComponent, rootClassName)
        .receiver(DependencyHandler::class)
        .initializer("%T()", rootClassName)
        .build()

    rootFile.addProperty(dependencyHandlerExtension)
    return (listOf(rootFile.build()) + childrenFiles).toSet()
}


internal fun Vertex<Group>.generateChildOfRootFile(): FileSpec {
    val fileBuilder = FileSpec.builder(packageName, pathComponent.capitalize())
    val thisClass = generateClass()
    fileBuilder.addType(thisClass)
    return fileBuilder.build()
}

internal fun Vertex<Group>.generateClass(): TypeSpec = when (this) {
    is Vertex.PathVertex -> pathClass(this)
    is Vertex.ArtifactVertex -> artifactClass(this)
}

internal fun artifactClass(artifactVertex: Vertex.ArtifactVertex<Group>): TypeSpec {
    TODO("not implemented")
}

internal fun pathClass(vertex: Vertex.PathVertex<Group>): TypeSpec {
    val className = ClassName(packageName, vertex.pathComponent.capitalize())

    val classBuilder = TypeSpec.classBuilder(className)
        .internalConstructor {
            addParameter("prefix", String::class)
        }

    classBuilder.addProperty(prefixProperty(vertex.pathComponent))

    vertex.children.forEach {
        it.run {
            val childClass = generateClass()
            classBuilder.addType(childClass)
            classBuilder.addProperty(generateProperty())
        }
    }


    return classBuilder.build()

}

private fun prefixProperty(pathComponent: String): PropertySpec {
    return PropertySpec.builder("prefix", String::class)
        .addModifiers(KModifier.PRIVATE)
        .initializer("%P", "\$prefix.$pathComponent")
        .build()
}

private fun Vertex<Group>.generateProperty(): PropertySpec {
    val className = ClassName(packageName, pathComponent.capitalize())
    return PropertySpec.builder(pathComponent, className)
        .initializer("%T(prefix)", className)
        .build()
}

private fun TypeSpec.Builder.internalConstructor(block: FunSpec.Builder.() -> FunSpec.Builder = { this }): TypeSpec.Builder =
    primaryConstructor(FunSpec.constructorBuilder().addModifiers(KModifier.INTERNAL).run(block).build())
