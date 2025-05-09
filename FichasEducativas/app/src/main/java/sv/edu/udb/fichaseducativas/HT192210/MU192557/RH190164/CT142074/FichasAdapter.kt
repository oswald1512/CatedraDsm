package sv.edu.udb.fichaseducativas.HT192210.MU192557.RH190164.CT142074

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FichasAdapter(
    private var fichas: List<FichaEducativa>,
    private val onItemClick: (FichaEducativa) -> Unit,
    private val onMenuClick: (FichaEducativa) -> Unit
) : RecyclerView.Adapter<FichasAdapter.FichaViewHolder>() {

    class FichaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNumeroFicha: TextView = view.findViewById(R.id.tvNumeroFicha)
        val btnMenuFicha: ImageButton = view.findViewById(R.id.btnMenuFicha)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FichaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ficha, parent, false)
        return FichaViewHolder(view)
    }

    override fun onBindViewHolder(holder: FichaViewHolder, position: Int) {
        val ficha = fichas[position]
        holder.tvNumeroFicha.text = "Ficha ${position + 1}"
        holder.itemView.setOnClickListener {
            try {
                onItemClick(ficha)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        holder.btnMenuFicha.setOnClickListener {
            onMenuClick(ficha)
        }
    }

    override fun getItemCount() = fichas.size

    fun updateFichas(newFichas: List<FichaEducativa>) {
        fichas = newFichas
        notifyDataSetChanged()
    }
}