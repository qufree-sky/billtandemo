package com.qiyang.a202302010430.billtandemo.data.database

import androidx.room.TypeConverter
import java.util.Date

/**
 * Date类型转换器，用于Room数据库存储Date类型
 */
class DateTypeConverterg {
    /**
     * 将Date转换为Long（时间戳）
     */
    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }
    
    /**
     * 将Long（时间戳）转换为Date
     */
    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        return timestamp?.let { Date(it) }
    }
}
