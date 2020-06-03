package com.omricat.androidxdash.codegen

import com.omricat.androidxdash.Group
import com.omricat.androidxdash.GroupName
import com.squareup.kotlinpoet.*

private fun FileSpec.Builder.nothingToInline() = addAnnotation(
    AnnotationSpec.builder(Suppress::class)
        .addMember(CodeBlock.of("%S", "NOTHING_TO_INLINE"))
        .useSiteTarget(AnnotationSpec.UseSiteTarget.FILE)
        .build()
)


fun generateCode(group: Group): FileSpec {
    val fileName = group.groupName.hierarchy().first()
    val builder = FileSpec.builder(
            "com.omricat.package???",
            fileName
        ).nothingToInline()
        .apply { TODO() }
    return builder.build()
}


fun GroupName.hierarchy(): List<String> =
    name.split(".")
