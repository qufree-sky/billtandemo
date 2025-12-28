package com.qiyang.a202302010430.billtandemo

import android.app.Application
import com.qiyang.a202302010430.billtandemo.data.database.DatabaseInitializer

/**
 * 自定义Application类，用于初始化应用程序级别的组件
 */
class MyApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // 初始化数据库默认数据
        DatabaseInitializer.initialize(this)
    }
}