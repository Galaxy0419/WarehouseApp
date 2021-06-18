package com.demo.warehouseinventoryapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecyclerAdapter(private val onCardDelete: OnCardDelete) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
    private var items: List<WarehouseItem>? = null

    fun setDataSource(itemsIn: List<WarehouseItem>) {
        items = itemsIn
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_card_layout, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvItemName.text = items!![position].name
        holder.tvQuantity.text = items!![position].quantity.toString()
        holder.tvCost.text = items!![position].cost.toString()
        holder.tvDescription.text = items!![position].description
        holder.tvFrozen.text = if (items!![position].isFrozen) "True" else "False"
        holder.btDelete.setOnClickListener { _: View? ->
            onCardDelete.onDelete(items!![holder.adapterPosition].itemId)
            notifyItemRemoved(holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }

    interface OnCardDelete {
        fun onDelete(index: Int)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvItemName: TextView = itemView.findViewById(R.id.tv_card_value_item_name)
        var tvQuantity: TextView = itemView.findViewById(R.id.tv_card_value_quantity)
        var tvCost: TextView = itemView.findViewById(R.id.tv_card_value_cost)
        var tvDescription: TextView = itemView.findViewById(R.id.tv_card_value_description)
        var tvFrozen: TextView = itemView.findViewById(R.id.tv_card_value_frozen)
        var btDelete: Button = itemView.findViewById(R.id.bt_card_delete)
    }
}
