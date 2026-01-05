package com.qiyang.a202302010430.billtandemo.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.qiyang.a202302010430.billtandemo.data.entity.BillRecord
import java.util.Date

/**
 * 记账记录数据访问对象
 */
@Dao
interface BillRecordDao {
    // 插入记账记录
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: BillRecord)
    
    // 批量插入记账记录
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(records: List<BillRecord>)
    
    // 更新记账记录
    @Update
    suspend fun update(record: BillRecord)
    
    // 删除记账记录
    @Delete
    suspend fun delete(record: BillRecord)
    
    // 根据ID删除记账记录
    @Query("DELETE FROM bill_records WHERE id = :id")
    suspend fun deleteById(id: Long)
    
    // 获取所有记账记录
    @Query("SELECT * FROM bill_records ORDER BY date DESC")
    suspend fun getAllRecords(): List<BillRecord>
    
    // 根据类型获取记账记录
    @Query("SELECT * FROM bill_records WHERE type = :type ORDER BY date DESC")
    suspend fun getRecordsByType(type: Int): List<BillRecord>
    
    // 根据日期范围获取记账记录
    @Query("SELECT * FROM bill_records WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    suspend fun getRecordsByDateRange(startDate: Long, endDate: Long): List<BillRecord>
    
    // 根据月份获取记账记录（格式：yyyy-MM）
    @Query("SELECT * FROM bill_records WHERE strftime('%Y-%m', date/1000, 'unixepoch') = :month ORDER BY date DESC")
    suspend fun getRecordsByMonth(month: String): List<BillRecord>
    
    // 根据周获取记账记录
    @Query("SELECT * FROM bill_records WHERE strftime('%Y-%W', date/1000, 'unixepoch') = :yearWeek ORDER BY date DESC")
    suspend fun getRecordsByWeek(yearWeek: String): List<BillRecord>
    
    // 获取指定日期的记账记录
    @Query("SELECT * FROM bill_records WHERE strftime('%Y-%m-%d', date/1000, 'unixepoch') = :date ORDER BY date DESC")
    suspend fun getRecordsByDate(date: String): List<BillRecord>
    
    // 根据分类获取记账记录
    @Query("SELECT * FROM bill_records WHERE category = :category ORDER BY date DESC")
    suspend fun getRecordsByCategory(category: String): List<BillRecord>
    
    // 获取指定月份的总收入
    @Query("SELECT SUM(amount) FROM bill_records WHERE type = 1 AND strftime('%Y-%m', date/1000, 'unixepoch') = :month")
    suspend fun getMonthlyIncome(month: String): Double?
    
    // 获取指定月份的总支出
    @Query("SELECT SUM(amount) FROM bill_records WHERE type = 2 AND strftime('%Y-%m', date/1000, 'unixepoch') = :month")
    suspend fun getMonthlyExpense(month: String): Double?
    
    // 获取指定周的总收入
    @Query("SELECT SUM(amount) FROM bill_records WHERE type = 1 AND strftime('%Y-%W', date/1000, 'unixepoch') = :yearWeek")
    suspend fun getWeeklyIncome(yearWeek: String): Double?
    
    // 获取指定周的总支出
    @Query("SELECT SUM(amount) FROM bill_records WHERE type = 2 AND strftime('%Y-%W', date/1000, 'unixepoch') = :yearWeek")
    suspend fun getWeeklyExpense(yearWeek: String): Double?
    
    // 获取指定月份各分类的支出金额
    @Query("SELECT category, SUM(amount) AS total FROM bill_records WHERE type = 2 AND strftime('%Y-%m', date/1000, 'unixepoch') = :month GROUP BY category")
    suspend fun getMonthlyExpenseByCategory(month: String): List<CategoryAmount>
    
    // 获取指定周各分类的支出金额
    @Query("SELECT category, SUM(amount) AS total FROM bill_records WHERE type = 2 AND strftime('%Y-%W', date/1000, 'unixepoch') = :yearWeek GROUP BY category")
    suspend fun getWeeklyExpenseByCategory(yearWeek: String): List<CategoryAmount>
    
    // 分类金额数据类
    data class CategoryAmount(val category: String, val total: Double)
}