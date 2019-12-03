package com.example.trabajofinalmoviles

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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

    }

    fun getCow(view: View){
        val intent: Intent = Intent(this, GetCowActivity::class.java)
        startActivity(intent)
    }

    fun test(view: View){
        val intent: Intent = Intent(this, GetCowActivity::class.java)
        intent.putExtra("cowId", 12)
        startActivity(intent)
    }
}
