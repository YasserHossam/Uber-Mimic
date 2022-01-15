package com.mtm.uber_mimic.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mtm.uber_mimic.databinding.ItemLocationBinding
import com.mtm.uber_mimic.ui.models.LocationModel

class LocationAdapter(private val onItemSelected: (LocationModel) -> Unit) :
    ListAdapter<LocationModel, LocationAdapter.LocationViewHolder>(LocationDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return LocationViewHolder(ItemLocationBinding.inflate(layoutInflater))
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    inner class LocationViewHolder(private val binding: ItemLocationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener { onItemSelected(currentList[adapterPosition]) }
        }

        fun bind(model: LocationModel) {
            binding.tvLocationTitle.text = model.name
        }
    }
}