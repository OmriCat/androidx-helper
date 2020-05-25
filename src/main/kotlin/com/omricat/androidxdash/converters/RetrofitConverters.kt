package com.omricat.androidxdash.converters

import com.github.michaelbull.result.Result
import com.omricat.androidxdash.Group
import com.omricat.androidxdash.GroupName
import com.omricat.androidxdash.GroupsDoc
import com.omricat.androidxdash.asPath
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
                GroupsDoc::class.java -> GroupsDocConverter()
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

class GroupsDocConverter : Converter<ResponseBody, Result<GroupsDoc, *>> {
    override fun convert(body: ResponseBody): Result<GroupsDoc, *> = GroupsDoc.parseFromString(body.string())
}

class GroupNameConverter : Converter<GroupName, String> {
    override fun convert(value: GroupName): String = value.asPath()

}
