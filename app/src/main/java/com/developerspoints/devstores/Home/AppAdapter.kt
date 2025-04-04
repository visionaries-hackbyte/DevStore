package com.developerspoints.devstores.Home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.developerspoints.devstores.Model.AppModels
import com.developerspoints.devstores.R

class AppAdapter(private val appList: List<AppModels>) : RecyclerView.Adapter<AppAdapter.AppViewHolder>() {

    class AppViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val appLogo: ImageView = view.findViewById(R.id.app_logo)
        val fileName: TextView = view.findViewById(R.id.app_name)
        val developerName: TextView = view.findViewById(R.id.developer_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.app_card_vertical, parent, false)
        return AppViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val app = appList[position]
        holder.fileName.text = app.fileName
        holder.developerName.text = app.uploaderId

        Glide.with(holder.itemView.context)
            .load(app.fileUrl)
            .placeholder(R.drawable.logo)
            .into(holder.appLogo)
    }

    override fun getItemCount(): Int {
        return appList.size
    }
}
