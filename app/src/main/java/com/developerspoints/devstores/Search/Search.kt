package com.developerspoints.devstores.Search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
    private var currentTab: String = "ALL"


    private val searchResults = mutableListOf<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchEditText = findViewById(R.id.searchEditText)
        searchResultsRecyclerView = findViewById(R.id.searchResultsRecyclerView)

        searchAdapter = SearchAdapter(searchResults)
        searchResultsRecyclerView.layoutManager = LinearLayoutManager(this)
        searchResultsRecyclerView.adapter = searchAdapter

        val tabApps = findViewById<Button>(R.id.tabApps)
        val tabDevelopers = findViewById<Button>(R.id.tabDevelopers)

        findViewById<Button>(R.id.tabApps).setOnClickListener {
            currentTab = "APPS"
            updateTabUI("APPS")
            performSearch(searchEditText.text.toString().trim())
        }

        findViewById<Button>(R.id.tabDevelopers).setOnClickListener {
            currentTab = "DEVELOPERS"
            updateTabUI("DEVELOPERS")
            performSearch(searchEditText.text.toString().trim())
        }



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

        if (currentTab == "APPS" || currentTab == "ALL") {
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

                    if (currentTab == "APPS") {
                        searchAdapter.notifyDataSetChanged()
                        toggleEmptyState()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@SearchActivity, "Error searching apps: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        if (currentTab == "DEVELOPERS" || currentTab == "ALL") {
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

                    if (currentTab == "DEVELOPERS") {
                        searchAdapter.notifyDataSetChanged()
                        toggleEmptyState()
                    } else if (currentTab == "ALL") {
                        searchAdapter.notifyDataSetChanged()
                        toggleEmptyState()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@SearchActivity, "Error searching developers: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }


    }

    private fun toggleEmptyState() {
        val emptyState = findViewById<LinearLayout>(R.id.emptyState)
        if (searchResults.isEmpty()) {
            emptyState.visibility = View.VISIBLE
        } else {
            emptyState.visibility = View.GONE
        }
    }

    private fun updateTabUI(selectedTab: String) {
        val tabApps = findViewById<Button>(R.id.tabApps)
        val tabDevelopers = findViewById<Button>(R.id.tabDevelopers)

        if (selectedTab == "APPS") {
            tabApps.setBackgroundResource(R.drawable.filled_button) // highlighted background
            tabApps.setTextColor(ContextCompat.getColor(this, android.R.color.white))

            tabDevelopers.setBackgroundResource(R.drawable.rounded_outline_button) // unselected background
            tabDevelopers.setTextColor(ContextCompat.getColor(this, R.color.LoginBtn))
        } else if (selectedTab == "DEVELOPERS") {
            tabDevelopers.setBackgroundResource(R.drawable.filled_button)
            tabDevelopers.setTextColor(ContextCompat.getColor(this, android.R.color.white))

            tabApps.setBackgroundResource(R.drawable.rounded_outline_button)
            tabApps.setTextColor(ContextCompat.getColor(this, R.color.LoginBtn))
        }
    }



}