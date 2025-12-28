package com.qiyang.a202302010430.billtandemo.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.qiyang.a202302010430.billtandemo.data.database.AppDatabase
import com.qiyang.a202302010430.billtandemo.data.entity.BillRecord
import com.qiyang.a202302010430.billtandemo.data.entity.BillCategory
import com.qiyang.a202302010430.billtandemo.data.entity.Budget
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

/**
 * 记账ViewModel，管理记账数据的业务逻辑
 */
class BillViewModel(application: Application) : AndroidViewModel(application) {
    // 数据库实例
    private val database = AppDatabase.getInstance(application)
    
    // 记账记录列表LiveData
    private val _records = MutableLiveData<List<BillRecord>>()
    val records: LiveData<List<BillRecord>> = _records
    
    // 收入分类列表LiveData
    private val _incomeCategories = MutableLiveData<List<BillCategory>>()
    val incomeCategories: LiveData<List<BillCategory>> = _incomeCategories
    
    // 支出分类列表LiveData
    private val _expenseCategories = MutableLiveData<List<BillCategory>>()
    val expenseCategories: LiveData<List<BillCategory>> = _expenseCategories
    
    // 预算LiveData
    private val _currentBudget = MutableLiveData<Budget?>(null)
    val currentBudget: LiveData<Budget?> = _currentBudget
    
    // 当前选择的类型（1：收入，2：支出）
    private val _selectedType = MutableLiveData<Int>(2) // 默认支出
    val selectedType: LiveData<Int> = _selectedType
    
    // 当前选择的分类
    private val _selectedCategory = MutableLiveData<String>()
    val selectedCategory: LiveData<String> = _selectedCategory
    
    // 当前输入的金额
    private val _amount = MutableLiveData<Double>(0.0)
    val amount: LiveData<Double> = _amount
    
    // 当前输入的备注
    private val _remark = MutableLiveData<String>("")
    val remark: LiveData<String> = _remark
    
    // 当前选择的日期
    private val _selectedDate = MutableLiveData<Date>(Date())
    val selectedDate: LiveData<Date> = _selectedDate
    
    // 初始化数据
    init {
        loadCategories()
        loadCurrentMonthBudget()
    }
    
    /**
     * 加载分类列表
     */
    private fun loadCategories() {
        viewModelScope.launch {
            // 加载收入分类
            val incomeCats = database.billCategoryDao().getCategoriesByType(1)
            _incomeCategories.postValue(incomeCats)
            
            // 加载支出分类
            val expenseCats = database.billCategoryDao().getCategoriesByType(2)
            _expenseCategories.postValue(expenseCats)
            
            // 设置默认分类
            if (expenseCats.isNotEmpty()) {
                _selectedCategory.postValue(expenseCats[0].name)
            }
        }
    }
    
    /**
     * 加载当前月份的预算
     */
    private fun loadCurrentMonthBudget() {
        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            val currentMonth = String.format("%04d-%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1)
            val budget = database.budgetDao().getBudgetByMonth(currentMonth)
            _currentBudget.postValue(budget)
        }
    }
    
    /**
     * 设置选择的类型
     */
    fun setSelectedType(type: Int) {
        _selectedType.value = type
        
        // 根据类型更新默认分类
        viewModelScope.launch {
            val categories = database.billCategoryDao().getCategoriesByType(type)
            if (categories.isNotEmpty()) {
                _selectedCategory.postValue(categories[0].name)
            }
        }
    }
    
    /**
     * 设置选择的分类
     */
    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }
    
    /**
     * 设置金额
     */
    fun setAmount(amount: Double) {
        _amount.value = amount
    }
    
    /**
     * 设置备注
     */
    fun setRemark(remark: String) {
        _remark.value = remark
    }
    
    /**
     * 设置日期
     */
    fun setSelectedDate(date: Date) {
        _selectedDate.value = date
    }
    
    /**
     * 保存记账记录
     */
    fun saveRecord(): Boolean {
        val type = _selectedType.value ?: 2
        val category = _selectedCategory.value ?: return false
        val amount = _amount.value ?: 0.0
        val remark = _remark.value ?: ""
        val date = _selectedDate.value ?: Date()
        
        if (amount <= 0) return false
        
        val record = BillRecord(
            type = type,
            category = category,
            amount = amount,
            remark = remark,
            date = date
        )
        
        viewModelScope.launch {
            // 保存记录
            database.billRecordDao().insert(record)
            
            // 更新预算已使用金额（如果是支出）
            if (type == 2) {
                updateBudgetUsedAmount(date)
            }
        }
        
        return true
    }
    
    /**
     * 更新预算已使用金额
     */
    private fun updateBudgetUsedAmount(date: Date) {
        val calendar = Calendar.getInstance()
        calendar.time = date
        val month = String.format("%04d-%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1)
        
        viewModelScope.launch {
            // 获取当月所有支出记录的总金额
            val records = database.billRecordDao().getRecordsByMonth(month)
            val totalExpense = records.filter { it.type == 2 }.sumOf { it.amount }
            
            // 获取或创建预算
            var budget = database.budgetDao().getBudgetByMonth(month)
            if (budget == null) {
                budget = Budget(month = month, amount = 0.0, usedAmount = totalExpense)
                database.budgetDao().insert(budget)
            } else {
                budget = budget.copy(usedAmount = totalExpense)
                database.budgetDao().update(budget)
            }
            
            // 如果是当前月份，更新LiveData
            val currentCalendar = Calendar.getInstance()
            val currentMonth = String.format("%04d-%02d", currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH) + 1)
            if (month == currentMonth) {
                _currentBudget.postValue(budget)
            }
        }
    }
    
    /**
     * 加载当月记账记录
     */
    fun loadCurrentMonthRecords() {
        val calendar = Calendar.getInstance()
        val currentMonth = String.format("%04d-%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1)
        
        viewModelScope.launch {
            val records = database.billRecordDao().getRecordsByMonth(currentMonth)
            _records.postValue(records)
        }
    }
    
    /**
     * 加载指定月份的记账记录
     */
    fun loadRecordsByMonth(month: String) {
        viewModelScope.launch {
            val records = database.billRecordDao().getRecordsByMonth(month)
            _records.postValue(records)
        }
    }
    
    /**
     * 加载本周记账记录
     */
    fun loadCurrentWeekRecords() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val week = calendar.get(Calendar.WEEK_OF_YEAR)
        val yearWeek = String.format("%04d-%02d", year, week)
        
        viewModelScope.launch {
            val records = database.billRecordDao().getRecordsByWeek(yearWeek)
            _records.postValue(records)
        }
    }
    
    /**
     * 设置当月预算
     */
    fun setCurrentMonthBudget(amount: Double) {
        val calendar = Calendar.getInstance()
        val currentMonth = String.format("%04d-%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1)
        
        viewModelScope.launch {
            var budget = database.budgetDao().getBudgetByMonth(currentMonth)
            if (budget == null) {
                budget = Budget(month = currentMonth, amount = amount, usedAmount = 0.0)
                database.budgetDao().insert(budget)
            } else {
                budget = budget.copy(amount = amount)
                database.budgetDao().update(budget)
            }
            
            _currentBudget.postValue(budget)
        }
    }
    
    /**
     * 获取所有记账记录
     */
    suspend fun getAllRecords(): List<BillRecord> {
        return database.billRecordDao().getAllRecords()
    }
}