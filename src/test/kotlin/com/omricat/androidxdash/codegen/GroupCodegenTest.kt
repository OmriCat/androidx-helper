package com.omricat.androidxdash.codegen

import com.omricat.androidxdash.Group
import com.omricat.androidxdash.GroupName
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

internal class GroupCodegenTest : StringSpec({

    "Correct hierarchy for nested groupname" {
        val group = Group(groupName = GroupName("level1.level2"), emptyMap())


        val generatedCode = generateCode(group).toString()

        val expectedCode = """|@file:Suppress("NOTHING_TO_INLINE")
            |
            |package com.omricat.nofuckingclue
            |
            |class Level1 internal constructor() {
            |    private val groupPrefix = "level1"
            |    val level2 = Level2(groupPrefix)
            |    
            |    class Level2 internal constructor(groupPrefix: String) {
            |        private val group = "${'$'}groupPrefix.level2"
            |    }
            |}
        """.trimMargin()

        generatedCode.shouldBe(expectedCode)

    }

})
