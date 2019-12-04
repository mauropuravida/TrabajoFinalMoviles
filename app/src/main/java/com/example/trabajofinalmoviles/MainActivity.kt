package com.example.trabajofinalmoviles

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging




class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseMessaging.getInstance().subscribeToTopic("notificaciones")
            .addOnCompleteListener { Log.d("FIREBASE", "Suscripción exitosa") }

        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result!!.token
                Log.d("FIREBASE", "Token: $token")
            })
    }

    fun addHerd(view: View){
        val intent: Intent = Intent(this, AddHerdActivity::class.java)
        startActivity(intent)
    }

    fun addCow(view: View){
        val intent: Intent = Intent(this, AddCowActivity::class.java)
        startActivity(intent)
    }

    fun getHerd(view: View){
        val intent: Intent = Intent(this, GetHerdActivity::class.java)
        startActivity(intent)
    }

    fun getCow(view: View){
        val intent: Intent = Intent(this, GetCowActivity::class.java)
        startActivity(intent)
    }

    fun setUrl(view: View){

    }

    fun exit(view: View){
        finishAndRemoveTask()
    }
}
