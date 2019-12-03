package com.example.trabajofinalmoviles

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompatSideChannelService
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_add_cow.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*

class AddCowActivity : AppCompatActivity() {

    val success = "Animal cargado"
    val fail = "No se pudo cargar el animal"

    private val CERO = "0"
    private val BARRA = "/"

    //Calendario para obtener fecha & hora
    val c = Calendar.getInstance()

    //Fecha
    val mes = c[Calendar.MONTH]
    val dia = c[Calendar.DAY_OF_MONTH]
    val anio = c[Calendar.YEAR]

    inner class Tarea: AsyncTask<Void, Int, Void>() {
        override fun doInBackground(vararg params: Void?): Void? {

            var request = OkHttpRequest(OkHttpClient())

            //verificacion de input de fechas
            var fecha1: String? = fechaNacView.text.toString()
            var fecha2: String? = ultimoPartoView.text.toString()

            val pattern = "[0-9]{2}/[0-9]{2}/[0-9]{4}".toRegex()

            if (pattern.matches(fecha1.toString()))
                fecha1 = toDateFormat(fechaNacView.text.toString())
            else
                fecha1 = null

            if (pattern.matches(fecha2.toString()))
                fecha2 = toDateFormat(ultimoPartoView.text.toString())
            else
                fecha2 = null

            var js = JSONObject()
            js.put("herdId", valueId.text.toString().toIntOrNull())
            js.put("cantidadPartos",cantPartosView.text.toString().toIntOrNull())
            js.put("electronicId",electronicIdView.text.toString())
            js.put("fechaNacimiento", fecha1)
            js.put("peso",pesoView.text.toString().toFloatOrNull())
            js.put("ultimaFechaParto", fecha2)

            request.POST("http://192.168.0.194:8080/api/cow", js,  object: Callback {
                override fun onResponse(call: Call?, response: Response) {
                    val responseData = response.body()?.string()
                    runOnUiThread{
                        try {
                            var json = JSONObject(responseData)
                            valueCowId.setText(json.getString("id"))
                            valueId.setText(json.getString("herdId"))
                            cantPartosView.setText(json.getString("cantidadPartos"))
                            electronicIdView.setText(json.getString("electronicId"))
                            fechaNacView.setText(toDateFormatView(json.getString("fechaNacimiento")))
                            pesoView.setText(json.getString("peso"))

                            if (json.getString("ultimaFechaParto") == "null")
                                ultimoPartoView.setText("dd/mm/yyyy")
                            else
                                ultimoPartoView.setText(toDateFormatView(json.getString("ultimaFechaParto")))

                            message.setText(success)
                            layoutIdAddCow.setVisibility(View.VISIBLE)
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
        setContentView(R.layout.activity_add_cow)
        if (savedInstanceState != null) {

            valueCowId.setText(savedInstanceState.getString("cowId",""))
            valueId.setText(savedInstanceState.getString("herdId",""))
            cantPartosView.setText(savedInstanceState.getString("cantidadPartos",""))
            electronicIdView.setText(savedInstanceState.getString("electronicId",""))
            fechaNacView.setText(savedInstanceState.getString("fechaNacimiento",""))
            pesoView.setText(savedInstanceState.getString("peso",""))
            ultimoPartoView.setText(savedInstanceState.getString("ultimaFechaParto",""))

            if (savedInstanceState.getString("message", "") == success) {
                layoutIdAddCow.setVisibility(View.VISIBLE)
                message.setBackgroundColor(Color.GREEN)
            }else
                if (savedInstanceState.getString("message", "") == fail) {
                    message.setBackgroundColor(Color.RED)
                }
            message.setText(savedInstanceState.getString("message", ""))
        }

        ib_obtener_fechaNac.setOnClickListener(){
            obtenerFecha(fechaNacView)
        }

        ib_obtener_fechaParto.setOnClickListener(){
            obtenerFecha(ultimoPartoView)
        }

        addButton.setOnClickListener(){
            //ocultar teclado
            val view = this.currentFocus
            view?.let { v ->
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(v.windowToken, 0)
            }

            asyn = Tarea()
            asyn?.execute()
        }
    }

    fun back(view: View){
        finish()
    }

    private fun toDateFormat(fecha : String): String{
        return ""+fecha[6]+fecha[7]+fecha[8]+fecha[9]+"-"+fecha[3]+fecha[4]+"-"+fecha[0]+fecha[1]
    }

    private fun toDateFormatView(fecha : String): String{
        return ""+fecha[8]+fecha[9]+"/"+fecha[5]+fecha[6]+"/"+fecha[0]+fecha[1]+fecha[2]+fecha[3]
    }

    private fun obtenerFecha(v: EditText) {
        val recogerFecha = DatePickerDialog(this,
            OnDateSetListener { view, year, month, dayOfMonth ->
                val mesActual = month + 1
                val diaFormateado =
                    if (dayOfMonth < 10) CERO + dayOfMonth.toString() else dayOfMonth.toString()
                val mesFormateado =
                    if (mesActual < 10) CERO + mesActual.toString() else mesActual.toString()
                v.setText(diaFormateado + BARRA + mesFormateado + BARRA + year)
            }, anio, mes, dia
        )
        recogerFecha.show()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        if (outState != null) {
            outState.putString("cowId", valueCowId.text.toString())
            outState.putString("herdId",valueId.text.toString())
            outState.putString("cantidadPartos",cantPartosView.text.toString())
            outState.putString("electronicId",electronicIdView.text.toString())
            outState.putString("fechaNacimiento",fechaNacView.text.toString())
            outState.putString("peso",pesoView.text.toString())
            outState.putString("ultimaFechaParto",ultimoPartoView.text.toString())

            outState.putString("message", message.text.toString())
            outState.putBoolean("layoutIdVisibility", layoutIdAddCow.isVisible)
        }
    }
}
