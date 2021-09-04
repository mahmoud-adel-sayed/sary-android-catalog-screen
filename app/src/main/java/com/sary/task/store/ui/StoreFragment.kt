package com.sary.task.store.ui

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.request.RequestOptions
import com.sary.task.*
import com.sary.task.banner.ImageLoadingListener
import com.sary.task.banner.Slide
import com.sary.task.banner.SlideView
import com.sary.task.databinding.BannerSlideBinding
import com.sary.task.databinding.CatalogSectionBinding
import com.sary.task.databinding.FragmentStoreBinding
import com.sary.task.databinding.SmartSectionItemBinding
import com.sary.task.store.data.model.BannerItem
import com.sary.task.store.data.model.CatalogSection
import com.sary.task.store.data.model.CatalogSection.Companion.DATA_TYPE_BANNER
import com.sary.task.store.data.model.CatalogSection.Companion.DATA_TYPE_GROUP
import com.sary.task.store.data.model.CatalogSection.Companion.DATA_TYPE_SMART
import com.sary.task.store.data.model.SectionItem
import com.sary.task.util.Response
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StoreFragment : Fragment() {
    private var _binding: FragmentStoreBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<StoreViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStoreBinding.inflate(inflater, container, false)
        observeData()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.getBannerItems()
        viewModel.getCatalog()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeData() {
        viewModel.bannerItems.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Response.Success -> setupBanner(it.data.result)
                is Response.Error -> {
                    binding.banner.setError(
                        message = it.message,
                        onRetryClick = { viewModel.getBannerItems() }
                    )
                }
                is Response.Empty -> {
                    // We could create an empty state view that has an image and a tagline,
                    // but for now we show nothing.
                    val emptyStateView: View? = null
                    binding.banner.showEmptyState(view = emptyStateView)
                }
            }
        })

        viewModel.catalog.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Response.Success -> constructCatalog(it.data.result)
                is Response.Error -> {
                    it.message?.let { message ->
                        binding.root.showSnackBar(
                            message = "Error Showing Catalog: $message",
                            actionLabel = getString(R.string.retry),
                            action = { viewModel.getCatalog() }
                        )
                    }
                }
                is Response.Empty -> handleEmptyCatalog()
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
        binding.banner.setSlides(
            slides = slides,
            onSlideView = { inflater, viewGroup -> BannerItemView(context, inflater, viewGroup) }
        )
    }

    private fun constructCatalog(sections: List<CatalogSection>) {
        sections.forEach { section ->
            val sectionBinding = CatalogSectionBinding.inflate(layoutInflater).apply {
                if (section.showTitle) {
                    title.text = section.title
                } else {
                    title.visibility = View.GONE
                }
                if (section.dataType == DATA_TYPE_SMART) {
                    root.setBackgroundColor(Color.parseColor("#EEEEEE"))
                }
            }
            val column = sectionBinding.sectionItems
            section addTo column
            binding.catalogSections.addView(sectionBinding.root)
        }
    }

    private infix fun CatalogSection.addTo(column: LinearLayout) {
        column.orientation = LinearLayout.VERTICAL
        val edgePadding = (12 * 2)
        when (uiType) {
            CatalogSection.UI_TYPE_GRID -> {
                val itemSize = (getScreenWidthInPixels() - edgePadding.toPx.toInt()) / rowItemsCount

                val rowsCount = (data.size + rowItemsCount - 1) / rowItemsCount
                for (i in 0 until rowsCount) {
                    val row = LinearLayout(requireContext()).also {
                        it.orientation = LinearLayout.HORIZONTAL
                    }
                    for (j in 0 until rowItemsCount) {
                        val index = i * rowItemsCount + j
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
                val itemSize = getScreenWidthInPixels() / 2
                data.forEach { item ->
                    column.addView(
                        createSectionItem(size = itemSize, dataType = this.dataType, item = item)
                    )
                }
            }
            CatalogSection.UI_TYPE_SLIDER -> {
                val horizontalScrollView = HorizontalScrollView(requireContext()).run {
                    layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                    val row = LinearLayout(requireContext()).also {
                        it.orientation = LinearLayout.HORIZONTAL
                    }
                    // Show fixed number of items (3 items and then content will be scrollable)
                    val itemSize = (getScreenWidthInPixels() - edgePadding.toPx.toInt()) / 3
                    data.forEach { item ->
                        row.addView(
                            createSectionItem(size = itemSize, dataType = dataType, item = item)
                        )
                    }
                    addView(row)
                    this
                }
                column.addView(horizontalScrollView)
            }
        }
    }

    private fun createSectionItem(size: Int, dataType: String, item: SectionItem): View? {
        return when (dataType) {
            DATA_TYPE_SMART -> SmartSectionItemBinding.inflate(layoutInflater).apply {
                root.layoutParams = LayoutParams(size, LayoutParams.WRAP_CONTENT)
                image.loadImage(
                    context = requireContext(),
                    requestOptions = RequestOptions.overrideOf(75),
                    imageUrl = item.imageUrl
                )
                title.text = item.name
            }.root
            DATA_TYPE_GROUP -> ImageView(context).apply {
                layoutParams = LayoutParams(size, LayoutParams.WRAP_CONTENT)
                setPadding(4.toPx.toInt())
                loadImage(
                    context = requireContext(),
                    requestOptions = RequestOptions.overrideOf(size),
                    imageUrl = item.imageUrl
                )
            }
            DATA_TYPE_BANNER -> ImageView(context).apply {
                layoutParams = LayoutParams(size, LayoutParams.WRAP_CONTENT)
                setPadding(4.toPx.toInt())
                loadImage(
                    context = requireContext(),
                    requestOptions = RequestOptions.overrideOf(size),
                    imageUrl = item.imageUrl
                )
            }
            else -> null
        }
    }

    private fun handleEmptyCatalog() {
        // Show empty state for the catalog,
        // but for now we do nothing.
    }
}

private class BannerItemView(
    private val context: Context,
    layoutInflater: LayoutInflater,
    container: ViewGroup?
) : SlideView() {
    private val binding = BannerSlideBinding.inflate(layoutInflater, container, false)

    override val view: View get() = binding.root

    override fun bind(position: Int, slide: Slide) = with(binding) {
        root.setOnClickListener { slide.onClick() }
        slideImage.loadImage(
            context = context, imageUrl = slide.imageUrl, listener = ImageLoadingListener(progress)
        )
    }
}