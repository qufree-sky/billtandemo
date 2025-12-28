package com.qiyang.a202302010430.billtandemo.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.qiyang.a202302010430.billtandemo.data.entity.Budget

/**
 * 预算数据访问对象
 */
@Dao
interface BudgetDao {
    // 插入预算
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(budget: Budget)
    
    // 更新预算
    @Update
    suspend fun update(budget: Budget)
    
    // 删除预算
    @Delete
    suspend fun delete(budget: Budget)
    
    // 根据ID删除预算
    @Query("DELETE FROM budgets WHERE id = :id")
    suspend fun deleteById(id: Long)
    
    // 获取所有预算
    @Query("SELECT * FROM budgets ORDER BY month DESC")
    suspend fun getAllBudgets(): List<Budget>
    
    // 根据月份获取预算
    @Query("SELECT * FROM budgets WHERE month = :month LIMIT 1")
    suspend fun getBudgetByMonth(month: String): Budget?
    
    // 更新已使用金额
    @Query("UPDATE budgets SET usedAmount = :usedAmount, updateTime = CURRENT_TIMESTAMP WHERE month = :month")
    suspend fun updateUsedAmount(month: String, usedAmount: Double)
}