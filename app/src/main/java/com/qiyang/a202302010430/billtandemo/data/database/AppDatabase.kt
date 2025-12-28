package com.qiyang.a202302010430.billtandemo.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.qiyang.a202302010430.billtandemo.data.dao.BillCategoryDao
import com.qiyang.a202302010430.billtandemo.data.dao.BillRecordDao
import com.qiyang.a202302010430.billtandemo.data.dao.BudgetDao
import com.qiyang.a202302010430.billtandemo.data.entity.BillCategory
import com.qiyang.a202302010430.billtandemo.data.entity.BillRecord
import com.qiyang.a202302010430.billtandemo.data.entity.Budget

/**
 * Room数据库实例
 */
@TypeConverters(DateTypeConverter::class)
@Database(
    entities = [BillRecord::class, BillCategory::class, Budget::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    // 记账记录DAO
    abstract fun billRecordDao(): BillRecordDao
    
    // 记账分类DAO
    abstract fun billCategoryDao(): BillCategoryDao
    
    // 预算DAO
    abstract fun budgetDao(): BudgetDao
    
    companion object {
        // 数据库名称
        private const val DATABASE_NAME = "orange_bill.db"
        
        // 单例实例
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        /**
         * 获取数据库实例
         */
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }
        
        /**
         * 构建数据库
         */
        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            )
            .build()
        }
    }
}