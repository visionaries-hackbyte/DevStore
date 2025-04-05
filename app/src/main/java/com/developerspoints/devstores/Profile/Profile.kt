package com.developerspoints.devstores.Profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.developerspoints.devstores.Auth.Login
import com.developerspoints.devstores.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView

class Profile : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()

        // Initialize views
        val editBtn = findViewById<ImageButton>(R.id.edit_btn)
        val viewModeContainer = findViewById<LinearLayout>(R.id.view_mode_container)
        val editModeContainer = findViewById<LinearLayout>(R.id.edit_mode_container)
        val btnSave = findViewById<Button>(R.id.btn_save)
        val btnChangePhoto = findViewById<ImageButton>(R.id.btn_change_photo)
        val ivProfilePic = findViewById<CircleImageView>(R.id.iv_profile_pic)
        val ivProfilePicEdit = findViewById<CircleImageView>(R.id.iv_profile_pic_edit)
        val tvName = findViewById<TextView>(R.id.tv_name)
        val tvEmail = findViewById<TextView>(R.id.tv_email)
        val etName = findViewById<EditText>(R.id.et_name)
        val etEmail = findViewById<EditText>(R.id.et_email)
        val btnLogout = findViewById<Button>(R.id.btn_logout)

        // Load current user data
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            val userRef = database.reference.child("users").child(user.uid)

            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userData = snapshot.getValue(User::class.java)
                    userData?.let {
                        tvName.text = it.name
                        tvEmail.text = it.email
                        etName.setText(it.name)
                        etEmail.setText(it.email)

                        Glide.with(this@Profile)
                            .load(it.profilePic)
                            .placeholder(R.drawable.ic_profile_placeholder)
                            .into(ivProfilePic)

                        Glide.with(this@Profile)
                            .load(it.profilePic)
                            .placeholder(R.drawable.ic_profile_placeholder)
                            .into(ivProfilePicEdit)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@Profile, "Failed to load profile", Toast.LENGTH_SHORT).show()
                }
            })
        }

        // Edit button click handler
        editBtn.setOnClickListener {
            viewModeContainer.visibility = View.GONE
            editModeContainer.visibility = View.VISIBLE
        }

        // Change photo button
        btnChangePhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, 100)
        }

        // Save button click handler
        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()

            if (name.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            updateUserProfile(name, email)
        }

        // Logout button
        btnLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, Login::class.java))
            finish()
        }

        // Back button
        findViewById<ImageButton>(R.id.btn_back).setOnClickListener {
            onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.data
            findViewById<CircleImageView>(R.id.iv_profile_pic_edit).setImageURI(selectedImageUri)
        }
    }

    private fun updateUserProfile(name: String, email: String) {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            val userRef = database.reference.child("users").child(user.uid)

            // First update email in Auth if changed
            if (email != user.email) {
                user.updateEmail(email).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        updateUserData(userRef, name, email)
                    } else {
                        Toast.makeText(this, "Email update failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                updateUserData(userRef, name, email)
            }
        }
    }

    private fun updateUserData(userRef: DatabaseReference, name: String, email: String) {
        selectedImageUri?.let { uri ->
            // Upload new profile picture if changed
            val storageRef = storage.reference.child("profile_pics/${auth.currentUser?.uid}")
            storageRef.putFile(uri)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        saveUserData(userRef, name, email, downloadUri.toString())
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Image upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } ?: run {
            // No image change, just update other data
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val currentPic = snapshot.child("profilePic").getValue(String::class.java)
                    saveUserData(userRef, name, email, currentPic ?: "")
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@Profile, "Failed to load current profile pic", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun saveUserData(userRef: DatabaseReference, name: String, email: String, profilePic: String) {
        val userData = hashMapOf(
            "name" to name,
            "email" to email,
            "profilePic" to profilePic,
            "updatedAt" to ServerValue.TIMESTAMP
        )

        userRef.updateChildren(userData as Map<String, Any>)
            .addOnSuccessListener {
                // Update UI
                findViewById<TextView>(R.id.tv_name).text = name
                findViewById<TextView>(R.id.tv_email).text = email
                Glide.with(this)
                    .load(profilePic)
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .into(findViewById(R.id.iv_profile_pic))

                // Switch back to view mode
                findViewById<LinearLayout>(R.id.view_mode_container).visibility = View.VISIBLE
                findViewById<LinearLayout>(R.id.edit_mode_container).visibility = View.GONE

                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to update profile: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    data class User(
        val userId: String = "",
        val name: String = "",
        val email: String = "",
        val profilePic: String = "",
        val createdAt: Long = 0
    )
}