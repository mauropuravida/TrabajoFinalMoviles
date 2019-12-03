package com.example.trabajofinalmoviles

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class GetCowActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_cow)
    }

    fun back(view: View){
        finish()
    }
}
