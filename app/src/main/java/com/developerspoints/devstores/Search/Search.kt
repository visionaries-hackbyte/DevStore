package com.developerspoints.devstores.Search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.developerspoints.devstores.R
import com.developerspoints.devstores.model.AppItem
import com.developerspoints.devstores.model.DeveloperItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchActivity : AppCompatActivity() {

    private lateinit var searchEditText: EditText
    private lateinit var searchResultsRecyclerView: RecyclerView
    private lateinit var searchAdapter: SearchAdapter
    private val searchResults = mutableListOf<Any>() // Can contain both AppItem and DeveloperItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchEditText = findViewById(R.id.searchEditText)
        searchResultsRecyclerView = findViewById(R.id.searchResultsRecyclerView)

        // Setup RecyclerView
        searchAdapter = SearchAdapter(searchResults)
        searchResultsRecyclerView.layoutManager = LinearLayoutManager(this)
        searchResultsRecyclerView.adapter = searchAdapter

        // Setup search functionality
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                if (query.isNotEmpty()) {
                    performSearch(query)
                } else {
                    searchResults.clear()
                    searchAdapter.notifyDataSetChanged()
                }
            }
        })
    }

    private fun performSearch(query: String) {
        searchResults.clear()

        // Search in apps (from uploads)
        val uploadsRef = FirebaseDatabase.getInstance().getReference("uploads")
        uploadsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (uploadSnapshot in snapshot.children) {
                    val fileName = uploadSnapshot.child("fileName").getValue(String::class.java) ?: ""
                    val description = uploadSnapshot.child("description").getValue(String::class.java) ?: ""
                    val uploadedBy = uploadSnapshot.child("uploadedBy").getValue(String::class.java) ?: ""

                    if (fileName.contains(query, true) ||
                        description.contains(query, true) ||
                        uploadedBy.contains(query, true)) {

                        val appItem = AppItem(
                            uploadId = uploadSnapshot.key ?: "",
                            fileName = fileName,
                            description = description,
                            fileUrl = uploadSnapshot.child("fileUrl").getValue(String::class.java) ?: "",
                            picUrl = uploadSnapshot.child("picUrl").getValue(String::class.java) ?: "",
                            uploadedBy = uploadedBy,
                            uploadTime = uploadSnapshot.child("uploadTime").getValue(Long::class.java) ?: 0
                        )
                        searchResults.add(appItem)
                    }
                }

                // Search in developers (from users)
                val usersRef = FirebaseDatabase.getInstance().getReference("users")
                usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(userSnapshot: DataSnapshot) {
                        for (user in userSnapshot.children) {
                            val name = user.child("name").getValue(String::class.java) ?:
                            user.child("username").getValue(String::class.java) ?: ""
                            val email = user.child("email").getValue(String::class.java) ?: ""

                            if (name.contains(query, true) || email.contains(query, true)) {
                                val developerItem = DeveloperItem(
                                    userId = user.key ?: "",
                                    name = name,
                                    email = email,
                                    profilePic = user.child("profilePic").getValue(String::class.java) ?:
                                    user.child("profileImage").getValue(String::class.java) ?: ""
                                )
                                searchResults.add(developerItem)
                            }
                        }

                        // Update UI with combined results
                        searchAdapter.notifyDataSetChanged()

                        if (searchResults.isEmpty()) {
                            Toast.makeText(this@SearchActivity, "No results found", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@SearchActivity, "Error searching developers: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SearchActivity, "Error searching apps: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}