package com.exampley.charapplication

import android.app.Activity
import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.exampley.charapplication.Massages.LatestMasagesActivity
import com.exampley.charapplication.models.User
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class RegisterActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        already_have_account.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }

    fun autoUser(view: View) {
        val email = userEmail.text.toString()
        val password = userPassword.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Snackbar.make(view, "Fields cannot be empty mf", Snackbar.LENGTH_LONG).show()
            return
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener
                uplodeImageToFirebaseStorage()
            }
            .addOnFailureListener {
                view.hideKeyboard()
                Snackbar.make(view, "Faild To Create User:-  ${it.message}", Snackbar.LENGTH_LONG)
                    .show()
            }
    }

    private fun uplodeImageToFirebaseStorage() {
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/image/$filename")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {

                    saveUserToFireBaseDataBase(it.toString())
                    Log.d("Main", "Image is  $it")
                }
            }
    }


    fun View.hideKeyboard() = ViewCompat.getWindowInsetsController(this)
        ?.hide(WindowInsetsCompat.Type.ime())

    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            //uri is actually location of image on storage
            try {
                selectedPhotoUri = data.data
                val bitmap = when {
                    Build.VERSION.SDK_INT < 28 -> MediaStore.Images.Media.getBitmap(
                        this.contentResolver,
                        selectedPhotoUri
                    )
                    else -> {
                        val source =
                            ImageDecoder.createSource(this.contentResolver, selectedPhotoUri!!)
                        ImageDecoder.decodeBitmap(source)
                    }
                }
                circleImageView.setImageBitmap(bitmap)
                userImage.alpha = 0f
            } catch (e: Throwable) {
                Log.d("Main", "$e")
            }

        }
    }

    fun selectImage(view: View) {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 0)
    }


    private fun saveUserToFireBaseDataBase(profilePicture: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user = User(uid, userName.text.toString(), profilePicture)
        ref.setValue(user).addOnSuccessListener {
            Log.d("Main", "saved to fdb")
            val intent = Intent(this, LatestMasagesActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

    }
}

