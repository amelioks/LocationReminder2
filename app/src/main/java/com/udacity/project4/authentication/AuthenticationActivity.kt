package com.udacity.project4.authentication

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.udacity.project4.R

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
}
