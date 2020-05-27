package com.omricat.androidxdash

import com.github.michaelbull.result.Result
import com.omricat.androidxdash.converters.ConvertersFactory
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.toObservable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

interface GoogleMaven {
    @GET("master-index.xml")
    fun groupsIndex(): Single<Result<Groups, Any>>

    @GET("{group}/group-index.xml")
    fun group(@Path("group", encoded = true) group: GroupName): Single<Result<Group, Any>>

    companion object {
        fun instance(): GoogleMaven = instance

        private val instance by lazy {
            Retrofit.Builder()
                .baseUrl("https://dl.google.com/dl/android/maven2/")
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .addConverterFactory(ConvertersFactory)
                .build()
                .create<GoogleMaven>()
        }


    }
}

fun Collection<GroupName>.getDetails(service: GoogleMaven = GoogleMaven.instance()) =
        toObservable()
        .buffer(5)
        .flatMap({ groupsList ->
            groupsList
                .toObservable()
                .doOnEach { println("Requesting $it") }
                .delay(50, TimeUnit.MILLISECONDS)
                .flatMapSingle({ groupName -> service.group(groupName) }, true)
        }, true)



