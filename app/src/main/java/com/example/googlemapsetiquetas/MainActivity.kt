package com.example.marcadores_e_informacion_googlemap

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : AppCompatActivity() {
    private lateinit var mMap2: GoogleMap
    private var mapFragment: SupportMapFragment?=null
    lateinit var adapter:InfoWindowAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpMap()

    }
       private fun BitmapConverter(view: View):Bitmap?{
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val bitmap=Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas= Canvas(bitmap)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        view.draw(canvas)
        return bitmap
    }
    
    private fun Map(){
        adapter= InfoWindowAdapter(this)
        mapFragment=supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(adapter)
        val lstFacultades = ArrayList<ModelFacultad>()
        var marker:MarkerOptions
        var coordenadas:LatLng
        val cola = Volley.newRequestQueue(this)
        var url="https://62ff92289350a1e548e1bee5.mockapi.io/Uteq_locations"
        var request= JsonArrayRequest(Request.Method.GET,url,null,{
                respuesta->try{
            for (i in 0 until respuesta.length()){
                var item = respuesta.getJSONObject(i)
                lstFacultades.add(ModelFacultad(item))
            }

            adapter.marcadores(lstFacultades)

        }catch (error:Exception){
            Toast.makeText(this,
                "Error al cargar los datos "+error.message,
                Toast.LENGTH_SHORT).show()
        }
        }, {
            Toast.makeText(this,
                "Error al cargar los datos",
                Toast.LENGTH_SHORT).show()
        })
        cola.add(request)
    }

 
}