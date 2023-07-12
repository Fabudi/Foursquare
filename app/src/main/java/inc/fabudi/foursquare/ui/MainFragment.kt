package inc.fabudi.foursquare.ui

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.chip.Chip
import com.squareup.picasso.Picasso
import inc.fabudi.foursquare.R
import inc.fabudi.foursquare.databinding.FilterRowBinding
import inc.fabudi.foursquare.databinding.FragmentPlacesBinding
import inc.fabudi.foursquare.databinding.PlaceRowBinding
import inc.fabudi.foursquare.domain.Filter
import inc.fabudi.foursquare.domain.Place
import inc.fabudi.foursquare.util.getColorForCategory
import inc.fabudi.foursquare.viewmodels.PlacesViewModel


class MainFragment : Fragment() {
    private val viewModel: PlacesViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(
            this, PlacesViewModel.Factory(activity.application)
        )[PlacesViewModel::class.java]
    }

    private var placesViewModelAdapter: PlaceAdapter? = null
    private var filtersViewModelAdapter: FilterAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.places.observe(viewLifecycleOwner) { places ->
            places.apply {
                placesViewModelAdapter?.places = places
            }
        }
        viewModel.filters.observe(viewLifecycleOwner) { filters ->
            filters.apply {
                filtersViewModelAdapter?.filters = filters
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val binding: FragmentPlacesBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_places, container, false
        )
        binding.lifecycleOwner = viewLifecycleOwner

        binding.viewModel = viewModel

        viewModel.refreshDataFromRepository((this.activity as MainActivity).token)
        placesViewModelAdapter = PlaceAdapter(PlaceClick {
            val bundle = Bundle()
            bundle.putString("fsq_id", it.fsq_id)
            activity?.findNavController(R.id.my_nav_host_fragment)
                ?.navigate(R.id.action_mainFragment_to_detailsFragment, bundle)
        })

        filtersViewModelAdapter = FilterAdapter(FilterClick {
            it.isChecked = !it.isChecked
            if (it.isChecked) placesViewModelAdapter!!.filters.add(it.id)
            else placesViewModelAdapter!!.filters.remove(it.id)
            placesViewModelAdapter!!.performFiltering()
        })

        binding.root.findViewById<Chip>(R.id.clearChip).setOnClickListener {
            filtersViewModelAdapter?.clearFilters()
            placesViewModelAdapter!!.filters.clear()
            placesViewModelAdapter?.performFiltering()
        }

        binding.root.findViewById<RecyclerView>(R.id.recycler_view).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = placesViewModelAdapter
        }

        binding.root.findViewById<RecyclerView>(R.id.filter_recyclerview).apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.HORIZONTAL)
            adapter = filtersViewModelAdapter
        }

        viewModel.eventNetworkError.observe(viewLifecycleOwner) { isNetworkError ->
            if (isNetworkError) onNetworkError()
        }
        return binding.root
    }

    private fun onNetworkError() {
        if (!viewModel.isNetworkErrorShown.value!!) {
            Toast.makeText(activity, "Network Error", Toast.LENGTH_LONG).show()
            viewModel.onNetworkErrorShown()
        }
    }
}

class PlaceClick(val block: (Place) -> Unit) {
    fun onClick(place: Place) = block(place)
}


class PlaceAdapter(private val callback: PlaceClick) : RecyclerView.Adapter<PlaceViewHolder>() {

    var places: List<Place> = emptyList()
        set(value) {
            if (places != value) {
                field = value
                filteredPlaces = value.toMutableList()
                performFiltering()
                notifyDataSetChanged()
            }
        }

    var filters: MutableList<Int> = mutableListOf()

    private var filteredPlaces: MutableList<Place> = mutableListOf()

    fun performFiltering() {
        if (filters.isEmpty()) filteredPlaces = places.toMutableList()
        else {
            filteredPlaces = mutableListOf()
            for (filter in filters) {
                for (place in places) {
                    if (place.categories[0].id in filter..(filter + 999)) {
                        filteredPlaces.add(0, place)
                    }
                }
            }
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val withDataBinding: PlaceRowBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), PlaceViewHolder.LAYOUT, parent, false
        )
        return PlaceViewHolder(withDataBinding)
    }

    override fun getItemCount() = filteredPlaces.size

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        holder.viewDataBinding.also {
            filteredPlaces[position].color = getColorForCategory(
                filteredPlaces[position].categories[0].id, holder.viewDataBinding.root.context
            )
            holder.viewDataBinding.placeRowImage.backgroundTintList = ColorStateList.valueOf(
                filteredPlaces[position].color!!
            )
            holder.viewDataBinding.placeRowImage.imageTintList = ColorStateList.valueOf(
                filteredPlaces[position].color!!
            )
            it.place = filteredPlaces[position]
            it.placeCallback = callback
            Picasso.get().load(filteredPlaces[position].categories[0].iconUrl)
                .into(it.placeRowImage)
        }
    }
}

class PlaceViewHolder(val viewDataBinding: PlaceRowBinding) :
    RecyclerView.ViewHolder(viewDataBinding.root) {
    companion object {
        @LayoutRes
        val LAYOUT = R.layout.place_row
    }
}

class FilterAdapter(private val callback: FilterClick) : RecyclerView.Adapter<FilterViewHolder>() {

    var filters: List<Filter> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        val withDataBinding: FilterRowBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), FilterViewHolder.LAYOUT, parent, false
        )
        return FilterViewHolder(withDataBinding)
    }

    override fun getItemCount() = filters.size

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        holder.viewDataBinding.also { binding ->
            binding.filter = filters[position]
            binding.filterCallback = callback
            binding.chip.setOnClickListener {
                if ((it as Chip).isChecked) {
                    binding.chip.chipStrokeColor = ColorStateList.valueOf(
                        getColorForCategory(
                            filters[position].id, holder.viewDataBinding.root.context
                        )
                    )
                    binding.chip.setTextColor(
                        getColorForCategory(
                            filters[position].id, holder.viewDataBinding.root.context
                        )
                    )
                } else {
                    binding.chip.chipStrokeColor =
                        ColorStateList.valueOf(holder.viewDataBinding.root.resources.getColor(R.color.surface))
                    binding.chip.setTextColor(holder.viewDataBinding.root.resources.getColor(R.color.onSurface))
                }
                callback.onClick(filters[position])
            }
        }
    }

    fun clearFilters() {
        for (filter in filters) {
            filter.isChecked = false
        }
        notifyDataSetChanged()
    }
}

class FilterViewHolder(val viewDataBinding: FilterRowBinding) :
    RecyclerView.ViewHolder(viewDataBinding.root) {
    companion object {
        @LayoutRes
        val LAYOUT = R.layout.filter_row
    }
}

class FilterClick(val block: (Filter) -> Unit) {
    fun onClick(filter: Filter) = block(filter)
}
