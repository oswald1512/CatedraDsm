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
import sv.edu.udb.fichaseducativas.HT192210.MU192557.RH190164.CT142074.TematicasAdapter
import sv.edu.udb.fichaseducativas.HT192210.MU192557.RH190164.CT142074.Tematica

class TematicasActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAgregarTematica: FloatingActionButton
    private lateinit var db: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var adapter: TematicasAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tematicas)

        db = FirebaseDatabase.getInstance().getReference("tematicas")
        auth = FirebaseAuth.getInstance()

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Temáticas"

        recyclerView = findViewById(R.id.recyclerViewTematicas)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TematicasAdapter(emptyList(),
            onTematicaClick = { tematica ->
                val intent = Intent(this, FichasActivity::class.java)
                intent.putExtra("tematicaId", tematica.id)
                intent.putExtra("tematicaNombre", tematica.nombre)
                startActivity(intent)
            },
            onTematicaLongClick = { tematica ->
                mostrarOpcionesTematica(tematica)
            }
        )
        recyclerView.adapter = adapter

        fabAgregarTematica = findViewById(R.id.fabAgregarTematica)
        fabAgregarTematica.setOnClickListener {
            val intent = Intent(this, AgregarTematicaActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        cargarTematicasRealtime()
    }

    private fun cargarTematicasRealtime() {
        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tematicas = snapshot.children.mapNotNull { it.getValue(Tematica::class.java) }
                adapter.actualizarTematicas(tematicas)
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@TematicasActivity, "Error al cargar temáticas", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun mostrarOpcionesTematica(tematica: Tematica) {
        val opciones = arrayOf("Editar", "Eliminar")
        AlertDialog.Builder(this)
            .setTitle("Opciones de Temática")
            .setItems(opciones) { _, opcion ->
                when (opcion) {
                    0 -> {
                        val intent = Intent(this, AgregarTematicaActivity::class.java)
                        intent.putExtra("tematicaId", tematica.id)
                        intent.putExtra("nombre", tematica.nombre)
                        intent.putExtra("descripcion", tematica.descripcion)
                        startActivity(intent)
                    }
                    1 -> confirmarEliminarTematica(tematica)
                }
            }
            .show()
    }

    private fun confirmarEliminarTematica(tematica: Tematica) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Temática")
            .setMessage("¿Estás seguro de que deseas eliminar esta temática?")
            .setPositiveButton("Eliminar") { _, _ ->
                eliminarTematicaRealtime(tematica.id)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarTematicaRealtime(id: String) {
        db.child(id).removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Temática eliminada exitosamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al eliminar temática", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
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