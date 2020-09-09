package com.omricat.androidxdash.codegen

import com.github.michaelbull.result.get
import com.omricat.androidxdash.GroupsList
import io.kotest.core.spec.style.StringSpec

internal class StructureTreeTest : StringSpec({

    "tree stackoverflow test" {

        val masterIndex = StructureTreeTest::class.java.getResource("/master-index.xml").readText()

        val groupsList = GroupsList.parseFromString(masterIndex).get()?.groups ?: emptySet()


    }


})


