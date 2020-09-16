package com.omricat.androidxhelperplugin.converters

import com.omricat.androidxhelperplugin.Group
import com.omricat.androidxhelperplugin.GroupName
import com.omricat.androidxhelperplugin.GroupsList
import com.omricat.androidxhelperplugin.asPath
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

object ConvertersFactory : Converter.Factory() {

  override fun responseBodyConverter(
    type: Type,
    annotations: Array<Annotation>,
    retrofit: Retrofit
  ): Converter<ResponseBody, *>? = when (type) {
    Group::class.java -> GroupConverter()
    GroupsList::class.java -> GroupsDocConverter()
    else -> null
  }

  override fun stringConverter(
    type: Type,
    annotations: Array<Annotation>,
    retrofit: Retrofit
  ): Converter<*, String>? =
    if (type == GroupName::class.java)
      GroupNameConverter()
    else
      null
}

class GroupConverter : Converter<ResponseBody, Group> {
  override fun convert(body: ResponseBody): Group =
    Group.parseFromString(body.string())
}

class GroupsDocConverter : Converter<ResponseBody, GroupsList> {
  override fun convert(body: ResponseBody): GroupsList =
    GroupsList.parseFromString(body.string())
}

class GroupNameConverter : Converter<GroupName, String> {
  override fun convert(value: GroupName): String = value.asPath()
}

