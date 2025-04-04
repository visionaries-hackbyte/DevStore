package com.developerspoints.devstores.NavBar

import android.content.Intent
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.developerspoints.devstores.Categories.CategoriesActivity
import com.developerspoints.devstores.Downloads.DownloadsActivity
import com.developerspoints.devstores.Home.Home
import com.developerspoints.devstores.R
import com.developerspoints.devstores.Upload.UploadActivity

class NavBar(private val activity: AppCompatActivity) {

    fun setupNavBar() {
        val navBarHome: LinearLayout = activity.findViewById(R.id.navbar_home)
        val navBarUpload: LinearLayout = activity.findViewById(R.id.navbar_upload)
        val navBarDownload: LinearLayout = activity.findViewById(R.id.navbar_download)
        val navBarHistory: LinearLayout = activity.findViewById(R.id.navbar_history)

        navBarHome.setOnClickListener {
            val intent = Intent(activity, Home::class.java)
            activity.startActivity(intent)
        }

        navBarUpload.setOnClickListener {
            val intent = Intent(activity, UploadActivity::class.java)
            activity.startActivity(intent)
        }

        navBarDownload.setOnClickListener {
            val intent = Intent(activity, DownloadsActivity::class.java)
            activity.startActivity(intent)
        }

        navBarHistory.setOnClickListener {
            val intent = Intent(activity, CategoriesActivity::class.java)
            activity.startActivity(intent)
        }
    }
}
