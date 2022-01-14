package com.mtm.uber_mimic.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import com.mtm.uber_mimic.ui.models.LocationModel

object LocationDiffUtil : DiffUtil.ItemCallback<LocationModel>() {
    override fun areItemsTheSame(oldItem: LocationModel, newItem: LocationModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: LocationModel, newItem: LocationModel): Boolean {
        return oldItem.name == newItem.name
    }
}