package com.example.medicalcard.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.medicalcard.databinding.ItemNotificationBinding
import com.example.medicalcard.utils.getDateWithDayWeekString
import com.example.medicalcard.utils.getTimeString
import java.time.LocalDateTime

class NotificationsAdapter(
    val ldt: List<LocalDateTime>,
    val onClickDeleteNotification: (index: Int) -> Unit
) : RecyclerView.Adapter<NotificationsAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemNotificationBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(ldt[position]){
                binding.tvDate.text = getDateWithDayWeekString(this.toLocalDate())
                binding.tvTime.text = getTimeString(this)
                binding.btnDelete.setOnClickListener {
                    onClickDeleteNotification(position)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return ldt.size
    }
}