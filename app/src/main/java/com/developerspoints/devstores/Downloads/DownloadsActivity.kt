package com.developerspoints.devstores.Downloads

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.developerspoints.devstores.Model.AppModel
import com.developerspoints.devstores.Model.dmodel
import com.developerspoints.devstores.NavBar.DownloadsAdapter
import com.developerspoints.devstores.NavBar.NavBar
import com.developerspoints.devstores.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DownloadsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DownloadsAdapter
    private val downloadList = mutableListOf<dmodel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_downloads)

        val navBar = NavBar(this)
        navBar.setupNavBar()

        recyclerView = findViewById(R.id.downloads)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = DownloadsAdapter(downloadList)
        recyclerView.adapter = adapter

        fetchDownloads()
    }

    private fun fetchDownloads() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val ref = FirebaseDatabase.getInstance().reference.child("downloads").child(currentUserId)

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                downloadList.clear()

                for (appSnapshot in snapshot.children) { // Loop through each downloaded app
                    val appDetailsSnapshot = appSnapshot.child("appdetails") // ✅ Get "appdetails"

                    val app = appDetailsSnapshot.getValue(dmodel::class.java) // ✅ Map to DModel
                    if (app != null) {
                        downloadList.add(app)
                    }
                }

                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }



}