package com.omricat.androidxdash

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.onSuccess
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe

internal val groupsXml =
    """<?xml version="1.0" encoding="UTF-8"?>
                <metadata>
                <com.android.support.constraint/>
                <com.android.databinding/>
                <com.android.support/>
                </metadata>
                """.trimIndent()

internal val emptyGroupsListXml = """<?xml version="1.0" encoding="UTF-8"?>
                <metadata>
                </metadata>
                """.trimIndent()

internal class ModelsTest : StringSpec({

    "GroupsDoc#fromString should parse empty list" {
        GroupsDoc.parseFromString(emptyGroupsListXml).shouldBeOk {
            it.groups.shouldBeEmpty()
        }
    }

    "GroupsDoc#fromString should parse real xml" {
        GroupsDoc.parseFromString(groupsXml).shouldBeOk {
            it.groups.shouldContainExactly(
                GroupName("com.android.support.constraint"),
                GroupName("com.android.databinding"),
                GroupName("com.android.support")
            )
        }
    }

    "Group#fromString should parse real data" {

        Group.parseFromString(ModelsTestData.androidXUiGroupXml).shouldBeOk {
            it.name.shouldBe(GroupName("androidx.ui"))
            it.artifacts.size.shouldBe(24)
            val expectedUiLivedataVersions = listOf("0.1.0-dev09","0.1.0-dev10","0.1.0-dev11").map(::Version)
            it[Artifact("ui-livedata")].shouldContainExactly(expectedUiLivedataVersions)
        }
    }
})

fun beOk() = object : Matcher<Result<*, *>> {
    override fun test(value: Result<*, *>) =
        MatcherResult(value is Ok<*>, "Result $value should be Ok", "Result $value should be Err")
}

inline fun <V> Result<V, Any>.shouldBeOk(block: (V) -> Unit = {}) {
    this should beOk()
    this.onSuccess(block)
}
