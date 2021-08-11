package com.sary.task.store.ui

import android.annotation.SuppressLint
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
import com.sary.task.di.Injectable
import com.sary.task.loadImage
import com.sary.task.showSnackBar
import com.sary.task.store.data.model.Banner
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
        viewModel.getBanners()
        viewModel.getCatalog()
    }

    private fun setupCatalogSections() = with(catalogSectionsRV) {
        isNestedScrollingEnabled = false
        layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        adapter = catalogSectionsAdapter
    }

    private fun observeData() {
        viewModel.banners.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Response.Success -> setupBanners(it.data.result)
                is Response.Error -> {
                    // TODO: Find a better way to show errors.
                    it.message?.let { message ->
                        root.showSnackBar(
                            message = "Error Showing Banners: $message",
                            actionLabel = getString(R.string.retry),
                            action = { viewModel.getBanners() }
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

    private fun setupBanners(banners: List<Banner>) {
        val slides = Array(banners.size) { i -> getSlideView(banners[i]) }
        bannerView.setSlides(slides)
    }

    // Dynamic Content: we can render different kinds of views in the banner/slider
    private fun getSlideView(banner: Banner): View {
        val context = requireContext()
        @SuppressLint("InflateParams")
        val view = layoutInflater.inflate(R.layout.banner_slide, null)
        view.setOnClickListener { Toast.makeText(context, banner.link, Toast.LENGTH_SHORT).show() }
        view.findViewById<ImageView>(R.id.slide_image).loadImage(
            context = context,
            imageUrl = banner.imageUrl,
            listener = ImageLoadingListener(view.findViewById<ProgressBar>(R.id.progress))
        )
        return view
    }
}