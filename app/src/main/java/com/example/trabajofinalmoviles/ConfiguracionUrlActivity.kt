package com.example.trabajofinalmoviles

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_configuracion_url.*

class ConfiguracionUrlActivity : AppCompatActivity() {

    companion object {
        val PREFS_FILENAME = "address.prefs"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracion_url)

        valueId.setText(getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE).getString("address", ""))

        addButton.setOnClickListener(){
            val preferencesEditor = getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE).edit()
            preferencesEditor.putString("address", address.text.toString())
            preferencesEditor.apply()
            message.setText("Preferencia Guardada")
            message.setBackgroundColor(Color.GREEN)
            valueId.setText(address.text.toString())
        }
    }

    fun back(view: View){
        finish()
    }
}
