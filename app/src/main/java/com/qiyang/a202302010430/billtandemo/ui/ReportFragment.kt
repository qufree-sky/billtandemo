package com.qiyang.a202302010430.billtandemo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import com.qiyang.a202302010430.billtandemo.R
import com.qiyang.a202302010430.billtandemo.viewmodel.BillViewModel

class ReportFragment : Fragment() {
    
    private lateinit var billViewModel: BillViewModel
    private lateinit var btnWeek: Button
    private lateinit var btnMonth: Button
    private lateinit var btnYear: Button
    private lateinit var framePieChart: FrameLayout
    private lateinit var frameLineChart: FrameLayout
    
    private lateinit var pieChart: PieChart
    private lateinit var lineChart: LineChart
    
    private var currentTimeType = 2 // 1: 本周, 2: 本月, 3: 本年
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_report, container, false)
        
        btnWeek = view.findViewById(R.id.btn_week)
        btnMonth = view.findViewById(R.id.btn_month)
        btnYear = view.findViewById(R.id.btn_year)
        framePieChart = view.findViewById(R.id.frame_pie_chart)
        frameLineChart = view.findViewById(R.id.frame_line_chart)
        
        // 初始化图表
        pieChart = PieChart(requireContext())
        lineChart = LineChart(requireContext())
        
        framePieChart.addView(pieChart)
        frameLineChart.addView(lineChart)
        
        return view
    }
    
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        
        billViewModel = ViewModelProvider(this).get(BillViewModel::class.java)
        
        setupUI()
        observeData()
    }
    
    private fun setupUI() {
        // 时间选择按钮
        btnWeek.setOnClickListener {
            currentTimeType = 1
            updateTimeButtons()
            loadWeeklyData()
        }
        
        btnMonth.setOnClickListener {
            currentTimeType = 2
            updateTimeButtons()
            loadMonthlyData()
        }
        
        btnYear.setOnClickListener {
            currentTimeType = 3
            updateTimeButtons()
            loadYearlyData()
        }
        
        // 更新按钮状态
        updateTimeButtons()
        
        // 默认加载本月数据
        loadMonthlyData()
    }
    
    private fun updateTimeButtons() {
        when (currentTimeType) {
            1 -> {
                btnWeek.setBackgroundResource(R.drawable.btn_type_selected)
                btnWeek.setTextColor(resources.getColor(R.color.white))
                btnMonth.setBackgroundResource(R.drawable.btn_type_unselected)
                btnMonth.setTextColor(resources.getColor(R.color.black))
                btnYear.setBackgroundResource(R.drawable.btn_type_unselected)
                btnYear.setTextColor(resources.getColor(R.color.black))
            }
            2 -> {
                btnWeek.setBackgroundResource(R.drawable.btn_type_unselected)
                btnWeek.setTextColor(resources.getColor(R.color.black))
                btnMonth.setBackgroundResource(R.drawable.btn_type_selected)
                btnMonth.setTextColor(resources.getColor(R.color.white))
                btnYear.setBackgroundResource(R.drawable.btn_type_unselected)
                btnYear.setTextColor(resources.getColor(R.color.black))
            }
            3 -> {
                btnWeek.setBackgroundResource(R.drawable.btn_type_unselected)
                btnWeek.setTextColor(resources.getColor(R.color.black))
                btnMonth.setBackgroundResource(R.drawable.btn_type_unselected)
                btnMonth.setTextColor(resources.getColor(R.color.black))
                btnYear.setBackgroundResource(R.drawable.btn_type_selected)
                btnYear.setTextColor(resources.getColor(R.color.white))
            }
        }
    }
    
    private fun observeData() {
        billViewModel.records.observe(viewLifecycleOwner, Observer {
            updateCharts(it)
        })
    }
    
    private fun loadWeeklyData() {
        billViewModel.loadCurrentWeekRecords()
    }
    
    private fun loadMonthlyData() {
        billViewModel.loadCurrentMonthRecords()
    }
    
    private fun loadYearlyData() {
        // TODO: 实现加载本年数据
    }
    
    private fun updateCharts(records: List<com.qiyang.a202302010430.billtandemo.data.entity.BillRecord>) {
        updatePieChart(records)
        updateLineChart(records)
    }
    
    private fun updatePieChart(records: List<com.qiyang.a202302010430.billtandemo.data.entity.BillRecord>) {
        // 统计收支数据
        val income = records.filter { it.type == 1 }.sumOf { it.amount }
        val expense = records.filter { it.type == 2 }.sumOf { it.amount }
        
        val entries = mutableListOf<PieEntry>()
        val colors = mutableListOf<Int>()
        
        if (income > 0) {
            entries.add(PieEntry(income.toFloat(), "收入"))
            colors.add(resources.getColor(R.color.green))
        }
        
        if (expense > 0) {
            entries.add(PieEntry(expense.toFloat(), "支出"))
            colors.add(resources.getColor(R.color.orange))
        }
        
        val dataSet = PieDataSet(entries, "收支统计")
        dataSet.colors = colors
        dataSet.valueTextSize = 16f
        dataSet.valueTextColor = resources.getColor(R.color.black)
        
        val data = PieData(dataSet)
        pieChart.data = data
        pieChart.invalidate()
        
        // 配置饼图
        pieChart.setUsePercentValues(true)
        pieChart.description.text = "收支占比"
        pieChart.centerText = "总金额"
        pieChart.setCenterTextSize(18f)
    }
    
    private fun updateLineChart(records: List<com.qiyang.a202302010430.billtandemo.data.entity.BillRecord>) {
        // 按日期分组统计收支数据
        val dateIncomeMap = mutableMapOf<String, Double>()
        val dateExpenseMap = mutableMapOf<String, Double>()
        
        // 统计每日收支
        for (record in records) {
            val date = record.date.toString().substring(0, 10) // 获取日期部分(yyyy-MM-dd)
            if (record.type == 1) { // 收入
                dateIncomeMap[date] = dateIncomeMap.getOrDefault(date, 0.0) + record.amount
            } else { // 支出
                dateExpenseMap[date] = dateExpenseMap.getOrDefault(date, 0.0) + record.amount
            }
        }
        
        // 获取所有日期并排序
        val allDates = (dateIncomeMap.keys + dateExpenseMap.keys).sorted()
        
        // 创建收入和支出的数据集
        val incomeEntries = mutableListOf<Entry>()
        val expenseEntries = mutableListOf<Entry>()
        
        for ((index, date) in allDates.withIndex()) {
            val income = dateIncomeMap.getOrDefault(date, 0.0).toFloat()
            val expense = dateExpenseMap.getOrDefault(date, 0.0).toFloat()
            
            incomeEntries.add(Entry(index.toFloat(), income))
            expenseEntries.add(Entry(index.toFloat(), expense))
        }
        
        // 配置收入数据集
        val incomeDataSet = LineDataSet(incomeEntries, "收入")
        incomeDataSet.color = resources.getColor(R.color.green)
        incomeDataSet.valueTextSize = 12f
        incomeDataSet.valueTextColor = resources.getColor(R.color.black)
        incomeDataSet.lineWidth = 2f
        
        // 配置支出数据集
        val expenseDataSet = LineDataSet(expenseEntries, "支出")
        expenseDataSet.color = resources.getColor(R.color.orange)
        expenseDataSet.valueTextSize = 12f
        expenseDataSet.valueTextColor = resources.getColor(R.color.black)
        expenseDataSet.lineWidth = 2f
        
        // 设置数据
        val data = LineData(incomeDataSet, expenseDataSet)
        lineChart.data = data
        lineChart.invalidate()
        
        // 配置折线图
        lineChart.description.text = "收支趋势"
        lineChart.xAxis.labelCount = allDates.size
        lineChart.xAxis.valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val index = value.toInt()
                if (index >= 0 && index < allDates.size) {
                    return allDates[index].substring(5) // 只显示月-日(MM-dd)
                }
                return ""
            }
        }
        lineChart.axisLeft.labelCount = 5
        lineChart.axisRight.isEnabled = false
        lineChart.legend.isEnabled = true
        lineChart.legend.position = com.github.mikephil.charting.components.Legend.LegendPosition.TOP
    }
}
