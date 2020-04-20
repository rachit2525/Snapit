package com.example.snapit

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.util.*

class CreateSnapActivity : AppCompatActivity() {

    var createSnapImageView :ImageView? = null
    var messageEditText : EditText? = null
    val imageName = UUID.randomUUID().toString()+".jpg"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_snap)

        createSnapImageView=findViewById(R.id.createSnapImageView)
        messageEditText = findViewById(R.id.messageEditText)
    }

    

    /////////////////////image nikalne ka kaaam idhar chal rha .... yahan se bs createSnapImageView mei photo mil jayegi... baaki kaam dekhlo.../////////////////////////////
    val REQUEST_IMAGE_CAPTURE = 1
    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }
    fun chooseImageClicked(view: View) {
        if(checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),1)
        } else {
            dispatchTakePictureIntent()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            createSnapImageView?.setImageBitmap(imageBitmap)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode==1) {
            if (grantResults.size > 0&&grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                dispatchTakePictureIntent()
            }
        }
    }
    ////////////////////////////////////////  iite mei kaam hua image ka.....//////////////////////////////////////
    fun nextClicked(view: View) {

        // Get the data from an ImageView as bytes
        createSnapImageView?.isDrawingCacheEnabled = true
        createSnapImageView?.buildDrawingCache()
        val bitmap = (createSnapImageView?.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()


        val ref = FirebaseStorage.getInstance().getReference().child("images").child(imageName)

        val uploadTask = ref.putBytes(data)
        uploadTask.addOnFailureListener {
            Toast.makeText(this,"Could not Upload and send",Toast.LENGTH_SHORT).show()
            // Handle unsuccessful uploads
        }.addOnSuccessListener {
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
            //val downloadUrl=taskSnapshot.downloadUrl
            val urlTask = uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                ref.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    Log.i("URL:",downloadUri.toString())

                    val intent=Intent(this,ChooseUserActivity::class.java)

                    intent.putExtra("imageURL",downloadUri.toString())
                    intent.putExtra("imageName",imageName)
                    intent.putExtra("message",messageEditText?.text?.toString())


                    startActivity(intent)

                } else {
                    Toast.makeText(this,"Could not Upload and send",Toast.LENGTH_SHORT).show()
                    // Handle failures
                    // ...
                }
            }


        }

    }
}
