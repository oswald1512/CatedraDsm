package sv.edu.udb.fichaseducativas.HT192210.MU192557.RH190164.CT142074

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.FirebaseDatabase

class AgregarTematicaActivity : AppCompatActivity() {

    private var tematicaId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_tematica)

        val etNombre = findViewById<TextInputEditText>(R.id.etNombreTematica)
        val etDescripcion = findViewById<TextInputEditText>(R.id.etDescripcionTematica)
        val btnAgregar = findViewById<Button>(R.id.btnAgregarTematica)

        // Si venimos a editar, rellenar los campos
        tematicaId = intent.getStringExtra("tematicaId")
        if (tematicaId != null) {
            etNombre.setText(intent.getStringExtra("nombre") ?: "")
            etDescripcion.setText(intent.getStringExtra("descripcion") ?: "")
            btnAgregar.text = "Actualizar"
        }

        btnAgregar.setOnClickListener {
            val nombre = etNombre.text.toString()
            val descripcion = etDescripcion.text.toString()
            if (nombre.isNotEmpty()) {
                val ref = FirebaseDatabase.getInstance().getReference("tematicas")
                val id = tematicaId ?: ref.push().key!!
                val tematica = mapOf(
                    "id" to id,
                    "nombre" to nombre,
                    "descripcion" to descripcion
                )
                ref.child(id).setValue(tematica)
                    .addOnSuccessListener {
                        Toast.makeText(this, if (tematicaId == null) "Temática agregada" else "Temática actualizada", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error al guardar temática", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show()
            }
        }
    }
}