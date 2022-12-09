package com.vroomvroom.fooddeliverys.view.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vroomvroom.fooddeliverys.R
import com.vroomvroom.fooddeliverys.data.model.merchant.OptionType
import com.vroomvroom.fooddeliverys.databinding.ItemOptionTypeBinding
import com.vroomvroom.fooddeliverys.utils.OnOptionClickListener

class OptionSectionDiffUtil: DiffUtil.ItemCallback<OptionType>() {
    override fun areItemsTheSame(
        oldItem: OptionType,
        newItem: OptionType
    ): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(
        oldItem: OptionType,
        newItem: OptionType
    ): Boolean {
        return oldItem == newItem
    }
}

class OptionSectionAdapter (
    private val listener: OnOptionClickListener
): ListAdapter<OptionType, OptionSectionViewHolder>(OptionSectionDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionSectionViewHolder {
        val binding: ItemOptionTypeBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_option_type,
            parent,
            false
        )
        return OptionSectionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OptionSectionViewHolder, position: Int) {
        val optionType = getItem(position)
        holder.binding.optionType = optionType
        val productAdapter = OptionAdapter(getItem(position).options, listener)
        productAdapter.setProductOptionType(optionType.name)
        holder.binding.optionSectionRv.adapter = productAdapter
    }
}

class OptionSectionViewHolder(val binding: ItemOptionTypeBinding): RecyclerView.ViewHolder(binding.root)