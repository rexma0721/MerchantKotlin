package com.vroomvroom.fooddeliverys.view.ui.account.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.vroomvroom.fooddeliverys.R
import com.vroomvroom.fooddeliverys.databinding.ItemAccountOptionBinding
import com.vroomvroom.fooddeliverys.data.model.account.AccountMenuOptionItem
import com.vroomvroom.fooddeliverys.view.ui.account.AccountMenuOptionType

class OptionAdapter(
    private val option: List<AccountMenuOptionItem>,
    private val listener: (optionType: AccountMenuOptionType) -> Unit
    ) : RecyclerView.Adapter<OptionAdapter.OptionViewHolder>(){

    class OptionViewHolder(val binding: ItemAccountOptionBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionViewHolder {
        val binding: ItemAccountOptionBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_account_option,
            parent,
            false
        )
        return OptionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OptionViewHolder, position: Int) {
        val option = option[position]
        holder.binding.option = option
        holder.binding.optionIcon.setImageResource(option.icon)
        holder.binding.root.setOnClickListener {
            listener.invoke(option.type)
        }
    }

    override fun getItemCount(): Int {
        return option.size
    }
}