package com.example.trabajofinalmoviles

import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_add_herd.*
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import kotlin.collections.HashMap


class addHerdActivity : AppCompatActivity() {

    val success = "Rodeo cargado"
    val fail = "No se pudo cargar el rodeo"

    inner class Tarea: AsyncTask<Void, Int, Void>() {
        override fun doInBackground(vararg params: Void?): Void? {

            var request = OkHttpRequest(OkHttpClient())

            var values = HashMap<String, String>()
            values.put("location",loc.text.toString())
            request.POST("http://192.168.0.194:8080/api/herd", values,  object: Callback{
                override fun onResponse(call: Call?, response: Response) {
                    val responseData = response.body()?.string()
                    runOnUiThread{
                        try {
                            var json = JSONObject(responseData)
                            valueId.setText(json.getString("id"))
                            loc.setText(json.getString("location"))
                            message.setText(success)
                            layoutId.setVisibility(View.VISIBLE)
                            message.setBackgroundColor(Color.GREEN)
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            message.setText(fail)
                            message.setBackgroundColor(Color.RED)
                        }
                    }
                }
                override fun onFailure(call: Call?, e: IOException?) {
                    println(e)
                }
            })

            return null
        }
        override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)
        }
    }

    var asyn: Tarea? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_herd)
        if (savedInstanceState != null) {

            loc.setText(savedInstanceState.getString("location",""))
            valueId.setText(savedInstanceState.getString("herdId",""))

            if (savedInstanceState.getBoolean("layoutIdVisibility")) {
                layoutId.setVisibility(View.VISIBLE)
                message.setText(savedInstanceState.getString("message", ""))
                if (savedInstanceState.getString("message", "") == success)
                    message.setBackgroundColor(Color.GREEN)
                else
                    message.setBackgroundColor(Color.RED)
            }
        }

        addButton.setOnClickListener(){
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
            outState.putBoolean("layoutIdVisibility", layoutId.isVisible)
        }
    }

}
