package com.qiyang.a202302010430.billtandemo.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * 记账记录实体类
 */
@Entity(tableName = "bill_records")
data class BillRecord(
    // 主键，自动增长
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    // 金额
    val amount: Double,
    // 类型：收入或支出（1：收入，2：支出）
    val type: Int,
    // 分类
    val category: String,
    // 备注
    val remark: String,
    // 日期时间
    val date: Date,
    // 创建时间
    val createTime: Date = Date()
)