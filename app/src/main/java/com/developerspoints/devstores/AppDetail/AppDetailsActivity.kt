package com.developerspoints.devstores.AppDetail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.developerspoints.devstores.R
import com.google.firebase.database.*

class AppDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_details)

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
            // Corrected path: "uploads" not "upload"
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

                        Glide.with(this@AppDetailsActivity)
                            .load(picUrl)
                            .placeholder(R.drawable.logo)
                            .into(appLogo)

                        if (fileUrl.isNotEmpty()) {
                            downloadButton.setOnClickListener {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(fileUrl))
                                startActivity(intent)
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Optionally handle the error
                }
            })
        }
    }
}
