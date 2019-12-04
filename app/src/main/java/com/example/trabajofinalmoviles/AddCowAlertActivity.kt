package com.example.trabajofinalmoviles

import android.content.Context
import android.graphics.Color
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import kotlinx.android.synthetic.main.activity_add_cow_alert.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class AddCowAlertActivity : AppCompatActivity() {


    val success = "Alerta cargada"
    val fail = "No se pudo cargar la alerta"

    inner class Tarea : AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg params: Void?): Void? {

            var request = OkHttpRequest(OkHttpClient())

            var js = JSONObject()
            js.put("cowId", valueCowId.text.toString().toLongOrNull())
            js.put("bcsThresholdMax", maxBcs.text.toString().toBigDecimalOrNull())
            js.put("bcsThresholdMin", minBcs.text.toString().toBigDecimalOrNull())

            val conexion = "http://" + getSharedPreferences(
                ConfiguracionUrlActivity.PREFS_FILENAME,
                Context.MODE_PRIVATE
            ).getString("address", "") + "/api/cowAlert"

            request.POST(conexion, js, object : Callback {
                override fun onResponse(call: Call?, response: Response) {
                    val responseData = response.body()?.string()
                    runOnUiThread {
                        try {
                            var json = JSONObject(responseData)
                            valueId.setText(json.getString("id"))
                            valueCowId.setText(json.getString("animalId"))
                            maxBcs.setText(json.getString("bcsThresholdMax"))
                            minBcs.setText(json.getString("bcsThresholdMin"))


                            message.setText(success)
                            layoutIdAddCow.setVisibility(View.VISIBLE)
                            message.setBackgroundColor(Color.GREEN)
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            layoutIdAddCow.setVisibility(View.INVISIBLE)
                            message.setText(fail)
                            message.setBackgroundColor(Color.RED)
                        }
                        addButton.isEnabled = true
                        addButton.setText("Cargar")
                    }
                }

                override fun onFailure(call: Call?, e: IOException?) {
                    println(e)
                    runOnUiThread {
                        layoutIdAddCow.setVisibility(View.INVISIBLE)
                        message.setText(fail)
                        message.setBackgroundColor(Color.RED)
                        addButton.isEnabled = true
                        addButton.setText("Cargar")
                    }
                }
            })

            return null
        }
    }

    var asyn: Tarea? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_cow_alert)
        if (savedInstanceState != null) {

            if (savedInstanceState.getString("message", "") == success) {
                layoutIdAddCow.setVisibility(View.VISIBLE)
                message.setBackgroundColor(Color.GREEN)
                valueId.setText(savedInstanceState.getString("alertId", ""))
            } else
                if (savedInstanceState.getString("message", "") == fail) {
                    message.setBackgroundColor(Color.RED)
                }
            message.setText(savedInstanceState.getString("message", ""))
        }

        addButton.setOnClickListener() {
            //ocultar teclado
            val view = this.currentFocus
            view?.let { v ->
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(v.windowToken, 0)
            }
            message.setText("Enviando datos")
            message.setBackgroundColor(Color.GREEN)
            addButton.isEnabled = false
            addButton.setText("Cargando...")

            asyn = Tarea()
            asyn?.execute()
        }
    }

    fun back(view: View) {
        finish()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        if (outState != null) {
            outState.putString("alertId", valueId.text.toString())
            outState.putString("message", message.text.toString())
        }
    }
}