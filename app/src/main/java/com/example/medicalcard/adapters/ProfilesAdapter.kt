package com.example.medicalcard.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.medicalcard.databinding.ItemProfileBinding
import com.example.models.Profile

class ProfilesAdapter(
    private val idCurrentProfile: Long?,
    private val profiles: List<Profile>,
    private val onClickProfile: (id: Long) -> Unit,
    private val onClickEditProfile: (id: Long) -> Unit
) : RecyclerView.Adapter<ProfilesAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemProfileBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProfileBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            if (profiles[position].id != idCurrentProfile) itemView.setBackgroundColor(Color.TRANSPARENT)

            with(profiles[position]){
                binding.tvName.text = this.name
                binding.btnEditProfile.setOnClickListener { onClickEditProfile(requireNotNull(this.id)) }
                itemView.setOnClickListener {
                    onClickProfile(requireNotNull(this.id))
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return profiles.size
    }
}