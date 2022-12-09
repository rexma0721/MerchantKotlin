package com.vroomvroom.fooddeliverys.view.ui.orders.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vroomvroom.fooddeliverys.R
import com.vroomvroom.fooddeliverys.data.model.order.Merchant
import com.vroomvroom.fooddeliverys.data.model.order.OrderDto
import com.vroomvroom.fooddeliverys.databinding.ItemOrderBinding
import com.vroomvroom.fooddeliverys.utils.Utils.toUppercase

class OrdersDiffUtil: DiffUtil.ItemCallback<OrderDto>() {
    override fun areItemsTheSame(
        oldItem: OrderDto,
        newItem: OrderDto
    ): Boolean {
        return oldItem._id == newItem._id
    }

    override fun areContentsTheSame(
        oldItem: OrderDto,
        newItem: OrderDto
    ): Boolean {
        return oldItem == newItem
    }

}

class OrderAdapter: ListAdapter<OrderDto, OrderViewHolder>(OrdersDiffUtil()) {

    var onMerchantClicked: ((Merchant) -> Unit)? = null
    var onOrderClicked: ((String) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding: ItemOrderBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_order,
            parent,
            false
        )
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = getItem(position)
        holder.binding.order = order
        val subTotal = order.order_detail!!.totalPrice + order.order_detail.deliveryFee
        holder.binding.apply {
            statusTv.text = order.status!!.label
                .toUppercase()
                .replace(",", " ")
            subtotal.text = holder.itemView.context.getString(
                R.string.peso, "%.2f".format(subTotal))
            val orderProductAdapter = OrderProductAdapter(order.order_detail.products!!)
            orderProductRv.adapter = orderProductAdapter

            orderMerchantLayout.setOnClickListener {
                onMerchantClicked?.invoke(order.merchant!!)
            }

            root.setOnClickListener {
                onOrderClicked?.invoke(order._id)
            }

            coverView.setOnClickListener {
                onOrderClicked?.invoke(order._id)
            }
        }
    }
}

class OrderViewHolder(val binding: ItemOrderBinding): RecyclerView.ViewHolder(binding.root)