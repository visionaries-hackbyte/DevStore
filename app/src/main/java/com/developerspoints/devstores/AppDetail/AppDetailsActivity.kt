package com.developerspoints.devstores.AppDetail

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.developerspoints.devstores.NavBar.NavBar
import com.developerspoints.devstores.R
import com.google.firebase.database.*

class AppDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_details)

        val navBar = NavBar(this)
        navBar.setupNavBar()

        // UI Elements
        val appLogo = findViewById<ImageView>(R.id.app_logo)
        val appNameText = findViewById<TextView>(R.id.app_name)
        val developerNameText = findViewById<TextView>(R.id.app_uploaded_by)
        val descriptionText = findViewById<TextView>(R.id.app_description)
        val downloadButton = findViewById<Button>(R.id.download_button)

        // Get data from Intent
        val appId = intent.getStringExtra("uploadId")
        val intentFileName = intent.getStringExtra("fileName") ?: "Unknown App"
        val intentDeveloper = intent.getStringExtra("developerName") ?: "Unknown Developer"
        val intentLogoUrl = intent.getStringExtra("logoUrl") ?: ""

        // Set initial data from Intent
        appNameText.text = intentFileName
        developerNameText.text = intentDeveloper
        Glide.with(this).load(intentLogoUrl).placeholder(R.drawable.logo).into(appLogo)

        if (appId != null) {
            val database = FirebaseDatabase.getInstance().getReference("uploads").child(appId)

            database.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val description = snapshot.child("description").getValue(String::class.java) ?: ""
                        val fileName = snapshot.child("fileName").getValue(String::class.java) ?: intentFileName
                        val fileUrl = snapshot.child("fileUrl").getValue(String::class.java) ?: ""
                        val picUrl = snapshot.child("picUrl").getValue(String::class.java) ?: intentLogoUrl
                        val uploadedBy = snapshot.child("uploadedBy").getValue(String::class.java) ?: intentDeveloper

                        // Update UI
                        appNameText.text = fileName
                        developerNameText.text = uploadedBy
                        descriptionText.text = description
                        Glide.with(this@AppDetailsActivity).load(picUrl).placeholder(R.drawable.logo).into(appLogo)

                        // Download Button Functionality
                        if (fileUrl.isNotEmpty()) {
                            downloadButton.setOnClickListener {
                                val request = DownloadManager.Request(Uri.parse(fileUrl))
                                request.setTitle("Downloading $fileName")
                                request.setDescription("Please wait...")
                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "$fileName.apk")
                                request.setAllowedOverMetered(true)
                                request.setAllowedOverRoaming(true)

                                val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                                downloadManager.enqueue(request)

                                Toast.makeText(this@AppDetailsActivity, "Downloading started...", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@AppDetailsActivity, "Failed to load app details.", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
