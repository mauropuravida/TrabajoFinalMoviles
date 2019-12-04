package com.example.trabajofinalmoviles

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_get_herd.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class GetHerdActivity : AppCompatActivity() {

    val success = "Rodeo encontrado"
    val fail = "No se encontro el rodeo"

    var cowsInHerd: JSONArray? = null

    protected fun mostrarVaca(v: JSONObject){
        val id = v.getInt("id")
        val electronicId = v.getInt("electronicId")
        val cc = v.getDouble("cc")

        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 2)
        layoutParams.setMargins(0, 35, 0, 35)

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

            val conexion = "http://"+getSharedPreferences(ConfiguracionUrlActivity.PREFS_FILENAME, Context.MODE_PRIVATE).getString("address", "")+"/api/herd/"

            request.GET(conexion+valueId.text.toString(),  object: Callback {
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
                            cowsInHerd = vacasArray

                            if (vacasArray.length() > 0) {
                                vacas.setVisibility(View.VISIBLE)
                                tituloVacas.text = "Vacas del rodeo:"
                                for (i in 0 until vacasArray.length()) {
                                    mostrarVaca(vacasArray.getJSONObject(i))
                                }
                            }else{
                                tituloVacas.text = "No hay vacas en este rodeo"
                                vacas.setVisibility(View.GONE)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            tituloVacas.text = ""
                            message.setText(fail)
                            message.setBackgroundColor(Color.RED)
                            layoutlocation.setVisibility(View.GONE)
                            layoutbcs.setVisibility(View.GONE)
                            vacas.setVisibility(View.GONE)
                        }
                        getButton.setText("Consultar")
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
                        vacas.setVisibility(View.GONE)
                        getButton.isEnabled = true
                        getButton.setText("Consultar")
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
        if (savedInstanceState != null) {

            if (savedInstanceState.getString("message", "") == success) {
                message.setBackgroundColor(Color.GREEN)

                valueId.setText(savedInstanceState.getString("herdId", ""))
                location.setText(savedInstanceState.getString("location", ""))
                bcspromedio.setText(savedInstanceState.getString("bcsPromedio", ""))

                layoutlocation.setVisibility(View.VISIBLE)
                layoutbcs.setVisibility(View.VISIBLE)

                if (savedInstanceState.getBoolean("scrollView")){
                    vacas.setVisibility(View.VISIBLE)

                    val cows = JSONArray(savedInstanceState.getString("json", ""))
                    cowsInHerd = cows

                    for (i in 0 until cows.length())
                        mostrarVaca(cows.getJSONObject(i))
                }

            }else
                if (savedInstanceState.getString("message", "") == fail) {
                    message.setBackgroundColor(Color.RED)
                }
            message.setText(savedInstanceState.getString("message", ""))
            tituloVacas.setText(savedInstanceState.getString("tituloVacas", ""))
        }

        getButton.setOnClickListener{
            //ocultar teclado
            val view = this.currentFocus
            view?.let { v ->
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(v.windowToken, 0)
            }
            message.setText("Realizando consulta")
            message.setBackgroundColor(Color.GREEN)
            getButton.isEnabled = false
            getButton.setText("Consultando...")

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
            outState.putString("herdId", valueId.text.toString())
            outState.putString("location", location.text.toString())
            outState.putString("bcsPromedio", bcspromedio.text.toString())
            outState.putString("message", message.text.toString())
            outState.putString("tituloVacas", tituloVacas.text.toString())
            outState.putBoolean("scrollView", vacas.isVisible)
            outState.putString("json", cowsInHerd.toString())
        }
    }
}
