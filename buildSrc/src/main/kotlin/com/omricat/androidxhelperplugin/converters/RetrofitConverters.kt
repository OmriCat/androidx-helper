package com.omricat.androidxhelperplugin.converters

import com.github.michaelbull.result.Result
import com.omricat.androidxhelperplugin.Group
import com.omricat.androidxhelperplugin.GroupName
import com.omricat.androidxhelperplugin.GroupsList
import com.omricat.androidxhelperplugin.asPath
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

object ConvertersFactory : Converter.Factory() {

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? = (type as? ParameterizedType)
        ?.takeIf { it.rawType == Result::class.java }
        ?.let {
            when (it.actualTypeArguments[0]) {
                Group::class.java -> GroupConverter()
                GroupsList::class.java -> GroupsDocConverter()
                else -> null
            }
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

class GroupConverter : Converter<ResponseBody, Result<Group, *>> {
    override fun convert(body: ResponseBody): Result<Group, *> = Group.parseFromString(body.string())
}

class GroupsDocConverter : Converter<ResponseBody, Result<GroupsList, *>> {
    override fun convert(body: ResponseBody): Result<GroupsList, *> = GroupsList.parseFromString(body.string())
}

class GroupNameConverter : Converter<GroupName, String> {
    override fun convert(value: GroupName): String = value.asPath()

}
