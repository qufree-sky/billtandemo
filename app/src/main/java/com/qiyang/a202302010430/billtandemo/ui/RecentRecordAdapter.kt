package com.qiyang.a202302010430.billtandemo.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.qiyang.a202302010430.billtandemo.R
import com.qiyang.a202302010430.billtandemo.data.entity.BillRecord
import java.text.SimpleDateFormat
import java.util.*

class RecentRecordAdapter(private val records: List<BillRecord>) : RecyclerView.Adapter<RecentRecordAdapter.ViewHolder>() {

    private val dateFormat = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCategory: TextView = itemView.findViewById(R.id.tv_category)
        val tvAmount: TextView = itemView.findViewById(R.id.tv_amount)
        val tvDate: TextView = itemView.findViewById(R.id.tv_date)
        val tvNote: TextView = itemView.findViewById(R.id.tv_note)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recent_record, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val record = records[position]
        
        // 设置分类
        holder.tvCategory.text = record.category
        
        // 设置金额，收入为绿色，支出为橙色
        val amountText = if (record.type == 1) "¥+${String.format("%.2f", record.amount)}" else "¥-${String.format("%.2f", record.amount)}"
        holder.tvAmount.text = amountText
        holder.tvAmount.setTextColor(holder.itemView.resources.getColor(if (record.type == 1) R.color.green else R.color.orange))
        
        // 设置日期
        holder.tvDate.text = dateFormat.format(record.date)
        
        // 设置备注
        holder.tvNote.text = record.remark
    }

    override fun getItemCount(): Int = records.size
}
