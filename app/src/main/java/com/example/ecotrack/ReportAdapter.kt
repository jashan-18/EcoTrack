package com.jashan.ecotrack

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

import java.text.SimpleDateFormat
import java.util.*

class ReportAdapter(private val reportList: List<Report>) :
    RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {

    class ReportViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCity: TextView = view.findViewById(R.id.tvCityItem)
        val tvDesc: TextView = view.findViewById(R.id.tvDescItem)
        val tvTime: TextView = view.findViewById(R.id.tvTimeItem)
        val imgReport: ImageView = view.findViewById(R.id.imgReport)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_report, parent, false)
        return ReportViewHolder(view)
    }

    override fun getItemCount() = reportList.size

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {

        val report = reportList[position]

        holder.tvCity.text = "City: ${report.city}"
        holder.tvDesc.text = report.description

        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        holder.tvTime.text = sdf.format(Date(report.timestamp))

        Glide.with(holder.itemView.context)
            .load(report.imageUrl)
            .into(holder.imgReport)
    }
}