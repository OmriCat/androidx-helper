package com.omricat.androidxdash.codegen

import com.omricat.androidxdash.Group
import com.omricat.androidxdash.codegen.Vertex.ArtifactVertex
import com.omricat.androidxdash.codegen.Vertex.PathVertex
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe

const val packageName = "com.omricat.androidxplugin"


internal class GroupCodegenTest : StringSpec(
  {


    "Three level tree" should {
      val group1 = Group("groupLevel1", emptyMap())
      val group2 = Group("groupLevel2", emptyMap())

      val tree = PathVertex(
        "root",
        listOf(
          ArtifactVertex(
            "level1", listOf(
              ArtifactVertex("level2", emptyList(), group2)
            ), group1
          )
        )
      ).let { GroupPathTree(it, packageName) }

      val generatedCode =
        tree.generateClasses()

      "produce 2 files" {
        generatedCode.shouldHaveSize(2)
      }

      "produce expected root file" {
        val rootFile = generatedCode.first { it.name == "Dependencies" }
        val expectedCode =
          """package com.omricat.androidxplugin
            |
            |import org.gradle.api.artifacts.dsl.DependencyHandler
            |
            |class Root internal constructor() {
            |  val level1: Level1 = Level1()
            |}
            |
            |private val instance: Root by lazy { Root() }
            |
            |val DependencyHandler.root: Root
            |  get() = instance
            |
        """.trimMargin()

        rootFile.toString().shouldBe(expectedCode)
      }

      "produce expected child file" {
        val level1File = generatedCode.first {it.name == "Level1"}
        val expectedCode =
          """package com.omricat.androidxplugin
            |
            |class Level1 internal constructor() {
            |  val level2: Level2 = Level2()
            |
            |  class Level2 internal constructor()
            |}
            |
          """.trimMargin()

        level1File.toString().shouldBe(expectedCode)
      }


    }

  }
)
