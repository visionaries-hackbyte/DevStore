package com.developerspoints.devstores.Categories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.developerspoints.devstores.Home.AppAdapter
import com.developerspoints.devstores.R

class CategoryAdapter(private val categories: List<Pair<String, List<AppItem>>>) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryName: TextView = itemView.findViewById(R.id.categoryName)
        val appsRecyclerView: RecyclerView = itemView.findViewById(R.id.appsRecyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val (categoryName, apps) = categories[position]
        holder.categoryName.text = categoryName

        // Setup nested RecyclerView for apps using NestedAppAdapter
        holder.appsRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.appsRecyclerView.adapter = NestedAppAdapter(apps)
    }

    override fun getItemCount() = categories.size
}