package com.omricat.androidxdash.converters

import com.omricat.androidxdash.*
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.create

internal class ConvertersFactoryTest : StringSpec({
    fun setup(): Triple<MockWebServer, GoogleMaven, Retrofit> {
        val server = MockWebServer()
        val retrofit = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addCallAdapterFactory(RxJava3CallAdapterFactory.createSynchronous())
            .addConverterFactory(ConvertersFactory)
            .build()
        return Triple(
            server,
            retrofit.create(),
            retrofit
        )
    }

    "GroupsDocConverter integration test happy path" {
        val (server, service) = setup()

        val response = MockResponse().apply {
            setBody(TestData.groupsXml)
        }
        server.enqueue(response)

        val testObserver = service.groupsIndex().test()
        testObserver.assertComplete()
        testObserver.values().shouldHaveSize(1)

        val expectedGroups = listOf(
            "com.android.support.constraint",
            "com.android.databinding",
            "com.android.support"
        ).map(::GroupName)

        testObserver.values().first().shouldBeOk {
            it.groups.shouldHaveSize(3)
            it.groups.shouldContainExactly(expectedGroups)
        }
    }

    "GroupConverter integration test" {
        val (server, service) = setup()

        val response = MockResponse().apply {
            setBody(TestData.androidXUiGroupXml)
        }

        server.dispatcher = object :Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse = when(request.path) {
                "/androidx/ui/group-index.xml" -> response
                else -> MockResponse().setResponseCode(404)
            }
        }

        val testObserver = service.group(GroupName("androidx.ui")).test()
        testObserver.assertComplete()
        testObserver.values().shouldHaveSize(1)

        val expectedGroups = listOf(
            "0.1.0-dev09", "0.1.0-dev10", "0.1.0-dev11"
        ).map(::Version)

        testObserver.values().first().shouldBeOk {
            it.name.shouldBe(GroupName("androidx.ui"))
            it.artifacts.shouldHaveSize(24)
            it.artifactsToVersions[Artifact("ui-text-core")]
                .shouldContainExactly(expectedGroups)
        }
    }
})