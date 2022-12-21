package com.udacity.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.udacity.R

class GlobalBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.e("onReceive", "onReceive")

        if (context != null && intent != null && intent.action != null) {
            if (intent.action!!.equals(
                    context.getString(R.string.custom_action),
                    ignoreCase = true
                )
            ) {
                val extras = intent.extras
                if (extras != null) {

                    val firsArg = extras.getString("file_name")
                    val secondArg = extras.getString("file_status")

                    Toast.makeText(
                        context,
                        "This is $firsArg and This is $secondArg ",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}