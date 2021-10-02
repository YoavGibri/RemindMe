package com.hirshler.remindme.room

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hirshler.remindme.model.Alert

class Converters {


    @TypeConverter
    fun stringFromAlertList(list: List<Alert>?): String? {
        return list?.let { Gson().toJson(it) }
    }

    @TypeConverter
    fun alertListFromString(value: String?): List<Alert>? {
        val listType = object : TypeToken<List<Alert>>() {}.type
        return value?.let { Gson().fromJson(it, listType) }
    }


}