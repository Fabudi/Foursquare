package inc.fabudi.foursquare


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


class CategoryAdapter(private val data: ArrayList<Category>, private val listener: OnItemClickListener) :
    RecyclerView.Adapter<CategoryViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.category_row, parent, false)
        return CategoryViewHolder(v)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(data[position], listener)
    }

    fun unselectAll(){
        data.forEach { item -> item.isChecked = false }
        notifyDataSetChanged()
    }
}