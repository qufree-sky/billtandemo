package com.qiyang.a202302010430.billtandemo.data.database

import androidx.room.TypeConverter
import java.util.Date

/**
 * 类型转换器，用于Room数据库处理Date类型
 */
class TypeConverters {
    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun toDate(millisSinceEpoch: Long?): Date? {
        return millisSinceEpoch?.let { Date(it) }
    }
}
