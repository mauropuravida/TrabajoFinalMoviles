package com.example.trabajofinalmoviles

import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_add_herd.*
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap
import kotlin.concurrent.withLock


class addHerdActivity : AppCompatActivity() {

    inner class Tarea: AsyncTask<Void, Int, Void>() {
        override fun doInBackground(vararg params: Void?): Void? {

            var request = OkHttpRequest(OkHttpClient())
            request.GET("http://10.13.23.189:8080/api/herd/2",  object: Callback{
                override fun onResponse(call: Call?, response: Response) {
                    val responseData = response.body()?.string()
                    runOnUiThread{
                        try {
                            var json = JSONObject(responseData)
                            println(json.getString("location"))
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                }
                override fun onFailure(call: Call?, e: IOException?) {
                    println(e)
                }
            })

            /*var request = OkHttpRequest(OkHttpClient())
            var values = HashMap<String, String>()
            values.put("location","prueba")
            request.POST("http://10.13.23.189:8080/api/herd", values,  object: Callback{
                override fun onResponse(call: Call?, response: Response) {
                    val responseData = response.body()?.string()
                    runOnUiThread{
                        try {
                            var json = JSONObject(responseData)
                            println(json.getString("location"))
                        } catch (e: JSONException) {
                            e.printStackTrace()

                        }
                    }
                }
                override fun onFailure(call: Call?, e: IOException?) {
                    println(e)
                }

            })*/

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

        addButton.setOnClickListener(){
            asyn = Tarea()
            asyn?.execute()
        }
    }

    fun back(view: View){
        finish()
    }

}
