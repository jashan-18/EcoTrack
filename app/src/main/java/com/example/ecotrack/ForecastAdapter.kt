package com.jashan.ecotrack

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ForecastAdapter(
    private val forecastList: List<ForecastItem>
) : RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder>() {

    class ForecastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvTemp: TextView = itemView.findViewById(R.id.tvTempItem)
        val tvHumidity: TextView = itemView.findViewById(R.id.tvHumidityItem)
        val tvDesc: TextView = itemView.findViewById(R.id.tvDescItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_forecast, parent, false)
        return ForecastViewHolder(view)
    }

    override fun getItemCount() = forecastList.size

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {

        val item = forecastList[position]

        holder.tvDate.text = item.dt_txt
        holder.tvTemp.text = "Temp: ${item.main.temp} °C"
        holder.tvHumidity.text = "Humidity: ${item.main.humidity}%"
        holder.tvDesc.text = item.weather[0].description
    }
}