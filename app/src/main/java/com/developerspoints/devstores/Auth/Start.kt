package com.developerspoints.devstores.Auth

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.developerspoints.devstores.R
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

class Start : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = ContextCompat.getColor(this@Start, android.R.color.transparent)
            setBackgroundDrawable(ContextCompat.getDrawable(this@Start, R.drawable.gradient))
        }



        val LogInButton = findViewById<Button>(R.id.loginButton)
        LogInButton.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
        val signUpButton = findViewById<Button>(R.id.signUpButton)
        signUpButton.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }

    }

}
