package com.example.silviobaldwintsangacadet_dm2_projet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PlayerAdapter(private val playerDataList: ArrayList<Player>) :
    RecyclerView.Adapter<PlayerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_player, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val playerData = playerDataList[position]
        holder.playerNameTextView.text = playerData.playerName.toString()
        holder.playerScoreTextView.text = playerData.playerScore.toString()
    }

    override fun getItemCount(): Int {
        return playerDataList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val playerNameTextView: TextView = itemView.findViewById(R.id.playerName)
        val playerScoreTextView: TextView = itemView.findViewById(R.id.playerScore)
    }


}
