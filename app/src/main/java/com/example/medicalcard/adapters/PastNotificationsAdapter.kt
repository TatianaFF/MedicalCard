package com.example.medicalcard.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.medicalcard.databinding.ItemPastNotifyBinding
import com.example.medicalcard.utils.getDateShort
import com.example.medicalcard.utils.getTimeString
import com.example.models.Notification
import java.time.LocalDateTime
import java.time.ZoneOffset

class PastNotificationsAdapter(
    val notifications: List<Pair<Notification, String>>
) : RecyclerView.Adapter<PastNotificationsAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemPastNotifyBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPastNotifyBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(notifications[position]){
                val date = LocalDateTime.ofEpochSecond(this.first.dateTime, 0, ZoneOffset.UTC)
                binding.tvDate.text = getDateShort(date.toLocalDate())
                binding.tvTime.text = getTimeString(date)
                binding.tvNameVisit.text = this.second
            }
        }
    }

    override fun getItemCount(): Int {
        return notifications.size
    }
}