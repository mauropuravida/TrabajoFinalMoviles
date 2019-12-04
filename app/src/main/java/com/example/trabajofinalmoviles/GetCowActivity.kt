package com.example.trabajofinalmoviles

import android.content.Context
import android.graphics.Color
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_get_cow.*
import kotlinx.android.synthetic.main.activity_get_cow.cantPartosView
import kotlinx.android.synthetic.main.activity_get_cow.electronicIdView
import kotlinx.android.synthetic.main.activity_get_cow.fechaNacView
import kotlinx.android.synthetic.main.activity_get_cow.message
import kotlinx.android.synthetic.main.activity_get_cow.pesoView
import kotlinx.android.synthetic.main.activity_get_cow.ultimoPartoView
import kotlinx.android.synthetic.main.activity_get_cow.valueId
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.text.ParsePosition
import java.text.SimpleDateFormat

class GetCowActivity : AppCompatActivity() {

    val success = "Animal encontrado"
    val fail = "No se encontro el animal"

    inner class Tarea: AsyncTask<Void, Int, Void>() {
        override fun doInBackground(vararg params: Void?): Void? {

            var request = OkHttpRequest(OkHttpClient())

            val conexion = "http://"+getSharedPreferences(ConfiguracionUrlActivity.PREFS_FILENAME, Context.MODE_PRIVATE).getString("address", "")+"/api/cow/"

            request.GET(conexion+valueId.text.toString(),  object: Callback {
                override fun onResponse(call: Call?, response: Response) {
                    val responseData = response.body()?.string()
                    runOnUiThread{
                        try {
                            var json = JSONObject(responseData)
                            valueId.setText(json.getString("id"))
                            herdIdView.setText(json.getString("herdId"))
                            cantPartosView.setText(json.getString("cantidadPartos"))
                            electronicIdView.setText(json.getString("electronicId"))
                            fechaNacView.setText(toDateFormatView(json.getString("fechaNacimiento")))
                            pesoView.setText(json.getString("peso"))

                            if (json.getString("ultimaFechaParto") == "null")
                                ultimoPartoView.setText("dd/mm/yyyy")
                            else
                                ultimoPartoView.setText(toDateFormatView(json.getString("ultimaFechaParto")))

                            layoutElectronicId.setVisibility(View.VISIBLE)
                            layoutFechaNac.setVisibility(View.VISIBLE)
                            layoutCantPartos.setVisibility(View.VISIBLE)
                            layoutHerdId.setVisibility(View.VISIBLE)
                            layoutPeso.setVisibility(View.VISIBLE)
                            layoutUltimoParto.setVisibility(View.VISIBLE)

                            //si posee condicion corporal
                            if (json.getString("fechaBcs") != "null"){
                                cowBcsIdView.setText(json.getString("cowBcsId"))
                                fechaBcsView.setText(toDateFormatView(json.getString("fechaBcs")))
                                CCView.setText(json.getString("cc"))

                                layoutFechaBCS.setVisibility(View.VISIBLE)
                                layoutBCSId.setVisibility(View.VISIBLE)
                                layoutCC.setVisibility(View.VISIBLE)
                            }

                            if (getIntent().getIntExtra("cowId", 0) == 0 ) {
                                message.setText(success)
                                message.setBackgroundColor(Color.GREEN)
                            }

                        } catch (e: JSONException) {
                            e.printStackTrace()
                            message.setText(fail)
                            message.setBackgroundColor(Color.RED)
                            layoutElectronicId.setVisibility(View.GONE)
                            layoutFechaNac.setVisibility(View.GONE)
                            layoutCantPartos.setVisibility(View.GONE)
                            layoutHerdId.setVisibility(View.GONE)
                            layoutPeso.setVisibility(View.GONE)
                            layoutUltimoParto.setVisibility(View.GONE)
                            layoutFechaBCS.setVisibility(View.GONE)
                            layoutBCSId.setVisibility(View.GONE)
                            layoutCC.setVisibility(View.GONE)
                        }
                        getButton.setText("Consultar")
                        getButton.isEnabled = true
                    }
                }
                override fun onFailure(call: Call?, e: IOException?) {
                    println(e)
                    runOnUiThread{
                        message.setText(fail)
                        message.setBackgroundColor(Color.RED)
                        layoutElectronicId.setVisibility(View.GONE)
                        layoutFechaNac.setVisibility(View.GONE)
                        layoutCantPartos.setVisibility(View.GONE)
                        layoutHerdId.setVisibility(View.GONE)
                        layoutPeso.setVisibility(View.GONE)
                        layoutUltimoParto.setVisibility(View.GONE)
                        layoutFechaBCS.setVisibility(View.GONE)
                        layoutBCSId.setVisibility(View.GONE)
                        layoutCC.setVisibility(View.GONE)
                        getButton.setText("Consultar")
                        getButton.isEnabled = true
                    }
                }
            })

            return null
        }
    }

    var asyn: Tarea? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_cow)
        if (savedInstanceState != null) {

            valueId.setText(savedInstanceState.getString("cowId",""))
            herdIdView.setText(savedInstanceState.getString("herdId",""))
            cantPartosView.setText(savedInstanceState.getString("cantidadPartos",""))
            electronicIdView.setText(savedInstanceState.getString("electronicId",""))
            fechaNacView.setText(savedInstanceState.getString("fechaNacimiento",""))
            pesoView.setText(savedInstanceState.getString("peso",""))
            ultimoPartoView.setText(savedInstanceState.getString("ultimaFechaParto",""))

            cowBcsIdView.setText(savedInstanceState.getString("cowBcsId",""))
            fechaBcsView.setText(savedInstanceState.getString("fechaBCS",""))
            CCView.setText(savedInstanceState.getString("cc",""))

            if (savedInstanceState.getString("message", "") == success) {

                layoutElectronicId.setVisibility(View.VISIBLE)
                layoutFechaNac.setVisibility(View.VISIBLE)
                layoutCantPartos.setVisibility(View.VISIBLE)
                layoutHerdId.setVisibility(View.VISIBLE)
                layoutPeso.setVisibility(View.VISIBLE)
                layoutUltimoParto.setVisibility(View.VISIBLE)

                if (savedInstanceState.getString("fechaBCS","") == "dd/mm/yyyy") {
                    layoutFechaBCS.setVisibility(View.VISIBLE)
                    layoutBCSId.setVisibility(View.VISIBLE)
                    layoutCC.setVisibility(View.VISIBLE)
                }

                message.setBackgroundColor(Color.GREEN)
            }else
                if (savedInstanceState.getString("message", "") == fail) {
                    message.setBackgroundColor(Color.RED)
                }
            message.setText(savedInstanceState.getString("message", ""))
        }

        getButton.setOnClickListener(){
            //ocultar teclado
            val view = this.currentFocus
            view?.let { v ->
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(v.windowToken, 0)
            }
            message.setText("Realizando consulta")
            message.setBackgroundColor(Color.GREEN)
            getButton.setText("Consultando...")
            getButton.isEnabled = false
            asyn = Tarea()
            asyn?.execute()
        }


        if (getIntent().getIntExtra("cowId", 0) > 0 ){
            valueId.setText(getIntent().getIntExtra("cowId", 0).toString())
            asyn = Tarea()
            asyn?.execute()
        }
    }

    fun back(view: View) {
        finish()
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

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        if (outState != null) {
            outState.putString("cowId", valueId.text.toString())
            outState.putString("herdId",herdIdView.text.toString())
            outState.putString("cantidadPartos",cantPartosView.text.toString())
            outState.putString("electronicId",electronicIdView.text.toString())
            outState.putString("fechaNacimiento",fechaNacView.text.toString())
            outState.putString("peso",pesoView.text.toString())
            outState.putString("ultimaFechaParto",ultimoPartoView.text.toString())

            outState.putString("cowBcsId",cowBcsIdView.text.toString())
            outState.putString("fechaBCS",fechaBcsView.text.toString())
            outState.putString("cc",CCView.text.toString())

            outState.putString("message", message.text.toString())
            outState.putBoolean("layoutElectronicIdVisibility", layoutElectronicId.isVisible)
            outState.putBoolean("layoutFechaNacVisibility", layoutElectronicId.isVisible)
            outState.putBoolean("layoutCantPartosVisibility", layoutElectronicId.isVisible)
            outState.putBoolean("layoutHerdIdVisibility", layoutElectronicId.isVisible)
            outState.putBoolean("layoutPesoVisibility", layoutElectronicId.isVisible)
            outState.putBoolean("layoutUltimoPartoVisibility", layoutElectronicId.isVisible)
            outState.putBoolean("layoutFechaBCSVisibility", layoutElectronicId.isVisible)
            outState.putBoolean("layoutBCSIdVisibility", layoutElectronicId.isVisible)
            outState.putBoolean("layoutCCVisibility", layoutElectronicId.isVisible)
        }
    }
}

