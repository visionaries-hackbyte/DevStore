package com.developerspoints.devstores.Categories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.developerspoints.devstores.R

class NestedAppAdapter(private val apps: List<AppItem>) : RecyclerView.Adapter<NestedAppAdapter.AppViewHolder>() {

    inner class AppViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val appName: TextView = itemView.findViewById(R.id.appName)
        val appDescription: TextView = itemView.findViewById(R.id.appDescription)
        val appIcon: ImageView = itemView.findViewById(R.id.appIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_app, parent, false)
        return AppViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val app = apps[position]
        holder.appName.text = app.fileName
        holder.appDescription.text = app.description

        Glide.with(holder.itemView.context)
            .load(app.picUrl)
            .into(holder.appIcon)
    }

    override fun getItemCount() = apps.size
}