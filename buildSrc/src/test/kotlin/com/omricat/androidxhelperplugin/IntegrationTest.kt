package com.omricat.androidxhelperplugin

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.should
import io.reactivex.rxjava3.core.Single

class IntegrationTest : StringSpec(
  {
    "FileSpecs from androidx real data" should {
      val fileSpecs =
        generateFileSpecs("com.omricat.androidxplugin", TestService)

      "result in no errors" {
        val testObserver = fileSpecs.test()
        assertSoftly {
          testObserver.assertComplete()
          testObserver.assertNoErrors()
        }
      }
    }
  }
)

object TestService : GoogleMaven {
  override fun groupsIndex(): Single<GroupsList> = Single.just(
    GroupsList.parseFromString(masterIndex)
  )

  override fun group(group: GroupName): Single<Group> =
    Single.just(
      when (group.name) {
        "androidx.test" -> Group.parseFromString(androidxTest)
        "androidx.test.espresso" -> Group.parseFromString(androidxTestEspresso)
        "androidx.test.espresso.idling" -> Group.parseFromString(
          androidxTestEspressoIdling
        )
        else -> throw AssertionError()
      }
    )
}

const val masterIndex = """<metadata>
<androidx.test/>
<androidx.test.espresso/>
<androidx.test.espresso.idling/>
</metadata>
"""

const val androidxTest = """
<androidx.test>
<core versions="1.0.0-alpha2,1.0.0-alpha3,1.0.0-alpha4"/>
<core-ktx versions="1.1.0-alpha01,1.1.0-beta01,1.1.0"/>
<monitor versions="1.1.0-alpha1,1.1.0-alpha2,1.1.0-alpha3"/>
<orchestrator versions="1.1.0-alpha1,1.1.0-alpha2,1.1.0-alpha3"/>
<rules versions="1.1.0-alpha1,1.1.0-alpha2,1.1.0-alpha3,1.1.0-alpha4"/>
<runner versions="1.1.0-alpha1,1.1.0-alpha2,1.1.0-alpha3,1.1.0-alpha4"/>
</androidx.test>  
"""

const val androidxTestEspresso = """
<androidx.test.espresso>
<espresso-contrib versions="3.1.0-alpha1,3.1.0-alpha2,3.1.0-alpha3,3.1.0-alpha4"/>
<espresso-core versions="3.1.0-alpha1,3.1.0-alpha2,3.1.0-alpha3"/>
<espresso-idling-resource versions="3.1.0-alpha1,3.1.0-alpha2"/>
<espresso-intents versions="3.1.0-alpha1,3.1.0-alpha2,3.1.0-alpha3"/>
<espresso-remote versions="3.1.0-alpha1,3.1.0-alpha2,3.1.0-alpha3"/>
<espresso-web versions="3.1.0-alpha1,3.1.0-alpha2,3.1.0-alpha3"/>
</androidx.test.espresso>
"""

const val androidxTestEspressoIdling = """
<androidx.test.espresso.idling>
<idling-concurrent versions="3.1.0-alpha1,3.1.0-alpha2,3.1.0-alpha3"/>
<idling-net versions="3.1.0-alpha1,3.1.0-alpha2,3.1.0-alpha3,3.1.0-alpha4"/>
</androidx.test.espresso.idling>  
"""

