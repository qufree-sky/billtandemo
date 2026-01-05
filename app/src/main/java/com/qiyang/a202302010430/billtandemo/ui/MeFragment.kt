package com.qiyang.a202302010430.billtandemo.ui

import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.qiyang.a202302010430.billtandemo.R
import com.qiyang.a202302010430.billtandemo.viewmodel.BillViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.Font
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.VerticalAlignment
import org.apache.poi.xssf.usermodel.XSSFCell
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class MeFragment : Fragment() {
    
    private lateinit var llExportData: LinearLayout
    private lateinit var llSettings: LinearLayout
    private lateinit var llAbout: LinearLayout
    private lateinit var billViewModel: BillViewModel
    
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val fileNameFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_me, container, false)
        
        llExportData = view.findViewById(R.id.ll_export_data)
        llSettings = view.findViewById(R.id.ll_settings)
        llAbout = view.findViewById(R.id.ll_about)
        
        return view
    }
    
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        
        billViewModel = ViewModelProvider(this).get(BillViewModel::class.java)
        setupUI()
    }
    
    private fun setupUI() {
        // 导出数据
        llExportData.setOnClickListener {
            exportData()
        }
        
        // 设置
        llSettings.setOnClickListener {
            openSettings()
        }
        
        // 关于我们
        llAbout.setOnClickListener {
            openAbout()
        }
    }
    
    private fun exportData() {
        Toast.makeText(requireContext(), "正在导出数据...", Toast.LENGTH_SHORT).show()
        
        // 在后台线程执行数据导出
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 获取所有记账记录
                val records = billViewModel.getAllRecords()
                
                // 创建Excel工作簿
                val workbook = XSSFWorkbook()
                val sheet = workbook.createSheet("记账记录")
                
                // 设置列宽
                for (i in 0 until 6) {
                    sheet.setColumnWidth(i, 20 * 256)
                }
                
                // 创建表头样式
                val headerStyle = workbook.createCellStyle()
                val headerFont = workbook.createFont()
                headerFont.bold = true
                headerFont.fontHeightInPoints = 12.toShort()
                headerStyle.setFont(headerFont)
                headerStyle.alignment = HorizontalAlignment.CENTER
                headerStyle.verticalAlignment = VerticalAlignment.CENTER
                
                // 创建表头行
                val headerRow = sheet.createRow(0)
                val headers = arrayOf("ID", "类型", "分类", "金额", "备注", "日期")
                
                for (i in headers.indices) {
                    val cell = headerRow.createCell(i)
                    cell.setCellValue(headers[i])
                    cell.cellStyle = headerStyle
                }
                
                // 填充数据行
                var rowNum = 1
                for (record in records) {
                    val row = sheet.createRow(rowNum++)
                    
                    // ID
                    row.createCell(0).setCellValue(record.id.toString())
                    
                    // 类型
                    val typeStr = if (record.type == 1) "收入" else "支出"
                    row.createCell(1).setCellValue(typeStr)
                    
                    // 分类
                    row.createCell(2).setCellValue(record.category)
                    
                    // 金额
                    row.createCell(3).setCellValue(record.amount)
                    
                    // 备注
                    row.createCell(4).setCellValue(record.remark)
                    
                    // 日期
                    val dateStr = dateFormat.format(record.date)
                    row.createCell(5).setCellValue(dateStr)
                }
                
                // 创建文件路径
                val fileName = "橙子记账_${fileNameFormat.format(Date())}.xlsx"
                val file = File(requireContext().getExternalFilesDir(null), fileName)
                
                // 写入文件
                val fos = FileOutputStream(file)
                workbook.write(fos)
                fos.close()
                workbook.close()
                
                // 显示成功提示
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "数据导出成功，文件保存路径：${file.absolutePath}", Toast.LENGTH_LONG).show()
                }
                
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "数据导出失败：${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    private fun openSettings() {
        // TODO: 实现设置功能
        Toast.makeText(requireContext(), "设置功能开发中", Toast.LENGTH_SHORT).show()
    }
    
    private fun openAbout() {
        // TODO: 实现关于我们功能
        Toast.makeText(requireContext(), "关于我们功能开发中", Toast.LENGTH_SHORT).show()
    }
}
