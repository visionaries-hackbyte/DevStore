package com.developerspoints.devstores.Home

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.developerspoints.devstores.R
import com.developerspoints.devstores.AppDetail_download.AppDetailsActivity
import com.developerspoints.devstores.model.AppModel

class AppAdapter(private val appList: MutableList<AppModel>) : RecyclerView.Adapter<AppAdapter.AppViewHolder>() {

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
        holder.developerName.text = app.uploadedBy

        Glide.with(holder.itemView.context)
            .load(app.picUrl)
            .placeholder(R.drawable.logo)
            .into(holder.appLogo)

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, AppDetailsActivity::class.java).apply {
                putExtra("fileName", app.fileName)
                putExtra("developerName", app.uploadedBy)
                putExtra("logoUrl", app.picUrl)
                putExtra("uploadId", app.uploadId)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return appList.size
    }
}
