package sv.edu.udb.fichaseducativas.HT192210.MU192557.RH190164.CT142074

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sv.edu.udb.fichaseducativas.HT192210.MU192557.RH190164.CT142074.R
import sv.edu.udb.fichaseducativas.HT192210.MU192557.RH190164.CT142074.Tematica

class TematicasAdapter(
    private var tematicas: List<Tematica>,
    private val onTematicaClick: (Tematica) -> Unit,
    private val onTematicaLongClick: (Tematica) -> Unit
) : RecyclerView.Adapter<TematicasAdapter.TematicaViewHolder>() {

    class TematicaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombreTematica)
        val tvDescripcion: TextView = view.findViewById(R.id.tvDescripcionTematica)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TematicaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tematica, parent, false)
        return TematicaViewHolder(view)
    }

    override fun onBindViewHolder(holder: TematicaViewHolder, position: Int) {
        val tematica = tematicas[position]
        holder.tvNombre.text = tematica.nombre
        holder.tvDescripcion.text = tematica.descripcion

        holder.itemView.setOnClickListener { onTematicaClick(tematica) }
        holder.itemView.setOnLongClickListener {
            onTematicaLongClick(tematica)
            true
        }
    }

    override fun getItemCount() = tematicas.size

    fun actualizarTematicas(nuevasTematicas: List<Tematica>) {
        tematicas = nuevasTematicas
        notifyDataSetChanged()
    }
}