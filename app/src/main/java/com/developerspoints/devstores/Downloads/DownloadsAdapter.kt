package com.developerspoints.devstores.NavBar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.developerspoints.devstores.Model.AppModel
import com.developerspoints.devstores.R

class DownloadsAdapter(private val list: MutableList<AppModel>) :
    RecyclerView.Adapter<DownloadsAdapter.DownloadViewHolder>() {

    class DownloadViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val appLogo: ImageView = view.findViewById(R.id.app_logo)
        val appName: TextView = view.findViewById(R.id.app_name)
        val developerName: TextView = view.findViewById(R.id.developer_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DownloadViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.app_card, parent, false)
        return DownloadViewHolder(view)
    }

    override fun onBindViewHolder(holder: DownloadViewHolder, position: Int) {
        val app = list[position]

        holder.appName.text = app.fileName
        holder.developerName.text = app.uploadedBy

        Glide.with(holder.itemView.context)
            .load(app.picUrl)
            .placeholder(R.drawable.logo)
            .error(R.drawable.logo)
            .into(holder.appLogo)
    }

    override fun getItemCount(): Int = list.size
}
