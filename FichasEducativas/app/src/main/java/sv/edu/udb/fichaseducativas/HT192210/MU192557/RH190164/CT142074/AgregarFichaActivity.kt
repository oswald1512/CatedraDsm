package sv.edu.udb.fichaseducativas.HT192210.MU192557.RH190164.CT142074

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream

class AgregarFichaActivity : AppCompatActivity() {

    private var fichaId: String? = null
    private var tematicaId: String = ""
    private var imagenUrl: String = ""
    private val PICK_IMAGE_REQUEST = 1
    private val STORAGE_PERMISSION_REQUEST = 2
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_ficha)

        // Obtener el ID de la temática
        tematicaId = intent.getStringExtra("tematicaId") ?: ""

        val etPregunta = findViewById<TextInputEditText>(R.id.etPregunta)
        val etRespuesta = findViewById<TextInputEditText>(R.id.etRespuesta)
        val btnAgregar = findViewById<Button>(R.id.btnAgregarFicha)
        val btnImagen = findViewById<Button>(R.id.btnAgregarImagen)
        val ivPreview = findViewById<ImageView>(R.id.ivPreviewImagen)

        // Si venimos a editar, rellenar los campos
        fichaId = intent.getStringExtra("fichaId")
        if (fichaId != null && fichaId!!.isNotEmpty()) {
            etPregunta.setText(intent.getStringExtra("pregunta") ?: "")
            etRespuesta.setText(intent.getStringExtra("respuesta") ?: "")
            imagenUrl = intent.getStringExtra("imagen") ?: ""
            if (imagenUrl.isNotEmpty()) {
                ivPreview.visibility = ImageView.VISIBLE
                Picasso.get()
                    .load(imagenUrl)
                    .into(ivPreview)
            }
            btnAgregar.text = "Actualizar"
        }

        btnImagen.setOnClickListener {
            if (checkStoragePermission()) {
                abrirSelectorImagen()
            } else {
                requestStoragePermission()
            }
        }

        btnAgregar.setOnClickListener {
            val pregunta = etPregunta.text.toString()
            val respuesta = etRespuesta.text.toString()
            if (pregunta.isNotEmpty() && respuesta.isNotEmpty()) {
                if (selectedImageUri != null) {
                    val imagenBase64 = convertirImagenABase64()
                    guardarFichaRealtime(pregunta, respuesta, imagenBase64)
                } else {
                    guardarFichaRealtime(pregunta, respuesta, imagenUrl)
                }
            } else {
                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            STORAGE_PERMISSION_REQUEST
        )
    }

    private fun abrirSelectorImagen() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(Intent.createChooser(intent, "Seleccionar imagen"), PICK_IMAGE_REQUEST)
    }

    private fun convertirImagenABase64(): String {
        try {
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImageUri)
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream)
            val byteArray = stream.toByteArray()
            return Base64.encodeToString(byteArray, Base64.DEFAULT)
        } catch (e: Exception) {
            Toast.makeText(this, "Error al procesar la imagen", Toast.LENGTH_SHORT).show()
            return ""
        }
    }

    private fun guardarFichaRealtime(pregunta: String, respuesta: String, imagenBase64: String) {
        val ref = FirebaseDatabase.getInstance().getReference("fichas")
        val id = fichaId ?: ref.push().key!!
        val ficha = mapOf(
            "id" to id,
            "tematicaId" to tematicaId,
            "pregunta" to pregunta,
            "respuesta" to respuesta,
            "imagen" to imagenBase64
        )
        ref.child(id).setValue(ficha)
            .addOnSuccessListener {
                Toast.makeText(this, if (fichaId == null) "Ficha agregada" else "Ficha actualizada", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al guardar ficha", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            STORAGE_PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    abrirSelectorImagen()
                } else {
                    Toast.makeText(this, "Se necesita permiso para acceder a las imágenes", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            val ivPreview = findViewById<ImageView>(R.id.ivPreviewImagen)
            ivPreview.visibility = ImageView.VISIBLE
            Picasso.get()
                .load(selectedImageUri)
                .into(ivPreview)
        }
    }
}