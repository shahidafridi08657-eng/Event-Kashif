package com.example

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp

class TourneyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                val app = FirebaseApp.initializeApp(this)
                Log.d("TourneyApplication", "FirebaseApp initialized: ${app?.name}")
            }
        } catch (e: Exception) {
            Log.e("TourneyApplication", "Firebase initialization caught gracefully", e)
        }
    }
}
