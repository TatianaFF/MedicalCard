package com.example.medicalcard.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.medicalcard.databinding.ItemFileBinding

class FilesAdapter(
    val files: List<Uri>,
    val onClickDeleteFile: (uri: Uri) -> Unit,
    val onClickFile: (uri: Uri) -> Unit,
    val onClickDownloadFile: (uri: Uri) -> Unit
) : RecyclerView.Adapter<FilesAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemFileBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFileBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(files[position]) {
                binding.tvFile.text = this.lastPathSegment.toString()
                binding.btnDelete.setOnClickListener {
                    onClickDeleteFile(this)
                }
                itemView.setOnClickListener {
                    onClickFile(this)
                }
                binding.btnDownload.setOnClickListener { onClickDownloadFile(this) }
            }
        }
    }

    override fun getItemCount(): Int {
        return files.size
    }
}