package com.developerspoints.devstores.Upload

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.developerspoints.devstores.NavBar.NavBar
import com.developerspoints.devstores.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID

class UploadActivity : AppCompatActivity() {

    private lateinit var etFileName: EditText
    private lateinit var etFileType: EditText
    private lateinit var etFileUrl: EditText
    private lateinit var etPicUrl: EditText
    private lateinit var etUploadTime: EditText
    private lateinit var btnUploadApk: Button
    private lateinit var btnUploadLogo: Button
    private lateinit var btnSubmit: Button
    private lateinit var etDescription: EditText

    private lateinit var apkProgressContainer: LinearLayout
    private lateinit var apkProgressBar: ProgressBar
    private lateinit var apkPercentageText: TextView
    private lateinit var logoProgressContainer: LinearLayout
    private lateinit var logoProgressBar: ProgressBar
    private lateinit var logoPercentageText: TextView

    private var apkUri: Uri? = null
    private var logoUri: Uri? = null
    private val storageRef = FirebaseStorage.getInstance().reference
    private val databaseRef = FirebaseDatabase.getInstance().reference.child("uploads")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        val navBar = NavBar(this)
        navBar.setupNavBar()

        etDescription = findViewById(R.id.et_description)

        etFileName = findViewById(R.id.et_file_name)
        etFileType = findViewById(R.id.et_file_type)
        etFileUrl = findViewById(R.id.et_file_url)
        etPicUrl = findViewById(R.id.et_pic_url)
        etUploadTime = findViewById(R.id.et_upload_time)
        btnUploadApk = findViewById(R.id.btn_upload_apk)
        btnUploadLogo = findViewById(R.id.btn_upload_logo)
        btnSubmit = findViewById(R.id.btn_submit)
        apkProgressContainer = findViewById(R.id.apk_progress_container)
        apkProgressBar = findViewById(R.id.apk_progress_bar)
        apkPercentageText = findViewById(R.id.apk_percentage)
        logoProgressContainer = findViewById(R.id.logo_progress_container)
        logoProgressBar = findViewById(R.id.logo_progress_bar)
        logoPercentageText = findViewById(R.id.logo_percentage)

        btnUploadApk.setOnClickListener { selectApk() }
        btnUploadLogo.setOnClickListener { selectLogo() }
        btnSubmit.setOnClickListener { uploadData() }
    }

    private fun selectApk() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/vnd.android.package-archive"
        startActivityForResult(intent, 1)
    }

    private fun selectLogo() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, 2)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && data != null && data.data != null) {
            val fileUri = data.data!!

            when (requestCode) {
                1 -> {
                    apkUri = fileUri
                    uploadFileToStorage(apkUri!!, "apk")
                }
                2 -> {
                    logoUri = fileUri
                    uploadFileToStorage(logoUri!!, "logo")
                }
            }
        }
    }

    private fun uploadFileToStorage(fileUri: Uri, type: String) {
        val fileName = UUID.randomUUID().toString()
        val fileRef = storageRef.child("$type/$fileName")

        // Show progress UI and disable button
        when (type) {
            "apk" -> {
                apkProgressContainer.visibility = View.VISIBLE
                btnUploadApk.isEnabled = false
            }
            "logo" -> {
                logoProgressContainer.visibility = View.VISIBLE
                btnUploadLogo.isEnabled = false
            }
        }

        val uploadTask = fileRef.putFile(fileUri)

        // Add progress listener
        uploadTask.addOnProgressListener { taskSnapshot ->
            val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()
            when (type) {
                "apk" -> {
                    apkProgressBar.progress = progress
                    apkPercentageText.text = "$progress%"
                }
                "logo" -> {
                    logoProgressBar.progress = progress
                    logoPercentageText.text = "$progress%"
                }
            }
        }.addOnSuccessListener {
            // Get download URL after successful upload
            fileRef.downloadUrl.addOnSuccessListener { uri ->
                when (type) {
                    "apk" -> {
                        etFileUrl.setText(uri.toString())
                        apkProgressContainer.visibility = View.GONE
                        btnUploadApk.isEnabled = true
                    }
                    "logo" -> {
                        etPicUrl.setText(uri.toString())
                        logoProgressContainer.visibility = View.GONE
                        btnUploadLogo.isEnabled = true
                    }
                }
                Toast.makeText(this, "$type uploaded successfully!", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "$type upload failed: ${exception.message}", Toast.LENGTH_SHORT).show()
            when (type) {
                "apk" -> {
                    apkProgressContainer.visibility = View.GONE
                    btnUploadApk.isEnabled = true
                }
                "logo" -> {
                    logoProgressContainer.visibility = View.GONE
                    btnUploadLogo.isEnabled = true
                }
            }
        }
    }

    private fun uploadData() {
        val fileName = etFileName.text.toString().trim()
        val fileType = etFileType.text.toString().trim()
        val fileUrl = etFileUrl.text.toString().trim()
        val picUrl = etPicUrl.text.toString().trim()
        val uploadTime = System.currentTimeMillis()
        val uploadId = databaseRef.push().key ?: return

        val categoryName = fileType
        val description = etDescription.text.toString().trim()

        val currentUser = FirebaseAuth.getInstance().currentUser
        val userName = currentUser?.displayName ?: currentUser?.email ?: "Unknown User"

        etUploadTime.setText(SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(uploadTime))

        if (fileName.isEmpty() || fileType.isEmpty() || fileUrl.isEmpty() || picUrl.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show()
            return
        }

        val appData = mapOf(
            "fileName" to fileName,
            "fileType" to fileType,
            "fileUrl" to fileUrl,
            "uploadTime" to uploadTime,
            "description" to description,
            "uploadId" to uploadId,
            "picUrl" to picUrl,
            "uploadedBy" to userName
        )

        val updates = hashMapOf<String, Any>(
            "/uploads/$uploadId" to appData,
            "/categories/$categoryName/Apps/$uploadId" to appData
        )

        FirebaseDatabase.getInstance().reference.updateChildren(updates)
            .addOnSuccessListener {
                Toast.makeText(this, "Upload Successful!", Toast.LENGTH_SHORT).show()
                resetForm()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Upload Failed!", Toast.LENGTH_SHORT).show()
            }
    }

    private fun resetForm() {
        etFileName.setText("")
        etFileType.setText("")
        etFileUrl.setText("")
        etPicUrl.setText("")
        etUploadTime.setText("")
        etDescription.setText("")

        apkUri = null
        logoUri = null

        apkProgressBar.progress = 0
        apkPercentageText.text = ""
        apkProgressContainer.visibility = View.GONE
        btnUploadApk.isEnabled = true

        logoProgressBar.progress = 0
        logoPercentageText.text = ""
        logoProgressContainer.visibility = View.GONE
        btnUploadLogo.isEnabled = true
    }

}