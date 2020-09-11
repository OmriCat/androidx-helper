package com.omricat.androidxhelperplugin.codegen

import com.github.michaelbull.result.get
import com.omricat.androidxhelperplugin.GroupsList
import io.kotest.core.spec.style.StringSpec

internal class StructureTreeTest : StringSpec({

    "tree stackoverflow test" {

        val masterIndex = StructureTreeTest::class.java.getResource("/master-index.xml").readText()

        val groupsList = GroupsList.parseFromString(masterIndex).get()?.groups ?: emptySet()


    }


})


