package com.omricat.androidxdash.codegen

import com.omricat.androidxdash.Group
import com.squareup.kotlinpoet.*
import org.gradle.api.artifacts.dsl.DependencyHandler

private fun FileSpec.Builder.nothingToInline() = addAnnotation(
    AnnotationSpec.builder(Suppress::class)
        .addMember(CodeBlock.of("%S", "NOTHING_TO_INLINE"))
        .useSiteTarget(AnnotationSpec.UseSiteTarget.FILE)
        .build()
)


fun GroupPathTree.generateClasses(): Set<FileSpec> {
    val packageName = packageName

    val rootFile: FileSpec.Builder = FileSpec.builder(packageName, "Dependencies.kt")
//        .nothingToInline()

    val rootClassName = ClassName(packageName, this.root.pathComponent.capitalize())

    val rootClassBuilder = TypeSpec.classBuilder(rootClassName)
        .internalConstructor()

    val artifactVertexValueBuilder: (builder: TypeSpec.Builder, value: Group) -> TypeSpec.Builder =
        { builder, group -> artifactVertexGroupBuilder(builder, group) }
    val childrenFiles = this.root.children
        .onEach { vertex -> rootClassBuilder.addProperty(vertex.generateProperty(packageName)) }
        .map { vertex -> vertex.generateChildFileOfRoot(packageName, artifactVertexValueBuilder) }

    // TODO: Deal with case where root vertex is ArtifactVertex

    rootFile.addType(rootClassBuilder.build())
    val dependencyHandlerExtension = PropertySpec.builder(this.root.pathComponent, rootClassName)
        .receiver(DependencyHandler::class)
        .initializer("%T()", rootClassName)
        .build()

    rootFile.addProperty(dependencyHandlerExtension)
    return (listOf(rootFile.build()) + childrenFiles).toSet()
}


internal fun <T> Vertex<T>.generateChildFileOfRoot(
    packageName: String,
    block: (builder: TypeSpec.Builder, value: T) -> TypeSpec.Builder
): FileSpec {
    val fileBuilder = FileSpec.builder(packageName, pathComponent.capitalize())
    val thisClass = generateClass(this, packageName, block)
    fileBuilder.addType(thisClass)
    return fileBuilder.build()
}


internal fun <T> generateClass(
    vertex: Vertex<T>,
    packageName: String,
    artifactVertexValueBuilder: (TypeSpec.Builder, T) -> TypeSpec.Builder
): TypeSpec {
    val className = ClassName(packageName, vertex.pathComponent.capitalize())

    val classBuilder = TypeSpec.classBuilder(className).internalConstructor()

    vertex.children.forEach { childVertex ->
        val childClass = generateClass(childVertex, packageName, artifactVertexValueBuilder)
        classBuilder.addType(childClass)
        classBuilder.addProperty(childVertex.generateProperty(packageName))
    }

    val finalBuilder = when (vertex) {
        is Vertex.PathVertex -> classBuilder
        is Vertex.ArtifactVertex -> artifactVertexValueBuilder(classBuilder, vertex.value)
    }

    return finalBuilder.build()
}

private fun artifactVertexGroupBuilder(builder: TypeSpec.Builder, group: Group): TypeSpec.Builder {
    val functions: Collection<FunSpec> =
        group.artifacts.map { artifact ->
            FunSpec.builder(artifact.name)
                .addParameter("version", String::class)
                .returns(String::class)
                .addStatement("return %P", dependencySpec(group.groupName, artifact, "${'$'}version"))
                .build()
        }
    builder.addFunctions(functions)
    return builder
}

private fun dependencySpec(
    group: CharSequence,
    artifact: CharSequence,
    @Suppress("SameParameterValue") version: CharSequence
) =
    "$group:$artifact:$version"

private fun <T> Vertex<T>.generateProperty(packageName: String): PropertySpec {
    val className = ClassName(packageName, pathComponent.capitalize())
    return PropertySpec.builder(pathComponent, className)
        .initializer("%T()", className)
        .build()
}

private fun TypeSpec.Builder.internalConstructor(block: FunSpec.Builder.() -> FunSpec.Builder = { this }): TypeSpec.Builder =
    primaryConstructor(FunSpec.constructorBuilder().addModifiers(KModifier.INTERNAL).run(block).build())
