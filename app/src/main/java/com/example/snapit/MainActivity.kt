package com.example.snapit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlin.math.log

class MainActivity : AppCompatActivity() {

    var emailEditText:EditText? =null
    var passwordEditText:EditText? =null
    val mAuth=FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        emailEditText=findViewById(R.id.emailEditText)
        passwordEditText=findViewById(R.id.passwordEditText)

        if(mAuth.currentUser!=null)
        {
            logIn()
        }
    }

    fun goClicked(view: View) {
        //check if we can log in the user

        mAuth.signInWithEmailAndPassword(emailEditText?.text.toString(), passwordEditText?.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    logIn()
                } else {
                    // If sign in fails, display a message to the user.
                    mAuth.createUserWithEmailAndPassword(emailEditText?.text.toString(), passwordEditText?.text.toString())
                        .addOnCompleteListener(this) { task ->
                        if(task.isSuccessful) {
                            
                            FirebaseDatabase.getInstance().getReference().child("users").child(task.result?.user!!.uid).child("email").setValue(emailEditText?.text.toString())
                            
                            logIn()  //add to data base

                        }
                        else
                        {
                            Toast.makeText(this,"LogIn failed try again!!",Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            }

        //sign up the user
    }
    fun logIn() {
        // Move to next Activity
        val intent = Intent(this,SnapsActivity::class.java)
        startActivity(intent)
    }
}
