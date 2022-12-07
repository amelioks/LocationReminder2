package com.udacity.project4.authentication

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.R
import com.udacity.project4.locationreminders.RemindersActivity

/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */
class AuthenticationActivity : AppCompatActivity() {
    //source: https://learn.udacity.com/nanodegrees/nd940/parts/cd0638/lessons/08385552-25d3-44dc-b66f-ae9b193d7468/concepts/88f422a2-4b32-4c59-aea5-22997037c29c

    object RequestCodes {
        const val SIGN_IN = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)

        findViewById<Button>(R.id.button_login).setOnClickListener {
            launchSignInFlow()
        }

    }
    private fun launchSignInFlow() {
        // Give users the option to sign in / register with their email or Google account.
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(), AuthUI.IdpConfig.GoogleBuilder().build()
        )
        // Create and launch sign-in intent.
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(), RequestCodes.SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RequestCodes.SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                // User successfully signed in
                Log.i(
                    ContentValues.TAG,
                    "Successfully signed in user ${FirebaseAuth.getInstance().currentUser?.displayName}!"
                )
                // User successfully signed in and navigate into RemindersActivity
                val intent = Intent(this, RemindersActivity::class.java)
                startActivity(intent)
            } else {
                // Sign in failed.
                Log.i(ContentValues.TAG, "Sign in unsuccessful ${response?.error?.errorCode}")
            }
        }
    }
}
