package com.example.trabajofinalmoviles

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_add_cow.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.text.ParsePosition
import java.text.SimpleDateFormat
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

    inner class Tarea: AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg params: Void?): Void? {

            var request = OkHttpRequest(OkHttpClient())

            //verificacion de input de fechas
            val fecha1: String? = toDateFormat(fechaNacView.text.toString())
            var fecha2: String? = toDateFormat(ultimoPartoView.text.toString())


            val cantidadDePartos = cantPartosView.text.toString().toIntOrNull()
            if (cantidadDePartos == null || cantidadDePartos == 0)
                fecha2 = null


            var js = JSONObject()
            js.put("herdId", valueId.text.toString().toIntOrNull())
            js.put("cantidadPartos",cantidadDePartos)
            js.put("electronicId",electronicIdView.text.toString())
            js.put("fechaNacimiento", fecha1)
            js.put("peso",pesoView.text.toString().toFloatOrNull())
            js.put("ultimaFechaParto", fecha2)

            val conexion = "http://"+getSharedPreferences(ConfiguracionUrlActivity.PREFS_FILENAME, Context.MODE_PRIVATE).getString("address", "")+"/api/cow"

            request.POST(conexion, js,  object: Callback {
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

        fechaNacView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                obtenerFecha(fechaNacView)
                true
            }else {
                false
            }
        }

        ultimoPartoView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                obtenerFecha(ultimoPartoView)
                true
            }else {
                false
            }
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
            addButton.isEnabled = false
            addButton.setText("Cargando...")

            asyn = Tarea()
            asyn?.execute()
        }
    }

    fun back(view: View){
        finish()
    }

    private fun toDateFormat(fecha : String?): String?{
        return formatoFecha(fecha, "dd/MM/yyyy", "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    }

    private fun toDateFormatView(fecha : String): String?{
        return formatoFecha(fecha, "yyyy-MM-dd'T'HH:mm:ss.SSSZ", "dd/MM/yyyy")
    }

    private fun formatoFecha(fecha: String?, patronEntrada: String, patronSalida: String): String?{
        if (fecha == null) return null

        val pos = ParsePosition(0)
        var simpledateformat = SimpleDateFormat(patronEntrada)
        val date = simpledateformat.parse(fecha, pos) //Hacer un Date con la fecha recibido
        if (date == null) return null //Si no parseó bien, retornar null

        simpledateformat = SimpleDateFormat(patronSalida)
        return simpledateformat.format(date) //Retornar Date formateado con el formato de salida
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
