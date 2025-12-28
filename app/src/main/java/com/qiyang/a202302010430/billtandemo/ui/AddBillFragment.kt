package com.qiyang.a202302010430.billtandemo.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.qiyang.a202302010430.billtandemo.R
import com.qiyang.a202302010430.billtandemo.viewmodel.BillViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddBillFragment : Fragment() {
    
    private lateinit var billViewModel: BillViewModel
    private lateinit var btnIncome: Button
    private lateinit var btnExpense: Button
    private lateinit var etAmount: EditText
    private lateinit var spCategory: Spinner
    private lateinit var etRemark: EditText
    private lateinit var btnSelectDate: Button
    private lateinit var btnSave: Button
    
    private var currentType = 2 // 默认支出
    private var currentDate = Date()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val calendar = Calendar.getInstance()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_bill, container, false)
        
        btnIncome = view.findViewById(R.id.btn_income)
        btnExpense = view.findViewById(R.id.btn_expense)
        etAmount = view.findViewById(R.id.et_amount)
        spCategory = view.findViewById(R.id.sp_category)
        etRemark = view.findViewById(R.id.et_remark)
        btnSelectDate = view.findViewById(R.id.btn_select_date)
        btnSave = view.findViewById(R.id.btn_save)
        
        return view
    }
    
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        
        billViewModel = ViewModelProvider(this).get(BillViewModel::class.java)
        
        setupUI()
        observeData()
    }
    
    private fun setupUI() {
        // 设置默认日期
        btnSelectDate.text = dateFormat.format(currentDate)
        
        // 类型选择
        btnIncome.setOnClickListener {
            currentType = 1
            updateTypeButtons()
            billViewModel.setSelectedType(1)
        }
        
        btnExpense.setOnClickListener {
            currentType = 2
            updateTypeButtons()
            billViewModel.setSelectedType(2)
        }
        
        // 更新类型按钮状态
        updateTypeButtons()
        
        // 日期选择
        btnSelectDate.setOnClickListener {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                DatePickerDialog.OnDateSetListener {
                    _, selectedYear, selectedMonth, selectedDay ->
                    calendar.set(selectedYear, selectedMonth, selectedDay)
                    currentDate = calendar.time
                    btnSelectDate.text = dateFormat.format(currentDate)
                    billViewModel.setSelectedDate(currentDate)
                },
                year,
                month,
                day
            )
            datePickerDialog.show()
        }
        
        // 分类选择
        spCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val category = parent?.getItemAtPosition(position) as String
                billViewModel.setSelectedCategory(category)
            }
            
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 无需处理
            }
        }
        
        // 保存按钮
        btnSave.setOnClickListener {
            saveRecord()
        }
    }
    
    private fun observeData() {
        // 观察收入分类
        billViewModel.incomeCategories.observe(viewLifecycleOwner, Observer {
            if (currentType == 1) {
                updateCategorySpinner(it.map { category -> category.name })
            }
        })
        
        // 观察支出分类
        billViewModel.expenseCategories.observe(viewLifecycleOwner, Observer {
            if (currentType == 2) {
                updateCategorySpinner(it.map { category -> category.name })
            }
        })
    }
    
    private fun updateTypeButtons() {
        if (currentType == 1) {
            btnIncome.setBackgroundResource(R.drawable.btn_type_selected)
            btnIncome.setTextColor(resources.getColor(R.color.white))
            btnExpense.setBackgroundResource(R.drawable.btn_type_unselected)
            btnExpense.setTextColor(resources.getColor(R.color.black))
        } else {
            btnExpense.setBackgroundResource(R.drawable.btn_type_selected)
            btnExpense.setTextColor(resources.getColor(R.color.white))
            btnIncome.setBackgroundResource(R.drawable.btn_type_unselected)
            btnIncome.setTextColor(resources.getColor(R.color.black))
        }
    }
    
    private fun updateCategorySpinner(categories: List<String>) {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            categories
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spCategory.adapter = adapter
    }
    
    private fun saveRecord() {
        val amountStr = etAmount.text.toString()
        val remark = etRemark.text.toString()
        
        if (amountStr.isEmpty()) {
            Toast.makeText(requireContext(), "请输入金额", Toast.LENGTH_SHORT).show()
            return
        }
        
        val amount = amountStr.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            Toast.makeText(requireContext(), "请输入有效的金额", Toast.LENGTH_SHORT).show()
            return
        }
        
        // 设置数据
        billViewModel.setAmount(amount)
        billViewModel.setRemark(remark)
        billViewModel.setSelectedDate(currentDate)
        billViewModel.setSelectedType(currentType)
        
        // 保存记录
        val success = billViewModel.saveRecord()
        if (success) {
            Toast.makeText(requireContext(), "保存成功", Toast.LENGTH_SHORT).show()
            clearForm()
        } else {
            Toast.makeText(requireContext(), "保存失败", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun clearForm() {
        etAmount.text.clear()
        etRemark.text.clear()
        currentDate = Date()
        btnSelectDate.text = dateFormat.format(currentDate)
    }
}
