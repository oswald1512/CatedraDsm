package sv.edu.udb.fichaseducativas.HT192210.MU192557.RH190164.CT142074

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FichasActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAgregarFicha: FloatingActionButton
    private lateinit var db: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var adapter: FichasAdapter
    private var tematicaId: String = ""
    private lateinit var tematicaNombre: String
    private var listaFichas: List<FichaEducativa> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fichas)

        // Obtener y validar el ID de la temática
        tematicaId = intent.getStringExtra("tematicaId") ?: ""
        if (tematicaId.isEmpty()) {
            Toast.makeText(this, "Error: ID de temática no válido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        tematicaNombre = intent.getStringExtra("tematicaNombre") ?: ""

        db = FirebaseDatabase.getInstance().getReference("fichas")
        auth = FirebaseAuth.getInstance()

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "$tematicaNombre"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerView = findViewById(R.id.recyclerViewFichas)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = FichasAdapter(emptyList(),
            onItemClick = { ficha ->
                try {
                    if (ficha.id.isNotEmpty() && ficha.tematicaId.isNotEmpty()) {
                        val posicion = listaFichas.indexOfFirst { it.id == ficha.id }
                        val intent = Intent(this, TarjetaEstudioActivity::class.java).apply {
                            putExtra("fichas", ArrayList(listaFichas))
                            putExtra("posicion", posicion)
                            putExtra("tematicaNombre", tematicaNombre)
                        }
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Error: Datos de ficha incompletos", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Error al abrir la ficha: ${e.message}", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            },
            onMenuClick = { ficha ->
                mostrarOpcionesFicha(ficha)
            }
        )
        recyclerView.adapter = adapter

        fabAgregarFicha = findViewById(R.id.fabAgregarFicha)
        fabAgregarFicha.setOnClickListener {
            val intent = Intent(this, AgregarFichaActivity::class.java).apply {
                putExtra("tematicaId", tematicaId)
            }
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        cargarFichasRealtime()
    }

    private fun cargarFichasRealtime() {
        db.orderByChild("tematicaId").equalTo(tematicaId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val fichas = snapshot.children.mapNotNull { it.getValue(FichaEducativa::class.java) }
                    listaFichas = fichas
                    adapter.updateFichas(fichas)
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@FichasActivity, "Error al cargar las fichas", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun mostrarOpcionesFicha(ficha: FichaEducativa) {
        val opciones = arrayOf("Editar", "Eliminar")
        AlertDialog.Builder(this)
            .setTitle("Opciones de Ficha")
            .setItems(opciones) { _, opcion ->
                when (opcion) {
                    0 -> {
                        val intent = Intent(this, AgregarFichaActivity::class.java)
                        intent.putExtra("fichaId", ficha.id)
                        intent.putExtra("pregunta", ficha.pregunta)
                        intent.putExtra("respuesta", ficha.respuesta)
                        intent.putExtra("imagen", ficha.imagen)
                        intent.putExtra("tematicaId", tematicaId)
                        startActivity(intent)
                    }
                    1 -> confirmarEliminarFicha(ficha)
                }
            }
            .show()
    }

    private fun confirmarEliminarFicha(ficha: FichaEducativa) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Ficha")
            .setMessage("¿Estás seguro de que deseas eliminar esta ficha?")
            .setPositiveButton("Eliminar") { _, _ ->
                eliminarFichaRealtime(ficha.id)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarFichaRealtime(id: String) {
        db.child(id).removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Ficha eliminada exitosamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al eliminar ficha", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.action_sign_out -> {
                com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
                Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}