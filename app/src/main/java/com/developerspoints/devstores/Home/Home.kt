package com.developerspoints.devstores.Home

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.developerspoints.devstores.Model.AppModel
import com.developerspoints.devstores.NavBar.NavBar
import com.developerspoints.devstores.R
import com.google.firebase.database.*

class Home : AppCompatActivity() {
    private lateinit var recyclerViewAvailable: RecyclerView
    private lateinit var recyclerViewTrending: RecyclerView
    private val appListAvailable = mutableListOf<AppModel>()
    private val appListTrending = mutableListOf<AppModel>()
    private val databaseRef = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        recyclerViewAvailable = findViewById(R.id.available_recycler)
        recyclerViewAvailable.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        recyclerViewTrending = findViewById(R.id.trending_recycler)
        recyclerViewTrending.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        fetchAppsData()

        val navBar = NavBar(this)
        navBar.setupNavBar()
    }

    private fun fetchAppsData() {
        val uploadsRef = databaseRef.child("uploads")

        uploadsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                appListAvailable.clear()
                appListTrending.clear()

                for (appSnapshot in snapshot.children) {
                    try {
                        val app = appSnapshot.getValue(AppModel::class.java)
                        if (app != null) {
                            appListAvailable.add(app)
                            appListTrending.add(app)
                        }
                    } catch (e: Exception) {
                        Log.e("Firebase", "Error mapping app data: ${e.message}")
                    }
                }

                recyclerViewAvailable.adapter = AppAdapter(appListAvailable)
                recyclerViewTrending.adapter = AppAdapter(appListTrending)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error fetching data: ${error.message}")
            }
        })
    }
}
