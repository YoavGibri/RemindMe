package com.hirshler.remindme.room

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {


    @TypeConverter
    fun stringFromWeekDaysMap(list: MutableMap<Int, Boolean>?): String? {
        return list?.let { Gson().toJson(it) }
    }

    @TypeConverter
    fun weekDaysMapFromString(value: String?): MutableMap<Int, Boolean>? {
        val listType = object : TypeToken<MutableMap<Int, Boolean>>() {}.type
        return value?.let { Gson().fromJson(it, listType) }
    }


}