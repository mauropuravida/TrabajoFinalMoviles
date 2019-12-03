package com.example.trabajofinalmoviles

import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.marginTop
import kotlinx.android.synthetic.main.activity_get_herd.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class GetHerdActivity : AppCompatActivity() {

    val success = "Rodeo encontrado"
    val fail = "No se encontro el rodeo"

    protected fun mostrarVaca(v: JSONObject){
        val id = v.getInt("id")
        val electronicId = v.getInt("electronicId")
        val cc = v.getDouble("cc")

        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 2)
        layoutParams.setMargins(5, 35, 0, 35)

        val layoutParamsButton = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParamsButton.weight = 0f

        val layoutParamsText = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParamsText.weight = 1f

        //Linea separadora (al inicio)
        val linea = View(applicationContext)
        linea.layoutParams = layoutParams
        linea.setBackgroundColor(Color.parseColor("#ff9703"))
        vacas.addView(linea)

        //El layout Horizontal para los datos de esta vaca
        val ll = LinearLayout(applicationContext)
        ll.orientation = LinearLayout.HORIZONTAL

        //Texto con los datos
        val textoId = TextView(applicationContext)
        textoId.textSize = 20f; textoId.setTextColor(Color.parseColor("#ffffff"))
        textoId.text = Html.fromHtml("Id: <b>$id</b> - ElectronicId: <b>$electronicId</b> - CC: <b>$cc</b>", Html.FROM_HTML_MODE_COMPACT)
        textoId.layoutParams = layoutParamsText
        ll.addView(textoId)

        //Botón para ver más detalles
        val botonDetalles = Button(applicationContext)
        botonDetalles.layoutParams = layoutParamsButton
        botonDetalles.text = "Ver más"
        botonDetalles.setOnClickListener {
            val intent: Intent = Intent(this, GetCowActivity::class.java)
            intent.putExtra("cowId", id)
            startActivity(intent)
        }
        ll.addView(botonDetalles)

        vacas.addView(ll)
    }

    inner class Tarea: AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg params: Void?): Void? {

            var request = OkHttpRequest(OkHttpClient())

            request.GET("http://192.168.0.194:8080/api/herd/"+valueId.text.toString(),  object: Callback {
                override fun onResponse(call: Call?, response: Response) {
                    val responseData = response.body()?.string()
                    runOnUiThread{
                        vacas.removeAllViews()
                        try {
                            var json = JSONObject(responseData)
                            valueId.setText(json.getString("id"))
                            location.setText(json.getString("location"))
                            bcspromedio.setText(json.getDouble("bcsPromedio").toString())

                            layoutlocation.setVisibility(View.VISIBLE)
                            layoutbcs.setVisibility(View.VISIBLE)

                            message.setText(success)
                            message.setBackgroundColor(Color.GREEN)

                            val vacasArray = json.getJSONArray("cows")

                            if (vacasArray.length() > 0) {
                                scrollvacas.setVisibility(View.VISIBLE)
                                tituloVacas.text = "Vacas del rodeo:"
                                for (i in 0 until vacasArray.length()) {
                                    mostrarVaca(vacasArray.getJSONObject(i))
                                }
                            }else{
                                tituloVacas.text = "No hay vacas en este rodeo"
                                scrollvacas.setVisibility(View.GONE)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            tituloVacas.text = ""
                            message.setText(fail)
                            message.setBackgroundColor(Color.RED)
                            layoutlocation.setVisibility(View.GONE)
                            layoutbcs.setVisibility(View.GONE)
                            scrollvacas.setVisibility(View.GONE)
                        }
                        getButton.isEnabled = true
                    }
                }
                override fun onFailure(call: Call?, e: IOException?) {
                    println(e)
                    runOnUiThread {
                        vacas.removeAllViews()
                        tituloVacas.text = ""
                        message.setText(fail)
                        message.setBackgroundColor(Color.RED)
                        layoutlocation.setVisibility(View.GONE)
                        layoutbcs.setVisibility(View.GONE)
                        getButton.isEnabled = true
                        scrollvacas.setVisibility(View.GONE)
                    }
                }
            })

            return null
        }
    }

    var asyn: Tarea? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_herd)

        getButton.setOnClickListener{
            getButton.isEnabled = false
            asyn = Tarea()
            asyn?.execute()
        }
    }

    fun back(view: View) {
        finish()
    }
}
