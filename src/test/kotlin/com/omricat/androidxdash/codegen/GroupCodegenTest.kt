package com.omricat.androidxdash.codegen

import com.omricat.androidxdash.Group
import com.omricat.androidxdash.GroupName
import com.omricat.androidxdash.codegen.Vertex.ArtifactVertex
import com.omricat.androidxdash.codegen.Vertex.PathVertex
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

const val packageName = "com.omricat.androidxhelper"
const val tripleQuote = "\"\"\""


internal class GroupCodegenTest : StringSpec({


    "Correct hierarchy for simple tree".config(enabled = false) {
        val group = Group(GroupName("groupName"), emptyMap())

        val tree = PathVertex(
            "root",
            listOf(ArtifactVertex("level1", emptyList(), group))
        ).let { GroupPathTree(it) }

        val generatedCode = tree.generateClasses()

        val rootCode = generatedCode.firstOrNull()?.toString() ?: ""

        val expectedCode = """|@file:Suppress("NOTHING_TO_INLINE")
            |
            |package com.omricat.androidxhelper
            |
            |import org.gradle.api.artifacts.dsl.DependencyHandler 
            |
            |class Root internal constructor() {
            |    private val groupPrefix = "root"
            |    val level1 = Level1(groupPrefix)
            |}
            |
            |val DependencyHandler.root = Root()
        """.trimMargin()

        rootCode.shouldBe(expectedCode)

    }

    "simple test of PathVertex generation" {
        val vertex = PathVertex<Group>("root", listOf(PathVertex("level1", emptyList())))

        val generatedCode = pathClass(vertex).toString()

        val expectedCode = """|class Root internal constructor(
            |  prefix: kotlin.String
            |) {
            |  private val prefix: kotlin.String = $tripleQuote${'$'}prefix.root$tripleQuote
            |
            |  val level1: $packageName.Level1 = $packageName.Level1(prefix)
            |
            |  class Level1 internal constructor(
            |    prefix: kotlin.String
            |  ) {
            |    private val prefix: kotlin.String = $tripleQuote${'$'}prefix.level1$tripleQuote
            |  }
            |}
            |
        """.trimMargin()

        generatedCode.shouldBe(expectedCode)

    }

})
