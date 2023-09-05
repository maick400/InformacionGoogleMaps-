package com.example.googlemapsetiquetas.Adaptadores

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.googlemapsetiquetas.Modelos.MarcadorMapaDetalle
import com.example.googlemapsetiquetas.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class WindowInfoAdapter(private val ctx: Context, private val marcadorMapaDetalle: MarcadorMapaDetalle) : GoogleMap.InfoWindowAdapter {
    private val mView: View = LayoutInflater.from(ctx).inflate(R.layout.window_info_adapter_mapa, null)

    private fun setInformacionEnLayout(marker: Marker, view: View) {
        val txtLugar: TextView = view.findViewById(R.id.txtLugar)
        val txtHorario: TextView = view.findViewById(R.id.txtHorarios)
        val txtUbicacion: TextView = view.findViewById(R.id.txtUbicacion)
        val txtTelefono: TextView = view.findViewById(R.id.txtTelefono)
        val txtCoordenadas: TextView = view.findViewById(R.id.txtCoordenadas)

        val imgLogo: ImageView = view.findViewById(R.id.imgLogo)

        marcadorMapaDetalle?.let {
            txtLugar.text = it.nombre
            txtUbicacion.text = it.ubicacion

            it.num_telefono?.let { telefono ->
                txtTelefono.text = "Teléfono: $telefono"
            }

            txtCoordenadas.text = "Coordenadas: \n" +
                    "Latitud: ${it.coordenadas?.latitude}\n" +
                    "Longitud: ${it.coordenadas?.longitude}"

            it.url_fotos?.let { urlFotos ->
                if (urlFotos.isNotEmpty()) {
                    Glide.with(ctx)
                        .load(urlFotos[0])
                        .into(imgLogo)
                }
            }

            it.horario?.let { horario ->
                if (horario.isNotEmpty()) {
                    val horarioString = horario.joinToString("\n")
                    txtHorario.text = "Horario: \n$horaString"
                }
            }
        }
    }

    override fun getInfCont(marker: Marker): View {
        setInformacionEnLayout(marker, mView)
        return mView
    }

    override fun getInfo(marker: Marker): View {
        setInformacionEnLayout(marker, mView)
        return mView
    }
}
