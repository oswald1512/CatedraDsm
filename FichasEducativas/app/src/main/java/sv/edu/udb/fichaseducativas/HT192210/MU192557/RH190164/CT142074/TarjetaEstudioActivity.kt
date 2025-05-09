package sv.edu.udb.fichaseducativas.HT192210.MU192557.RH190164.CT142074

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.util.Base64
import android.graphics.BitmapFactory
import java.io.ByteArrayInputStream

class TarjetaEstudioActivity : AppCompatActivity() {

    private var mostrarPregunta = true
    private var fichas: ArrayList<FichaEducativa> = arrayListOf()
    private var posicion: Int = 0
    private var tematicaNombre: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tarjeta_estudio)

        fichas = intent.getSerializableExtra("fichas") as? ArrayList<FichaEducativa> ?: arrayListOf()
        posicion = intent.getIntExtra("posicion", 0)
        tematicaNombre = intent.getStringExtra("tematicaNombre") ?: ""

        // Configurar Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbarTarjeta)
        setSupportActionBar(toolbar)
        supportActionBar?.title = tematicaNombre
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        val btnAnterior = findViewById<Button>(R.id.btnAnterior)
        val btnSiguiente = findViewById<Button>(R.id.btnSiguiente)

        btnAnterior.setOnClickListener {
            if (posicion > 0) {
                posicion--
                mostrarPregunta = true
                mostrarFichaActual()
            } else {
                Toast.makeText(this, "Primera ficha", Toast.LENGTH_SHORT).show()
            }
        }
        btnSiguiente.setOnClickListener {
            if (posicion < fichas.size - 1) {
                posicion++
                mostrarPregunta = true
                mostrarFichaActual()
            } else {
                Toast.makeText(this, "Última ficha", Toast.LENGTH_SHORT).show()
            }
        }

        mostrarFichaActual()
    }

    private fun mostrarFichaActual() {
        if (fichas.isEmpty() || posicion !in fichas.indices) {
            Toast.makeText(this, "No hay fichas para mostrar", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        val ficha = fichas[posicion]
        val tvContenido = findViewById<TextView>(R.id.tvContenido)
        val btnVoltear = findViewById<Button>(R.id.btnVoltear)
        val ivImagen = findViewById<ImageView>(R.id.ivImagen)

        tvContenido.text = ficha.pregunta
        btnVoltear.text = "Mostrar respuesta"
        mostrarPregunta = true

        if (ficha.imagen.isNotEmpty()) {
            try {
                val decodedString = Base64.decode(ficha.imagen, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeStream(ByteArrayInputStream(decodedString))
                ivImagen.setImageBitmap(bitmap)
                ivImagen.visibility = View.VISIBLE
            } catch (e: Exception) {
                ivImagen.visibility = View.GONE
            }
        } else {
            ivImagen.visibility = View.GONE
        }

        btnVoltear.setOnClickListener {
            mostrarPregunta = !mostrarPregunta
            tvContenido.text = if (mostrarPregunta) ficha.pregunta else ficha.respuesta
            btnVoltear.text = if (mostrarPregunta) "Mostrar respuesta" else "Ver Pregunta"
        }
    }

    override fun onCreateOptionsMenu(menu: android.view.Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.action_sign_out -> {
                com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
                Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                val intent = android.content.Intent(this, LoginActivity::class.java)
                intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
} 