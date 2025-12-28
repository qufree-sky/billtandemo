package com.qiyang.a202302010430.billtandemo.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 记账分类实体类
 */
@Entity(tableName = "bill_categories")
data class BillCategory(
    // 主键，自动增长
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    // 分类名称
    val name: String,
    // 类型：收入或支出（1：收入，2：支出）
    val type: Int,
    // 图标资源名称
    val icon: String
)