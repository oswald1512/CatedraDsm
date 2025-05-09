package sv.edu.udb.fichaseducativas.HT192210.MU192557.RH190164.CT142074

import java.io.Serializable

data class FichaEducativa(
    val id: String = "",
    val tematicaId: String = "",
    val pregunta: String = "",
    val respuesta: String = "",
    val imagen: String = ""
) : Serializable