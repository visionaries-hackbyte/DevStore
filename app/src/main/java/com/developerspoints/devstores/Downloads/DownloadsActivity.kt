package com.developerspoints.devstores.Downloads

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.developerspoints.devstores.Model.AppModel
import com.developerspoints.devstores.NavBar.DownloadsAdapter
import com.developerspoints.devstores.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DownloadsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DownloadsAdapter
    private val downloadList = mutableListOf<AppModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_downloads)

        recyclerView = findViewById(R.id.downloads)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = DownloadsAdapter(downloadList)
        recyclerView.adapter = adapter

        fetchDownloads()
    }

    private fun fetchDownloads() {
        val ref = FirebaseDatabase.getInstance().reference.child("downloads").child("userId123")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                downloadList.clear()

                for (appSnapshot in snapshot.children) { // Loop through apps
                    val app = appSnapshot.getValue(AppModel::class.java) // ✅ Use AppModel
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