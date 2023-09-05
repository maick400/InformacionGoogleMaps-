import android.util.Log
import com.google.android.gms.maps.model.LatLng
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList

class MarcadorMapaDetalle @Throws(JSONException::class)
constructor(jObjectLugar: JSONObject) {
    var place_id: String? = null
    var url_icono_tipo_lugar: String? = null
    var nombre: String? = null
    var ubicacion: String? = null
    var coordenadas: LatLng? = null
    var num_telefono: String? = null
    var rating: String? = null
    var url_fotos: ArrayList<String>? = null
    var horario: ArrayList<String>? = null
    var lugares_alrededor: ArrayList<MarcadorMapaDetalle>? = null

    init {
        val place_id = jObjectLugar.getString("place_id")
        val icon_url_lugar = jObjectLugar.getString("icon")

        val jObjectGeometry = jObjectLugar.getJSONObject("geometry")
        val jObjectLocation = jObjectGeometry.getJSONObject("location")

        var latLng = LatLng(0.0, 0.0)
        if (jObjectLocation.has("lat") && jObjectLocation.has("lng")) {
            latLng = LatLng(jObjectLocation.getDouble("lat"), jObjectLocation.getDouble("lng"))
        }

        val name_place = jObjectLugar.getString("name")

        this.nombre = name_place
        this.place_id = place_id
        this.coordenadas = latLng
        this.url_icono_tipo_lugar = icon_url_lugar
    }

    constructor() : super() {}

    companion object {
        fun get_placeid_por_latlng(lista_lugares: ArrayList<MarcadorMapaDetalle>?, latLng: LatLng?): String {
            var place_id = ""

            if (lista_lugares != null) {
                for (i in lista_lugares.indices) {
                    if (lista_lugares[i].coordenadas == latLng) {
                        place_id = lista_lugares[i].place_id!!
                    }
                }
            }

            return place_id
        }

        @Throws(JSONException::class)
        fun getJsonLugaresAlrededor(jsonArray: JSONArray): ArrayList<MarcadorMapaDetalle> {
            val jArrayResults = jsonArray

            val arr_Marcadores = ArrayList<MarcadorMapaDetalle>()

            for (i in 0 until jArrayResults.length()) {
                val jObjectPSeleccionado = jArrayResults.getJSONObject(i)

                val marcadorMapaDetalle = MarcadorMapaDetalle(jObjectPSeleccionado)
                arr_Marcadores.add(marcadorMapaDetalle)
            }

            return arr_Marcadores
        }
    }

    fun getMarcadorMapaPorID(place_id: String): MarcadorMapaDetalle? {
        var marcadorMapaDetalle: MarcadorMapaDetalle? = null
        for (i in lugares_alrededor!!.indices) {
            if (lugares_alrededor!![i].place_id == place_id) {
                marcadorMapaDetalle = lugares_alrededor!![i]
            }
        }

        return marcadorMapaDetalle
    }

    @Throws(JSONException::class)
    fun set_detalles_lugar(place_id: String?, jObjectLugarDetalle: JSONObject, API_KEY: String): MarcadorMapaDetalle {
        val marcadorMapaDetalleEditar = getMarcadorMapaPorID(place_id!!)

        val jArrayAdressComponents = jObjectLugarDetalle.getJSONArray("address_components")

        var direccion_completa = ""
        var pais = ""
        var ciudad = ""
        var ruta = ""
        var calle_numero = ""

        for (i in 0 until jArrayAdressComponents.length()) {
            val componente_direccion = jArrayAdressComponents.getJSONObject(i)

            val tipo_componente = componente_direccion.getJSONArray("types")

            for (j in 0 until tipo_componente.length()) {
                val tipo = tipo_componente.getString(j)

                Log.i("TEST-2", "TIPO = $tipo")

                if (tipo == "country") {
                    pais = componente_direccion.getString("long_name")
                }
                if (tipo == "locality") {
                    ciudad = componente_direccion.getString("long_name")
                }
                if (tipo == "route") {
                    ruta = componente_direccion.getString("long_name")
                }
                if (tipo == "street_number") {
                    calle_numero = componente_direccion.getString("long_name")
                }
            }
        }

        if (pais != "") direccion_completa += pais
        if (ciudad != "") direccion_completa += ", $ciudad"
        if (ruta != "") direccion_completa += ", $ruta"
        if (ruta != "") direccion_completa += " $calle_numero"

        marcadorMapaDetalleEditar!!.ubicacion = direccion_completa

        Log.i("TEST-2", "direccion_completa = $direccion_completa")

        if (jObjectLugarDetalle.has("opening_hours")) {
            marcadorMapaDetalleEditar.horario = ArrayList()
            val jObjectOpeningHours = jObjectLugarDetalle.getJSONObject("opening_hours")

            val jArrayWeekdayText = jObjectOpeningHours.getJSONArray("weekday_text")

            for (i in 0 until jArrayWeekdayText.length()) {
                marcadorMapaDetalleEditar.horario!!.add(jArrayWeekdayText[i] as String)
            }
        }
        if (jObjectLugarDetalle.has("photos")) {
            marcadorMapaDetalleEditar.url_fotos = ArrayList()

            val jArrayFotos = jObjectLugarDetalle.getJSONArray("photos")
            for (i in 0 until jArrayFotos.length()) {
                val jObjectFoto = jArrayFotos.getJSONObject(i)
                val foto_url = jObjectFoto.getString("photo_reference")
                Log.i("FOTOS", foto_url)

                marcadorMapaDetalleEditar.url_fotos!!.add(get_url_imagen(foto_url, API_KEY))
            }
        }
        if (jObjectLugarDetalle.has("formatted_phone_number")) {
            marcadorMapaDetalleEditar.num_telefono = jObjectLugarDetalle.getString("formatted_phone_number")
        }

        return marcadorMapaDetalleEditar
    }

    fun get_url_imagen(imagen_referencia: String, API_KEY: String): String {
        return "https://maps.googleapis.com/maps/api/place/photo?" +
                "maxwidth=400&photoreference=$imagen_referencia&" +
                "key=$API_KEY"
    }

    fun setPlace_id(place_id: String) {
        this.place_id = place_id
    }

    fun setUrl_icono_tipo_lugar(url_icono_tipo_lugar: String) {
        this.url_icono_tipo_lugar = url_icono_tipo_lugar
    }

    fun setNombre(nombre: String) {
        this.nombre = nombre
    }

    fun setRating(rating: String) {
        this.rating = rating
    }

    fun setUbicacion(ubicacion: String) {
        this.ubicacion = ubicacion
    }

    fun  setNum_telefono(num_telefono: String) {
        this.num_telefono = num_telefono
    }

    fun setCoordenadas(coordenadas: LatLng) {
        this.coordenadas = coordenadas
    }

    fun setUrl_fotos(url_fotos: ArrayList<String>) {
        this.url_fotos = url_fotos
    }

    fun setHorario(horario: ArrayList<String>) {
        this.horario = horario
    }

    fun setLugares_alrededor(lugares_alrededor: ArrayList<MarcadorMapaDetalle>) {
        this.lugares_alrededor = lugares_alrededor
    }

    fun getPlace_id(): String? {
        return place_id
    }

    fun getUrl_icono_tipo_lugar(): String? {
        return url_icono_tipo_lugar
    }

    fun getNombre(): String? {
        return nombre
    }

    fun getUbicacion(): String? {
        return ubicacion
    }

    fun getRating(): String? {
        return rating
    }

    fun getNum_telefono(): String? {
        return num_telefono
    }

    fun getCoordenadas(): LatLng? {
        return coordenadas
    }

    fun getUrl_fotos(): ArrayList<String>? {
        return url_fotos
    }

    fun getHorario(): ArrayList<String>? {
        return horario
    }

    fun getLugares_alrededor(): ArrayList<MarcadorMapaDetalle>? {
        return lugares_alrededor
    }
}