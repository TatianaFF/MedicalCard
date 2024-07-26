package com.example.medicalcard.adapters

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.medicalcard.databinding.ItemVisitBinding
import com.example.medicalcard.utils.getDateString
import com.example.medicalcard.utils.getTimeString
import com.example.models.Visit
import java.time.LocalDateTime
import java.time.ZoneOffset

class VisitsAdapter(
    val visits: List<Visit>,
    val onClickVisit: (id: Long) -> Unit
) : RecyclerView.Adapter<VisitsAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemVisitBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemVisitBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(visits[position]){
                val date = LocalDateTime.ofEpochSecond(this.date, 0, ZoneOffset.UTC)
                val day: String =
                    if (date.dayOfMonth.toString().length == 1) "0${date.dayOfMonth}" else date.dayOfMonth.toString()
                val month: String =
                    if (date.monthValue.toString().length == 1) "0${date.monthValue}" else date.monthValue.toString()
                binding.tvTitle.text = this.name
                binding.tvDate.text = "$day.$month.${date.year}"
                binding.tvTime.text = getTimeString(date)
                itemView.setOnClickListener {
                    onClickVisit(requireNotNull(this.id) { "Ошибка при определении идентификатора " })
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return visits.size
    }
}