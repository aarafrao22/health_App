package com.azeroth.healthapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.azeroth.healthapp.R

class TipsAdapter(private val tipsList: List<String>) :
    RecyclerView.Adapter<TipsAdapter.TipsViewHolder>() {

    inner class TipsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tipTextView: TextView = itemView.findViewById(R.id.txt_tips)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TipsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.tip_item, parent, false
        )
        return TipsViewHolder(view)
    }

    override fun onBindViewHolder(holder: TipsViewHolder, position: Int) {
        val tip = tipsList[position]
        holder.tipTextView.text = tip
    }

    override fun getItemCount(): Int {
        return tipsList.size
    }
}
