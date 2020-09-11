@file:Suppress("NOTHING_TO_INLINE")

package com.omricat.androidxhelperplugin

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.combine
import com.omricat.androidxhelperplugin.converters.ConvertersFactory
import io.reactivex.rxjava3.annotations.NonNull
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
  fun groupsIndex(): Single<Result<GroupsList, Any>>

  @GET("{group}/group-index.xml")
  fun group(
    @Path(
      "group",
      encoded = true
    ) group: GroupName
  ): Single<Result<Group, Any>>

  companion object {

    fun instance(baseUrl: String = "https://dl.google.com/dl/android/maven2/"): GoogleMaven =
      Retrofit.Builder()
        .baseUrl(baseUrl)
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .addConverterFactory(ConvertersFactory)
        .build()
        .create()
  }
}

fun Collection<GroupName>.getDetails(service: GoogleMaven = GoogleMaven.instance()): @NonNull Single<Result<List<Group>, Any>> =
  toObservable()
    .buffer(5)
    .flatMap({ groupsBuffer: List<GroupName> ->
               groupsBuffer
                 .toObservable()
//                 .doOnEach { Logger.info { "Requesting $it" } }
                 .delay(50, TimeUnit.MILLISECONDS)
                 .flatMapSingle({ groupName -> service.group(groupName) }, true)
             }, true)
    .toList()
    .map { results -> results.combine() }


