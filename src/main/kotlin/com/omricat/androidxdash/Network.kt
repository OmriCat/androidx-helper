package com.omricat.androidxdash

import com.github.michaelbull.result.Result
import com.omricat.androidxdash.converters.ConvertersFactory
import io.reactivex.rxjava3.core.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Path

interface GoogleMaven {
    @GET("master-index.xml")
    fun groupsIndex(): Single<Result<GroupsDoc, Any>>

    @GET("{group}/group-index.xml")
    fun group(@Path("group", encoded = true) group: GroupName): Single<Result<Group, Any>>

    companion object {
        fun instance(): GoogleMaven =
            Retrofit.Builder()
                .baseUrl("https://dl.google.com/dl/android/maven2/")
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .addConverterFactory(ConvertersFactory)
                .build()
                .create()
    }
}

