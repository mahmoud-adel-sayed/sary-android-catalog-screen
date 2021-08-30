package com.sary.task.store.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.sary.task.R
import com.sary.task.banner.BannerView
import com.sary.task.banner.ImageLoadingListener
import com.sary.task.banner.Slide
import com.sary.task.banner.SlideView
import com.sary.task.di.Injectable
import com.sary.task.loadImage
import com.sary.task.showSnackBar
import com.sary.task.store.data.model.BannerItem
import com.sary.task.util.Response
import javax.inject.Inject

@SuppressLint("NonConstantResourceId")
class StoreFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @BindView(R.id.root)
    lateinit var root: View

    @BindView(R.id.banner)
    lateinit var bannerView: BannerView

    @BindView(R.id.rv_catalog_sections)
    lateinit var catalogSectionsRV: RecyclerView

    private lateinit var catalogSectionsAdapter: CatalogSectionsAdapter
    private val viewModel by viewModels<StoreViewModel> { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        catalogSectionsAdapter = CatalogSectionsAdapter(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_store, container, false)
        ButterKnife.bind(this, view)
        setupCatalogSections()
        observeData()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.getBannerItems()
        viewModel.getCatalog()
    }

    private fun setupCatalogSections() = with(catalogSectionsRV) {
        isNestedScrollingEnabled = false
        layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        adapter = catalogSectionsAdapter
    }

    private fun observeData() {
        viewModel.bannerItems.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Response.Success -> setupBanner(it.data.result)
                is Response.Error -> {
                    // TODO: Find a better way to show errors.
                    it.message?.let { message ->
                        root.showSnackBar(
                            message = "Error Showing Banners: $message",
                            actionLabel = getString(R.string.retry),
                            action = { viewModel.getBannerItems() }
                        )
                    }
                }
                is Response.Empty -> TODO()
            }
        })

        viewModel.catalog.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Response.Success -> catalogSectionsAdapter.replaceData(it.data.result)
                is Response.Error -> {
                    // TODO: Find a better way to show errors.
                    it.message?.let { message ->
                        root.showSnackBar(
                            message = "Error Showing Catalog: $message",
                            actionLabel = getString(R.string.retry),
                            action = { viewModel.getCatalog() }
                        )
                    }
                }
                is Response.Empty -> TODO()
            }
        })
    }

    private fun setupBanner(banner: List<BannerItem>) {
        val context = requireContext()
        val slides = List(banner.size) { i ->
            Slide(
                imageUrl = banner[i].imageUrl,
                onClick = { Toast.makeText(context, banner[i].link, Toast.LENGTH_SHORT).show() }
            )
        }
        bannerView.setSlides(
            slides = slides,
            onSlideView = { inflater, viewGroup -> BannerItemView(context, inflater, viewGroup) }
        )
    }
}

private class BannerItemView(
    private val context: Context,
    layoutInflater: LayoutInflater,
    container: ViewGroup?
) : SlideView() {
    private val _view = layoutInflater.inflate(R.layout.banner_slide, container, false)
    private val imageView = _view.findViewById<ImageView>(R.id.slide_image)
    private val progress = _view.findViewById<ProgressBar>(R.id.progress)

    override val view: View get() = _view

    override fun bind(position: Int, slide: Slide) {
        view.setOnClickListener { slide.onClick() }
        imageView.loadImage(
            context = context,
            imageUrl = slide.imageUrl,
            listener = ImageLoadingListener(progress)
        )
    }
}