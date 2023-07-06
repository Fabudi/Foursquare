package inc.fabudi.foursquare

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip

class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val chip = itemView.findViewById<Chip>(R.id.chip)

    fun bind(item: Category, listener: CategoryAdapter.OnItemClickListener) {
        chip.text = item.label
        chip.isChecked = item.isChecked
        chip.setOnClickListener {
            listener.onItemClick(adapterPosition)
        }
    }

}