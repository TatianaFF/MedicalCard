package com.example.medicalcard.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.medicalcard.databinding.ItemVisitBinding
import com.example.medicalcard.utils.getDateShort
import com.example.medicalcard.utils.getDateString
import com.example.medicalcard.utils.getDateWithDayWeekString
import com.example.medicalcard.utils.getTimeString
import com.example.models.Visit
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset

class VisitsAdapter(
    private val idsDateTitle: Set<Long>,
    private val idsDivider: Set<Long>,
    private val visits: List<ItemVisit>,
    private val onClickVisit: (id: Long) -> Unit
) : RecyclerView.Adapter<VisitsAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemVisitBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemVisitBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(visits[position]) {
                val ld = date.toLocalDate()

                if (idsDateTitle.contains(id)) {
                    binding.tvTitleDate.visibility =  View.VISIBLE
                    binding.tvTitleDate.text = getDateString(ld)
                } else {
                    binding.tvTitleDate.visibility =  View.GONE
                }

                if (idsDivider.contains(id)) {
                    binding.divider.visibility =  View.VISIBLE
                } else {
                    binding.divider.visibility =  View.GONE
                }

                binding.tvTitle.text = name
                binding.tvProfile.text = profileName
                binding.tvTime.text = getTimeString(date)
                itemView.setOnClickListener {
                    onClickVisit(id)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return visits.size
    }
}

data class ItemVisit(
    val id: Long,
    val name: String,
    val date: LocalDateTime,
    val profileName: String
) {

    companion object {
        fun visitToItemVisit(visit: Visit, profileName: String): ItemVisit =
            with(visit) {
                val date = LocalDateTime.ofEpochSecond(this.date, 0, ZoneOffset.UTC)

                ItemVisit(
                    requireNotNull(id) {"Ошибка при определении идентификатора"},
                    name,
                    date,
                    profileName
                )
            }
    }
}