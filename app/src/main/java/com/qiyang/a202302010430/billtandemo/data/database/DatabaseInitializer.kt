package com.qiyang.a202302010430.billtandemo.data.database

import android.content.Context
import com.qiyang.a202302010430.billtandemo.data.entity.BillCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * 数据库初始化类，用于在应用启动时初始化默认数据
 */
object DatabaseInitializer {
    // 收入分类列表
    private val incomeCategories = listOf(
        "工资", "奖金", "投资收益", "兼职收入", "其他收入"
    )
    
    // 支出分类列表
    private val expenseCategories = listOf(
        "餐饮", "交通", "购物", "娱乐", "房租", "水电费", "通讯费", 
        "学习", "医疗", "人情往来", "其他支出"
    )
    
    /**
     * 初始化数据库默认数据
     */
    fun initialize(context: Context) {
        GlobalScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getInstance(context)
            val categoryDao = db.billCategoryDao()
            
            // 检查是否已经有分类数据
            if (categoryDao.getAllCategories().isEmpty()) {
                // 插入默认收入分类
                val incomeCategoryList = incomeCategories.mapIndexed { index, name ->
                    BillCategory(
                        id = index.toLong() + 1,
                        name = name,
                        type = 1, // 1：收入
                        icon = "ic_income_$index"
                    )
                }
                
                // 插入默认支出分类
                val expenseCategoryList = expenseCategories.mapIndexed { index, name ->
                    BillCategory(
                        id = index.toLong() + 100,
                        name = name,
                        type = 2, // 2：支出
                        icon = "ic_expense_$index"
                    )
                }
                
                // 批量插入分类
                categoryDao.insertAll(incomeCategoryList + expenseCategoryList)
            }
        }
    }
}