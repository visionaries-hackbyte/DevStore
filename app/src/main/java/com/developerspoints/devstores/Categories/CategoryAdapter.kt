package com.developerspoints.devstores.Categories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.developerspoints.devstores.R

class CategoryAdapter(
    private val categories: List<Pair<String, List<AppItem>>>,
    private val onItemClick: (AppItem) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryTitle: TextView = itemView.findViewById(R.id.categoryName)
        val appsRecyclerView: RecyclerView = itemView.findViewById(R.id.appsRecyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val (categoryName, apps) = categories[position]

        holder.categoryTitle.text = categoryName

        // Setup horizontal recycler view for apps
        holder.appsRecyclerView.layoutManager = LinearLayoutManager(
            holder.itemView.context,
            LinearLayoutManager.HORIZONTAL,
            false
        )

        holder.appsRecyclerView.adapter = AppAdapter(apps, onItemClick)
    }

    override fun getItemCount(): Int = categories.size

    private inner class AppAdapter(
        private val apps: List<AppItem>,
        private val onItemClick: (AppItem) -> Unit
    ) : RecyclerView.Adapter<AppAdapter.AppViewHolder>() {

        inner class AppViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val appIcon: ImageView = itemView.findViewById(R.id.appIcon)
            val appName: TextView = itemView.findViewById(R.id.appName)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_app, parent, false)
            return AppViewHolder(view)
        }

        override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
            val app = apps[position]

            Glide.with(holder.itemView.context)
                .load(app.picUrl)
                .placeholder(R.drawable.logo)
                .into(holder.appIcon)

            holder.appName.text = app.fileName

            holder.itemView.setOnClickListener {
                onItemClick(app)
            }
        }

        override fun getItemCount(): Int = apps.size
    }
}