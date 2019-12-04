package com.example.trabajofinalmoviles

import android.content.Context
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_add_herd.*
import kotlinx.android.synthetic.main.activity_add_herd.addButton
import kotlinx.android.synthetic.main.activity_add_herd.message
import kotlinx.android.synthetic.main.activity_add_herd.valueId
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class AddHerdActivity : AppCompatActivity() {

    val success = "Rodeo cargado"
    val fail = "No se pudo cargar el rodeo"

    inner class Tarea: AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg params: Void?): Void? {

            var request = OkHttpRequest(OkHttpClient())

            var js = JSONObject()
            js.put("location", loc.text.toString())

            val conexion = "http://"+getSharedPreferences(ConfiguracionUrlActivity.PREFS_FILENAME, Context.MODE_PRIVATE).getString("address", "")+"/api/herd"

            request.POST(conexion, js,  object: Callback{
                override fun onResponse(call: Call?, response: Response) {
                    val responseData = response.body()?.string()
                    runOnUiThread{
                        try {
                            var json = JSONObject(responseData)
                            valueId.setText(json.getString("id"))
                            loc.setText(json.getString("location"))
                            message.setText(success)
                            layoutIdAddHerd.setVisibility(View.VISIBLE)
                            message.setBackgroundColor(Color.GREEN)
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            layoutIdAddHerd.setVisibility(View.INVISIBLE)
                            message.setText(fail)
                            message.setBackgroundColor(Color.RED)
                        }
                        addButton.setText("Cargar")
                        addButton.isEnabled = true
                    }
                }
                override fun onFailure(call: Call?, e: IOException?) {
                    println(e)
                    runOnUiThread {
                        layoutIdAddHerd.setVisibility(View.INVISIBLE)
                        message.setText(fail)
                        message.setBackgroundColor(Color.RED)
                        addButton.setText("Cargar")
                        addButton.isEnabled = true
                    }
                }
            })

            return null
        }
    }

    var asyn: Tarea? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_herd)
        if (savedInstanceState != null) {

            loc.setText(savedInstanceState.getString("location",""))
            valueId.setText(savedInstanceState.getString("herdId",""))

            if (savedInstanceState.getString("message", "") == success) {
                layoutIdAddHerd.setVisibility(View.VISIBLE)
                message.setBackgroundColor(Color.GREEN)
            }else
                if (savedInstanceState.getString("message", "") == fail) {
                    message.setBackgroundColor(Color.RED)
                }
            message.setText(savedInstanceState.getString("message", ""))
        }

        addButton.setOnClickListener(){
            //ocultar teclado
            val view = this.currentFocus
            view?.let { v ->
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(v.windowToken, 0)
            }
            message.setText("Enviando datos")
            message.setBackgroundColor(Color.GREEN)
            addButton.setText("Cargando...")
            addButton.isEnabled = false

            asyn = Tarea()
            asyn?.execute()
        }
    }

    fun back(view: View){
        finish()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        if (outState != null) {
            outState.putString("location", loc.text.toString())
            outState.putString("herdId", valueId.text.toString())
            outState.putString("message", message.text.toString())
            outState.putBoolean("layoutIdVisibility", layoutIdAddHerd.isVisible)
        }
    }

}
