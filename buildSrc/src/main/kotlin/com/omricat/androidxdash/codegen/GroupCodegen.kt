package com.omricat.androidxdash.codegen

import com.omricat.androidxdash.Group
import com.omricat.androidxdash.camelCase
import com.squareup.kotlinpoet.*
import org.gradle.api.artifacts.dsl.DependencyHandler

private fun FileSpec.Builder.nothingToInline() = addAnnotation(
  AnnotationSpec.builder(Suppress::class)
    .addMember(CodeBlock.of("%S", "NOTHING_TO_INLINE"))
    .useSiteTarget(AnnotationSpec.UseSiteTarget.FILE)
    .build()
)

private const val INSTANCE = "instance"

fun GroupPathTree.generateClasses(): Set<FileSpec> {
  val packageName = packageName

  val rootFile: FileSpec.Builder = FileSpec.builder(packageName, "Dependencies")
//        .nothingToInline()

  val rootClassName =
    ClassName(packageName, root.toSimpleClassName())

  val rootClassBuilder = TypeSpec.classBuilder(rootClassName)
    .internalConstructor()

  val artifactVertexValueBuilder: (builder: TypeSpec.Builder, value: Group) -> TypeSpec.Builder =
    { builder, group ->
      val functions: Collection<FunSpec> =
        group.artifacts.map { artifact ->
          FunSpec.builder(artifact.name.camelCase())
            .addParameter("version", String::class)
            .returns(String::class)
            .addStatement(
              "return %P",
              dependencySpec(group.groupName, artifact, "${'$'}version")
            )
            .build()
        }
      builder.addFunctions(functions)
      builder
    }

  root.children.forEach { vertex ->
    val className = ClassName(packageName, vertex.toSimpleClassName())
    rootClassBuilder.addProperty(generateProperty(className))
  }

  val childrenFiles: List<FileSpec> = root.children.map { vertex ->
    generateFirstLevelClass(vertex, packageName, artifactVertexValueBuilder)
      .let { FileSpec.get(packageName, it) }
  }

  // TODO: Deal with case where root vertex is ArtifactVertex

  rootFile.addType(rootClassBuilder.build())

  val instanceProperty =
    PropertySpec.builder(INSTANCE, rootClassName, KModifier.PRIVATE)
      .delegate("lazy { %T() }", rootClassName)
      .build()
  rootFile.addProperty(instanceProperty)

  val dependencyHandlerExtension =
    PropertySpec.builder(this.root.pathComponent, rootClassName)
      .receiver(DependencyHandler::class)
      .getter(
        FunSpec.getterBuilder().addStatement("return $INSTANCE").build()
      )
      .build()

  rootFile.addProperty(dependencyHandlerExtension)
  return (listOf(rootFile.build()) + childrenFiles).toSet()
}

internal fun <T> generateFirstLevelClass(
  vertex: Vertex<T>,
  packageName: String,
  artifactVertexValueBuilder: (TypeSpec.Builder, T) -> TypeSpec.Builder
): TypeSpec =
  generateClass(
    ClassName(packageName, vertex.toSimpleClassName()),
    vertex,
    artifactVertexValueBuilder
  )

internal fun <T> generateChildClass(
  vertex: Vertex<T>,
  parentClassName: ClassName,
  artifactVertexValueBuilder: (TypeSpec.Builder, T) -> TypeSpec.Builder
): TypeSpec = generateClass(
  ClassName(
    parentClassName.packageName,
    parentClassName.simpleNames + vertex.toSimpleClassName()
  ), vertex, artifactVertexValueBuilder
)

internal fun <T> generateClass(
  className: ClassName,
  vertex: Vertex<T>,
  artifactVertexValueBuilder: (TypeSpec.Builder, T) -> TypeSpec.Builder
): TypeSpec {
  val classBuilder =
    TypeSpec.classBuilder(className).internalConstructor()

  vertex.children.addAllToParentClass(
    classBuilder,
    className,
    artifactVertexValueBuilder
  )

  val finalBuilder = when (vertex) {
    is Vertex.PathVertex -> classBuilder
    is Vertex.ArtifactVertex -> artifactVertexValueBuilder(
      classBuilder,
      vertex.value
    )
  }

  return finalBuilder.build()
}


internal fun <T> Collection<Vertex<T>>.addAllToParentClass(
  builder: TypeSpec.Builder,
  parentClass: ClassName,
  artifactVertexValueBuilder: (TypeSpec.Builder, T) -> TypeSpec.Builder
) =
  forEach { childVertex ->
    val childClass = generateChildClass(
      childVertex,
      parentClass,
      artifactVertexValueBuilder
    )
    builder.addType(childClass)
    val childClassName = ClassName(
      parentClass.packageName,
      parentClass.simpleNames + childVertex.toSimpleClassName()
    )
    builder.addProperty(generateProperty(childClassName))
  }


private fun <T> Vertex<T>.toSimpleClassName() =
  pathComponent.capitalize()

private fun dependencySpec(
  group: CharSequence,
  artifact: CharSequence,
  @Suppress("SameParameterValue") version: CharSequence
) =
  "$group:$artifact:$version"

private fun generateProperty(
  className: ClassName
): PropertySpec {

  return PropertySpec.builder(className.simpleName.decapitalize(), className)
    .initializer("%T()", className)
    .build()
}

private fun TypeSpec.Builder.internalConstructor(block: FunSpec.Builder.() -> FunSpec.Builder = { this }): TypeSpec.Builder =
  primaryConstructor(
    FunSpec.constructorBuilder().addModifiers(KModifier.INTERNAL).run(block)
      .build()
  )
