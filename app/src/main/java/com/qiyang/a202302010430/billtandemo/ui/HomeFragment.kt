package com.qiyang.a202302010430.billtandemo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.qiyang.a202302010430.billtandemo.R
import com.qiyang.a202302010430.billtandemo.viewmodel.BillViewModel

class HomeFragment : Fragment() {
    
    private lateinit var billViewModel: BillViewModel
    private lateinit var tvBalanceAmount: TextView
    private lateinit var tvIncomeAmount: TextView
    private lateinit var tvExpenseAmount: TextView
    private lateinit var rvRecentRecords: RecyclerView
    private lateinit var recentRecordAdapter: RecentRecordAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        
        tvBalanceAmount = view.findViewById(R.id.tv_balance_amount)
        tvIncomeAmount = view.findViewById(R.id.tv_income_amount)
        tvExpenseAmount = view.findViewById(R.id.tv_expense_amount)
        rvRecentRecords = view.findViewById(R.id.rv_recent_records)
        
        // 初始化RecyclerView
        rvRecentRecords.layoutManager = LinearLayoutManager(context)
        
        return view
    }
    
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        
        billViewModel = ViewModelProvider(this).get(BillViewModel::class.java)
        
        // 加载当月记录
        billViewModel.loadCurrentMonthRecords()
        
        // 观察记录变化，更新总余额、收入和支出
        billViewModel.records.observe(viewLifecycleOwner, Observer {
            updateBalance(it)
            updateRecentRecords(it)
        })
    }
    
    private fun updateBalance(records: List<com.qiyang.a202302010430.billtandemo.data.entity.BillRecord>) {
        val totalIncome = records.filter { it.type == 1 }.sumOf { it.amount }
        val totalExpense = records.filter { it.type == 2 }.sumOf { it.amount }
        val balance = totalIncome - totalExpense
        
        tvBalanceAmount.text = "¥${String.format("%.2f", balance)}"
        tvIncomeAmount.text = "¥${String.format("%.2f", totalIncome)}"
        tvExpenseAmount.text = "¥${String.format("%.2f", totalExpense)}"
    }
    
    private fun updateRecentRecords(records: List<com.qiyang.a202302010430.billtandemo.data.entity.BillRecord>) {
        // 按日期降序排序，取最近的10条记录
        val recentRecords = records.sortedByDescending { it.date }.take(10)
        recentRecordAdapter = RecentRecordAdapter(recentRecords)
        rvRecentRecords.adapter = recentRecordAdapter
    }
}
