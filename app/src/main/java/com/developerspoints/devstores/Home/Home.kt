package com.developerspoints.devstores.Home

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.developerspoints.devstores.model.AppModel
import com.developerspoints.devstores.NavBar.NavBar
import com.developerspoints.devstores.Profile.Profile
import com.developerspoints.devstores.R
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Home : AppCompatActivity() {
    private lateinit var recyclerViewAvailable: RecyclerView
    private lateinit var recyclerViewTrending: RecyclerView
    private val appListAvailable = mutableListOf<AppModel>()
    private val appListTrending = mutableListOf<AppModel>()
    private val databaseRef = FirebaseDatabase.getInstance().reference
    private lateinit var date_time: TextView
    private lateinit var profile: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val viewPager2 = findViewById<ViewPager2>(R.id.imageSlider)

        val imageList = listOf(
            "https://img.freepik.com/free-vector/12-12-shopping-day-sales-social-media-promo-template_23-2149827883.jpg?t=st=1743822200~exp=1743825800~hmac=29453a58c2a1c0d7517f2cbf98c5037feda338e0f77ebf2c3cb46bdf9d4edfd9&w=1380",
            "https://img.freepik.com/premium-vector/yellow-book-that-says-smart-apps-cover_988987-29497.jpg?w=1380",
            "https://img.freepik.com/premium-vector/mobile-app-promotion-social-media-facebook-cover-template_135461-375.jpg?w=1380"
        )

        val adapter = ImageSliderAdapter(this, imageList)
        viewPager2.adapter = adapter

        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                val current = viewPager2.currentItem
                val next = (current + 1) % imageList.size
                viewPager2.setCurrentItem(next, true)
                handler.postDelayed(this, 3000) // every 3 seconds
            }
        }
        handler.post(runnable)


        date_time = findViewById(R.id.time_date_day)

        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("EEEE, MMMM d 'at' hh:mm a", Locale.getDefault())
        date_time.text = dateFormat.format(calendar.time)

        profile = findViewById(R.id.profile)
        profile.setOnClickListener {
            val intent = Intent(this, Profile::class.java)
            startActivity(intent)
        }


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
