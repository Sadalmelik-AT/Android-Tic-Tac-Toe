package com.example.silviobaldwintsangacadet_dm2_projet

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SymbolAdapter(private val symbolDataList: MutableList<MutableMap<String, Any?>>) :
    RecyclerView.Adapter<SymbolAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_symbol, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val symbolItem = symbolDataList[position]
        val symbolName = symbolItem["name"] as? String
        val symbolDrawable = symbolItem["drawable"] as? Drawable

        holder.symbolNameTextView.text = symbolName // Set the symbol name

        if (symbolDrawable != null) {
            holder.symbolImageView.setImageDrawable(symbolDrawable)
            holder.symbolImageView.visibility = View.VISIBLE
        } else {
            // Handle the case where the drawable is null or invalid.
            holder.symbolImageView.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return symbolDataList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val symbolNameTextView: TextView = itemView.findViewById(R.id.symbolName)
        val symbolImageView: ImageView = itemView.findViewById(R.id.symbolImage)
    }
}
