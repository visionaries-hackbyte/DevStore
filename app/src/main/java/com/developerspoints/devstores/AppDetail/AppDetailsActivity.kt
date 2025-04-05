package com.developerspoints.devstores.AppDetail

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.developerspoints.devstores.NavBar.NavBar
import com.developerspoints.devstores.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AppDetailsActivity : AppCompatActivity() {

    private var downloadId: Long = -1L
    private lateinit var fileName: String
    private lateinit var fileUrl: String
    private lateinit var developerName: String
    private lateinit var logoUrl: String
    private lateinit var description: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_details)

        val navBar = NavBar(this)
        navBar.setupNavBar()

        registerReceiver(
            onDownloadComplete,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
            Context.RECEIVER_NOT_EXPORTED
        )

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

        // Set initial UI from Intent
        appNameText.text = intentFileName
        developerNameText.text = intentDeveloper
        Glide.with(this).load(intentLogoUrl).placeholder(R.drawable.logo).into(appLogo)

        if (appId != null) {
            val database = FirebaseDatabase.getInstance().getReference("uploads").child(appId)

            database.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        description = snapshot.child("description").getValue(String::class.java) ?: ""
                        fileName = snapshot.child("fileName").getValue(String::class.java) ?: intentFileName
                        fileUrl = snapshot.child("fileUrl").getValue(String::class.java) ?: ""
                        logoUrl = snapshot.child("picUrl").getValue(String::class.java) ?: intentLogoUrl
                        developerName = snapshot.child("uploadedBy").getValue(String::class.java) ?: intentDeveloper

                        // Update UI
                        appNameText.text = fileName
                        developerNameText.text = developerName
                        descriptionText.text = description
                        Glide.with(this@AppDetailsActivity).load(logoUrl).placeholder(R.drawable.logo).into(appLogo)

                        // Download Button
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
                                downloadId = downloadManager.enqueue(request)

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

    private val onDownloadComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (id == downloadId) {
                val currentUser = FirebaseAuth.getInstance().currentUser
                val userId = currentUser?.uid

                if (userId != null) {
                    val downloadsRef = FirebaseDatabase.getInstance().getReference("downloads")
                        .child(userId)
                        .child(fileName)
                        .child("appdetails")

                    val appDetailsMap = hashMapOf(
                        "fileName" to fileName,
                        "fileUrl" to fileUrl,
                        "developerName" to developerName,
                        "logoUrl" to logoUrl,
                        "description" to description,
                        "timestamp" to System.currentTimeMillis()
                    )

                    downloadsRef.setValue(appDetailsMap).addOnSuccessListener {
                        Toast.makeText(this@AppDetailsActivity, "App downloaded & saved!", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        Toast.makeText(this@AppDetailsActivity, "Failed to save download info.", Toast.LENGTH_SHORT).show()
                    }
                }

                // Install the APK
                val apkFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/$fileName.apk")
                val apkUri: Uri = FileProvider.getUriForFile(
                    this@AppDetailsActivity,
                    "${packageName}.provider",
                    apkFile
                )

                val installIntent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(apkUri, "application/vnd.android.package-archive")
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
                }

                startActivity(installIntent)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(onDownloadComplete)
    }
}
