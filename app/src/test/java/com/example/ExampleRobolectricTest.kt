package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("EVENT KASHIF", appName)
  }

  @Test
  fun `launch MainActivity test`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    if (com.google.firebase.FirebaseApp.getApps(context).isEmpty()) {
      com.google.firebase.FirebaseApp.initializeApp(context)
    }
    println("DEBUG firestore_database_id: " + try { context.getString(R.string.firestore_database_id) } catch (e: Exception) { e.message })
    try {
      val fs = com.google.firebase.firestore.FirebaseFirestore.getInstance()
      println("DEBUG default instance: $fs")
    } catch (e: Exception) {
      println("DEBUG default instance error: $e")
    }
    val dbId = context.getString(R.string.firestore_database_id)
    try {
      val fsNamed = com.google.firebase.firestore.FirebaseFirestore.getInstance(com.google.firebase.FirebaseApp.getInstance(), dbId)
      println("DEBUG named instance: $fsNamed")
    } catch (e: Exception) {
      println("DEBUG named instance error: $e")
    }
    val controller = org.robolectric.Robolectric.buildActivity(MainActivity::class.java).setup()
    org.junit.Assert.assertNotNull(controller.get())
  }
}
