package com.qiyang.a202302010430.billtandemo.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * 预算实体类
 */
@Entity(tableName = "budgets")
data class Budget(
    // 主键，自动增长
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    // 月份（格式：yyyy-MM）
    val month: String,
    // 预算金额
    val amount: Double,
    // 已使用金额
    val usedAmount: Double = 0.0,
    // 创建时间
    val createTime: Date = Date(),
    // 更新时间
    val updateTime: Date = Date()
)