package com.exampley.charapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.exampley.charapplication.Massages.LatestMasagesActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login_.*
import kotlinx.android.synthetic.main.activity_main.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_)


        backToRegisterPage.setOnClickListener {
            finish()
        }



    }

    fun sendUserToMainScreen(view: View) {
        val userId = userName_1.text.toString()
        val password = userPassword_1.text.toString()
        Log.d("Login","$userId  $$password")
        if (userId.isEmpty() || password.isEmpty()) {
            Snackbar.make(view, "kuch dal na bsdk marna ha tara koo", Snackbar.LENGTH_LONG).show()
            return
        }
        FirebaseAuth.getInstance().signInWithEmailAndPassword(userId, password)
            .addOnCompleteListener {
                Log.d("Login","log in")
                val intent = Intent(this, LatestMasagesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
    }
}