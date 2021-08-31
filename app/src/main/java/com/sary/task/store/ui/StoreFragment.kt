package com.sary.task.store.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.request.RequestOptions
import com.sary.task.*
import com.sary.task.banner.BannerView
import com.sary.task.banner.ImageLoadingListener
import com.sary.task.banner.Slide
import com.sary.task.banner.SlideView
import com.sary.task.di.Injectable
import com.sary.task.store.data.model.BannerItem
import com.sary.task.store.data.model.CatalogSection
import com.sary.task.store.data.model.CatalogSection.Companion.DATA_TYPE_BANNER
import com.sary.task.store.data.model.CatalogSection.Companion.DATA_TYPE_GROUP
import com.sary.task.store.data.model.CatalogSection.Companion.DATA_TYPE_SMART
import com.sary.task.store.data.model.SectionItem
import com.sary.task.util.Response
import javax.inject.Inject

@SuppressLint("NonConstantResourceId", "InflateParams")
class StoreFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @BindView(R.id.root)
    lateinit var root: ViewGroup

    @BindView(R.id.banner)
    lateinit var bannerView: BannerView

    @BindView(R.id.catalog_sections)
    lateinit var catalogSectionsContainer: LinearLayout

    private val viewModel by viewModels<StoreViewModel> { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_store, container, false)
        ButterKnife.bind(this, view)
        observeData()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.getBannerItems()
        viewModel.getCatalog()
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
                is Response.Success -> constructCatalog(it.data.result)
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

    private fun constructCatalog(sections: List<CatalogSection>) {
        sections.forEach { section ->
            val container = layoutInflater.inflate(R.layout.catalog_section, null)
            val title = container.findViewById<TextView>(R.id.title)
            if (section.showTitle) {
                title.text = section.title
            } else {
                title.visibility = View.GONE
            }
            if (section.dataType == DATA_TYPE_SMART) {
                container.setBackgroundColor(Color.parseColor("#EEEEEE"))
            }
            val column = container.findViewById<LinearLayout>(R.id.section_items)
            section addTo column
            catalogSectionsContainer.addView(container)
        }
    }

    private infix fun CatalogSection.addTo(column: LinearLayout) {
        column.orientation = LinearLayout.VERTICAL
        when (uiType) {
            CatalogSection.UI_TYPE_GRID -> {
                val edgePadding = (12 * 2)
                val itemSize = (getScreenSizeInPixels() - edgePadding.toPx.toInt()) / rowCount

                val rowsCount = (data.size + rowCount - 1) / rowCount
                for (i in 0 until rowsCount) {
                    val row = LinearLayout(requireContext()).also {
                        it.orientation = LinearLayout.HORIZONTAL
                    }
                    for (j in 0 until rowCount) {
                        val index = i * rowCount + j
                        if (index >= data.size) {
                            break
                        }
                        row.addView(
                            createSectionItem(
                                size = itemSize, dataType = this.dataType, item = data[index]
                            )
                        )
                    }
                    column.addView(row)
                }
            }
            CatalogSection.UI_TYPE_LINEAR -> {
                column.gravity = Gravity.CENTER_HORIZONTAL
                val itemSize = getScreenSizeInPixels() / 2
                data.forEach { item ->
                    column.addView(
                        createSectionItem(size = itemSize, dataType = this.dataType, item = item)
                    )
                }
            }
            CatalogSection.UI_TYPE_SLIDER -> {
                TODO("Implement it later")
            }
        }
    }

    private fun createSectionItem(size: Int, dataType: String, item: SectionItem): View? {
        return when (dataType) {
            DATA_TYPE_SMART -> {
                layoutInflater.inflate(R.layout.smart_section_item, null).apply {
                    layoutParams = LayoutParams(size, LayoutParams.WRAP_CONTENT)
                    findViewById<ImageView>(R.id.image).loadImage(
                        context = requireContext(),
                        requestOptions = RequestOptions.overrideOf(75),
                        imageUrl = item.imageUrl
                    )
                    findViewById<TextView>(R.id.title).text = item.name
                }
            }
            DATA_TYPE_GROUP -> {
                layoutInflater.inflate(R.layout.group_section_item, null).apply {
                    layoutParams = LayoutParams(size, LayoutParams.WRAP_CONTENT)
                    findViewById<ImageView>(R.id.image).loadImage(
                        context = requireContext(),
                        requestOptions = RequestOptions.overrideOf(size),
                        imageUrl = item.imageUrl
                    )
                }
            }
            DATA_TYPE_BANNER -> {
                layoutInflater.inflate(R.layout.banner_section_item, null).apply {
                    layoutParams = LayoutParams(size, LayoutParams.WRAP_CONTENT)
                    findViewById<ImageView>(R.id.image).loadImage(
                        context = requireContext(),
                        requestOptions = RequestOptions.overrideOf(size),
                        imageUrl = item.imageUrl
                    )
                }
            }
            else -> null
        }
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