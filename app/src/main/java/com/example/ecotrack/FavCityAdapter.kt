package com.jashan.ecotrack

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class FavCity(
    val city: String,
    var temp: String,
    var aqi: String
)

class FavCityAdapter(
    private val list: MutableList<FavCity>,
    private val onLongClick: (FavCity, Int) -> Unit
) : RecyclerView.Adapter<FavCityAdapter.CityVH>() {

    class CityVH(v: View) : RecyclerView.ViewHolder(v) {
        val city: TextView = v.findViewById(R.id.tvCity)
        val temp: TextView = v.findViewById(R.id.tvTemp)
        val aqi: TextView = v.findViewById(R.id.tvAqi)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityVH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_fav_city, parent, false)
        return CityVH(v)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: CityVH, position: Int) {

        val item = list[position]

        holder.city.text = item.city
        holder.temp.text = "Temp : ${item.temp}"
        holder.aqi.text = "AQI : ${item.aqi}"

        holder.itemView.setOnLongClickListener {
            onLongClick(item, position)
            true
        }
    }
}