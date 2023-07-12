package inc.fabudi.foursquare.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderViewAdapter
import com.squareup.picasso.Picasso
import inc.fabudi.foursquare.R
import inc.fabudi.foursquare.databinding.FragmentDetailsBinding
import inc.fabudi.foursquare.databinding.PhotoRowBinding
import inc.fabudi.foursquare.databinding.TipRowBinding
import inc.fabudi.foursquare.domain.Photo
import inc.fabudi.foursquare.domain.Tip
import inc.fabudi.foursquare.viewmodels.PlacesViewModel

class DetailsFragment : Fragment() {

    private val viewModel: PlacesViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(
            this, PlacesViewModel.Factory(activity.application)
        )[PlacesViewModel::class.java]
    }

    private var tipsViewModelAdapter: TipAdapter? = null
    private var photosViewModelAdapter: PhotoAdapter? = null
    private var fsqId: String = ""
    private lateinit var placeTitleLabel: TextView
    private lateinit var placeTypeText: TextView
    private lateinit var placeAddressText: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val binding: FragmentDetailsBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_details, container, false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        fsqId = arguments?.getString("fsq_id", "asd").toString()
        viewModel.refreshDataFromRepository((this.activity as MainActivity).token, fsqId)
        tipsViewModelAdapter = TipAdapter()
        photosViewModelAdapter = PhotoAdapter()
        binding.root.findViewById<RecyclerView>(R.id.place_tips_recyclerview).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = tipsViewModelAdapter
        }
        binding.imageSlider.apply {
            setSliderAdapter(photosViewModelAdapter!!)
            setIndicatorAnimation(IndicatorAnimationType.WORM)
            setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
            startAutoCycle()
        }
        placeTitleLabel = binding.placeTitleLabel
        placeTypeText = binding.placeTypeText
        placeAddressText = binding.placeAddressText
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getTips(fsqId).observe(viewLifecycleOwner) { tips ->
            tips.apply {
                tipsViewModelAdapter?.tips = tips
            }
        }
        viewModel.getPhotos(fsqId).observe(viewLifecycleOwner) { photos ->
            photos.apply {
                photosViewModelAdapter?.photos = photos
            }
        }
        viewModel.getPlaceDescription(fsqId).observe(viewLifecycleOwner) {
            placeTitleLabel.text = it.name
            placeAddressText.text = it.location
            placeTypeText.text = it.categories[0].name
        }
    }
}

class TipAdapter : RecyclerView.Adapter<TipViewHolder>() {

    var tips: List<Tip> = emptyList()
        set(value) {
            if (tips != value) {
                field = value
                notifyDataSetChanged()
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TipViewHolder {
        val withDataBinding: TipRowBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), TipViewHolder.LAYOUT, parent, false
        )
        return TipViewHolder(withDataBinding)
    }

    override fun getItemCount() = tips.size

    override fun onBindViewHolder(holder: TipViewHolder, position: Int) {
        holder.viewDataBinding.also {
            it.tip = tips[position]
        }
    }
}

class TipViewHolder(val viewDataBinding: TipRowBinding) :
    RecyclerView.ViewHolder(viewDataBinding.root) {
    companion object {
        @LayoutRes
        val LAYOUT = R.layout.tip_row
    }
}


class PhotoAdapter : SliderViewAdapter<PhotoViewHolder>() {

    var photos: List<Photo> = emptyList()
        set(value) {
            if (photos != value) {
                field = value
                notifyDataSetChanged()
            }
        }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        Picasso.get().load(photos[position].imageUrl).into(holder.viewDataBinding.photoRowImage)
    }

    override fun getCount() = photos.size

    override fun onCreateViewHolder(parent: ViewGroup): PhotoViewHolder {
        val withDataBinding: PhotoRowBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), PhotoViewHolder.LAYOUT, parent, false
        )
        return PhotoViewHolder(withDataBinding)
    }
}

class PhotoViewHolder(val viewDataBinding: PhotoRowBinding) :
    SliderViewAdapter.ViewHolder(viewDataBinding.root) {
    companion object {
        @LayoutRes
        val LAYOUT = R.layout.photo_row
    }
}