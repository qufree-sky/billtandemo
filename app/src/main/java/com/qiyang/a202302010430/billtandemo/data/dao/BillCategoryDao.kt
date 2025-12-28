package com.qiyang.a202302010430.billtandemo.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.qiyang.a202302010430.billtandemo.data.entity.BillCategory

/**
 * 记账分类数据访问对象
 */
@Dao
interface BillCategoryDao {
    // 插入分类
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: BillCategory)
    
    // 批量插入分类
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<BillCategory>)
    
    // 更新分类
    @Update
    suspend fun update(category: BillCategory)
    
    // 删除分类
    @Delete
    suspend fun delete(category: BillCategory)
    
    // 根据ID删除分类
    @Query("DELETE FROM bill_categories WHERE id = :id")
    suspend fun deleteById(id: Long)
    
    // 获取所有分类
    @Query("SELECT * FROM bill_categories")
    suspend fun getAllCategories(): List<BillCategory>
    
    // 根据类型获取分类
    @Query("SELECT * FROM bill_categories WHERE type = :type")
    suspend fun getCategoriesByType(type: Int): List<BillCategory>
    
    // 根据名称获取分类
    @Query("SELECT * FROM bill_categories WHERE name = :name AND type = :type LIMIT 1")
    suspend fun getCategoryByName(name: String, type: Int): BillCategory?
}