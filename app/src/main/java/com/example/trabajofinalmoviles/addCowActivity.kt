package com.example.trabajofinalmoviles

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class addCowActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_cow)
    }

    fun addCow(view: View){

        finish()
    }

    fun back(view: View){
        finish()
    }
}
