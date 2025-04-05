package com.developerspoints.devstores.Search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.developerspoints.devstores.R
import com.developerspoints.devstores.model.AppItem
import com.developerspoints.devstores.model.DeveloperItem

class SearchAdapter(private val items: List<Any>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_APP = 1
        private const val TYPE_DEVELOPER = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is AppItem -> TYPE_APP
            is DeveloperItem -> TYPE_DEVELOPER
            else -> throw IllegalArgumentException("Invalid item type")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_APP -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_app_search_result, parent, false)
                AppViewHolder(view)
            }
            TYPE_DEVELOPER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_developer_search_result, parent, false)
                DeveloperViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is AppItem -> (holder as AppViewHolder).bind(item)
            is DeveloperItem -> (holder as DeveloperViewHolder).bind(item)
        }
    }

    override fun getItemCount() = items.size

    inner class AppViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val appIcon: ImageView = itemView.findViewById(R.id.appIcon)
        private val appName: TextView = itemView.findViewById(R.id.appName)
        private val appDescription: TextView = itemView.findViewById(R.id.appDescription)
        private val appDeveloper: TextView = itemView.findViewById(R.id.appDeveloper)

        fun bind(appItem: AppItem) {
            Glide.with(itemView.context)
                .load(appItem.picUrl)
                .placeholder(R.drawable.logo)
                .into(appIcon)

            appName.text = appItem.fileName
            appDescription.text = appItem.description
            appDeveloper.text = "By ${appItem.uploadedBy}"
        }
    }

    inner class DeveloperViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val developerAvatar: ImageView = itemView.findViewById(R.id.developerAvatar)
        private val developerName: TextView = itemView.findViewById(R.id.developerName)
        private val developerEmail: TextView = itemView.findViewById(R.id.developerEmail)

        fun bind(developerItem: DeveloperItem) {
            Glide.with(itemView.context)
                .load(developerItem.profilePic)
                .placeholder(R.drawable.ic_profile_placeholder)
                .into(developerAvatar)

            developerName.text = developerItem.name
            developerEmail.text = developerItem.email
        }
    }
}