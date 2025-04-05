package com.developerspoints.devstores.Categories

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.developerspoints.devstores.R
import com.developerspoints.devstores.Categories.CategoryAdapter
import com.developerspoints.devstores.NavBar.NavBar
import com.developerspoints.devstores.AppDetail.AppDetailsActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Category : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var categoryAdapter: CategoryAdapter
    private val categoryList = mutableListOf<Pair<String, List<AppItem>>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        val navBar = NavBar(this)
        navBar.setupNavBar()

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize adapter with click listener
        categoryAdapter = CategoryAdapter(categoryList) { appItem ->
            // Handle item click - navigate to AppDetailsActivity
            val intent = Intent(this, AppDetailsActivity::class.java).apply {
                putExtra("uploadId", appItem.uploadId)
                putExtra("fileName", appItem.fileName)
                putExtra("developerName", appItem.uploadedBy)
                putExtra("logoUrl", appItem.picUrl)
                putExtra("description", appItem.description)
                putExtra("fileUrl", appItem.fileUrl)
            }
            startActivity(intent)
        }

        recyclerView.adapter = categoryAdapter

        fetchCategoriesAndApps()
    }

    private fun fetchCategoriesAndApps() {
        val databaseReference = FirebaseDatabase.getInstance().getReference("categories")

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                categoryList.clear()

                for (categorySnapshot in snapshot.children) {
                    val categoryName = categorySnapshot.key ?: continue

                    val appsList = mutableListOf<AppItem>()
                    val appsSnapshot = categorySnapshot.child("Apps")

                    for (appSnapshot in appsSnapshot.children) {
                        val appItem = AppItem(
                            description = appSnapshot.child("description").getValue(String::class.java) ?: "",
                            fileName = appSnapshot.child("fileName").getValue(String::class.java) ?: "",
                            fileType = appSnapshot.child("fileType").getValue(String::class.java) ?: "",
                            fileUrl = appSnapshot.child("fileUrl").getValue(String::class.java) ?: "",
                            picUrl = appSnapshot.child("picUrl").getValue(String::class.java) ?: "",
                            uploadId = appSnapshot.child("uploadId").getValue(String::class.java) ?: "",
                            uploadTime = appSnapshot.child("uploadTime").getValue(Long::class.java) ?: 0L,
                            uploadedBy = appSnapshot.child("uploadedBy").getValue(String::class.java) ?: ""
                        )
                        appsList.add(appItem)
                    }

                    categoryList.add(Pair(categoryName, appsList))
                }

                categoryAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }
}

data class AppItem(
    val description: String,
    val fileName: String,
    val fileType: String,
    val fileUrl: String,
    val picUrl: String,
    val uploadId: String,
    val uploadTime: Long,
    val uploadedBy: String
)