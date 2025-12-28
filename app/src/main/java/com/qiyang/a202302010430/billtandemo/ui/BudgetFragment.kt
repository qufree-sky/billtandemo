package com.qiyang.a202302010430.billtandemo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.qiyang.a202302010430.billtandemo.R
import com.qiyang.a202302010430.billtandemo.viewmodel.BillViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BudgetFragment : Fragment() {
    
    private lateinit var billViewModel: BillViewModel
    private lateinit var tvCurrentMonth: TextView
    private lateinit var etBudgetAmount: EditText
    private lateinit var tvUsedAmount: TextView
    private lateinit var tvRemainingAmount: TextView
    private lateinit var pbBudgetProgress: ProgressBar
    private lateinit var btnSaveBudget: Button
    
    private val dateFormat = SimpleDateFormat("yyyy年MM月", Locale.getDefault())
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_budget, container, false)
        
        tvCurrentMonth = view.findViewById(R.id.tv_current_month)
        etBudgetAmount = view.findViewById(R.id.et_budget_amount)
        tvUsedAmount = view.findViewById(R.id.tv_used_amount)
        tvRemainingAmount = view.findViewById(R.id.tv_remaining_amount)
        pbBudgetProgress = view.findViewById(R.id.pb_budget_progress)
        btnSaveBudget = view.findViewById(R.id.btn_save_budget)
        
        return view
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        billViewModel = ViewModelProvider(this).get(BillViewModel::class.java)
        
        setupUI()
        observeData()
    }
    
    private fun setupUI() {
        // 设置当前月份
        val currentDate = Calendar.getInstance().time
        tvCurrentMonth.text = dateFormat.format(currentDate)
        
        // 保存预算按钮
        btnSaveBudget.setOnClickListener {
            saveBudget()
        }
    }
    
    private fun observeData() {
        // 观察预算变化
        billViewModel.currentBudget.observe(viewLifecycleOwner, Observer {
            updateBudgetUI(it)
        })
    }
    
    private fun updateBudgetUI(budget: com.qiyang.a202302010430.billtandemo.data.entity.Budget?) {
        if (budget != null) {
            etBudgetAmount.setText(budget.amount.toString())
            tvUsedAmount.text = "¥${String.format("%.2f", budget.usedAmount)}"
            
            val remaining = budget.amount - budget.usedAmount
            tvRemainingAmount.text = "¥${String.format("%.2f", remaining)}"
            
            // 更新进度条
            val progress = if (budget.amount > 0) {
                ((budget.usedAmount / budget.amount) * 100).toInt()
            } else {
                0
            }
            
            pbBudgetProgress.progress = progress
            
            // 超支提醒
            if (remaining < 0) {
                Toast.makeText(requireContext(), "预算已超支！", Toast.LENGTH_LONG).show()
            }
        } else {
            etBudgetAmount.setText("")
            tvUsedAmount.text = "¥0.00"
            tvRemainingAmount.text = "¥0.00"
            pbBudgetProgress.progress = 0
        }
    }
    
    private fun saveBudget() {
        val amountStr = etBudgetAmount.text.toString()
        
        if (amountStr.isEmpty()) {
            Toast.makeText(requireContext(), "请输入预算金额", Toast.LENGTH_SHORT).show()
            return
        }
        
        val amount = amountStr.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            Toast.makeText(requireContext(), "请输入有效的预算金额", Toast.LENGTH_SHORT).show()
            return
        }
        
        // 保存预算
        billViewModel.setCurrentMonthBudget(amount)
        Toast.makeText(requireContext(), "预算保存成功", Toast.LENGTH_SHORT).show()
    }
}
