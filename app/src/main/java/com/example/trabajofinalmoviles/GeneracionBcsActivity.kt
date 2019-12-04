package com.example.trabajofinalmoviles

import android.content.Context
import android.graphics.Color
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_generacion_bcs.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class GeneracionBcsActivity : AppCompatActivity() {

    inner class Tarea: AsyncTask<Boolean, Void, Void>() {
        override fun doInBackground(vararg params: Boolean?): Void? {

            var request = OkHttpRequest(OkHttpClient())

            var conexion = "http://"+getSharedPreferences(ConfiguracionUrlActivity.PREFS_FILENAME, Context.MODE_PRIVATE).getString("address", "")+"/api/session"

            val callback = object: Callback {
                override fun onResponse(call: Call?, response: Response) {
                    val responseData = response.body()?.string()
                    runOnUiThread{
                        try {
                            val estado = (responseData == "true")
                            switchActivacion.isChecked = estado
                            if (estado) {
                                message.setText("ACTIVO")
                                message.setBackgroundColor(Color.GREEN)
                            }
                            else{
                                message.setText("INACTIVO")
                                message.setBackgroundColor(Color.RED)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            if (params[0] != null)
                                switchActivacion.isChecked = !params[0]!!
                            message.setText("ERROR AL CONECTARSE")
                            message.setBackgroundColor(Color.RED)
                        }
                        switchActivacion.isEnabled = true
                    }
                }
                override fun onFailure(call: Call?, e: IOException?) {
                    println(e)
                    runOnUiThread{
                        switchActivacion.isEnabled = true
                        if (params[0] != null)
                            switchActivacion.isChecked = !params[0]!!
                        message.setText("ERROR AL CONECTARSE")
                        message.setBackgroundColor(Color.RED)
                    }
                }
            }

            runOnUiThread {
                switchActivacion.isEnabled = false
            }

            if (params[0] == null)
                request.GET(conexion, callback)
            else {
                var js = JSONObject()
                js.put("enable", params[0])
                request.POST(conexion, js, callback)
            }

            return null
        }
    }

    var asyn: Tarea? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generacion_bcs)

        asyn = Tarea()
        asyn?.execute(null)

        switchActivacion.setOnClickListener { _ ->
            asyn = Tarea()
            asyn?.execute(switchActivacion.isChecked)
        }
    }

    fun back(view: View) {
        finish()
    }
}
