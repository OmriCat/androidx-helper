package com.omricat.androidxhelperplugin

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

internal class ModelsTest : StringSpec(
  {

    "Groups#fromString should parse empty list" {
      GroupsList.parseFromString(TestData.emptyGroupsListXml).groups.shouldBeEmpty()
    }

    "Groups#fromString should parse real xml" {
      GroupsList.parseFromString(TestData.groupsXml).groups.shouldContainExactly(
        GroupName("com.android.support.constraint"),
        GroupName("com.android.databinding"),
        GroupName("com.android.support")
      )
    }

    "Group#fromString should parse real data" {

      val group = Group.parseFromString(TestData.androidXUiGroupXml)
      group.groupName.shouldBe(GroupName("androidx.ui"))
      group.artifacts.size.shouldBe(24)
      val expectedUiLivedataVersions =
        listOf(
          "0.1.0-dev09",
          "0.1.0-dev10",
          "0.1.0-dev11"
        ).map(::Version)
      group[Artifact("ui-livedata")].shouldContainExactly(
        expectedUiLivedataVersions
      )
    }
  })

fun beOk() = object : Matcher<Result<*, *>> {
  override fun test(value: Result<*, *>) =
    MatcherResult(
      value is Ok<*>,
      "Result $value should be Ok",
      "Result $value should be Err"
    )
}

inline fun <V> Result<V, Any>.shouldBeOk(block: (V) -> Unit = {}) {
  this should beOk()
  this.onSuccess(block)
}
