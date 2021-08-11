package com.sary.task.store.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.request.RequestOptions
import com.sary.task.R
import com.sary.task.loadImage
import com.sary.task.store.data.model.CatalogSection
import com.sary.task.store.data.model.CatalogSection.Companion.DATA_TYPE_BANNER
import com.sary.task.store.data.model.CatalogSection.Companion.DATA_TYPE_GROUP
import com.sary.task.store.data.model.CatalogSection.Companion.DATA_TYPE_SMART
import com.sary.task.store.data.model.CatalogSection.Companion.UI_TYPE_GRID
import com.sary.task.store.data.model.CatalogSection.Companion.UI_TYPE_LINEAR
import com.sary.task.store.data.model.CatalogSection.Companion.UI_TYPE_SLIDER
import com.sary.task.store.data.model.SectionItem

class CatalogSectionsAdapter(
    private val context: Context,
    private var sections: List<CatalogSection> = arrayListOf()
) : RecyclerView.Adapter<CatalogSectionsAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.catalog_section_list_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val section = sections[position]
        with(holder) {
            if (section.showTitle) {
                title.visibility = View.VISIBLE
                title.text = section.title
            } else {
                title.visibility = View.GONE
            }
            catalogSection.isNestedScrollingEnabled = false
            catalogSection.layoutManager = section.createLayoutManager(context)
            catalogSection.adapter = section.createAdapter(context)
            if (catalogSection.adapter is SmartCatalogSectionAdapter) {
                catalogSection.setBackgroundColor(Color.parseColor(SMART_SECTION_BACKGROUND))
            }
        }
    }

    override fun getItemCount(): Int = sections.size

    fun replaceData(sections: List<CatalogSection>) {
        this.sections = sections
        notifyDataSetChanged()
    }

    @SuppressLint("NonConstantResourceId")
    class ItemViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        @BindView(R.id.title)
        lateinit var title: TextView

        @BindView(R.id.catalog_section)
        lateinit var catalogSection: RecyclerView

        init {
            ButterKnife.bind(this, v)
        }
    }
}

class SmartCatalogSectionAdapter(
    private val context: Context,
    private val items: List<SectionItem> = arrayListOf()
) : RecyclerView.Adapter<SmartCatalogSectionAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.smart_section_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.image.loadImage(
            context = context,
            requestOptions = RequestOptions.overrideOf(75, 75),
            imageUrl = item.imageUrl
        )
        holder.title.text = item.name
    }

    override fun getItemCount(): Int = items.size

    @SuppressLint("NonConstantResourceId")
    class ItemViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        @BindView(R.id.image)
        lateinit var image: ImageView

        @BindView(R.id.title)
        lateinit var title: TextView

        init {
            ButterKnife.bind(this, v)
        }
    }
}

class GroupCatalogSectionAdapter(
    private val context: Context,
    private val items: List<SectionItem> = arrayListOf()
) : RecyclerView.Adapter<GroupCatalogSectionAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.group_section_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.image.loadImage(context = context, imageUrl = items[position].imageUrl)
    }

    override fun getItemCount(): Int = items.size

    @SuppressLint("NonConstantResourceId")
    class ItemViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        @BindView(R.id.image)
        lateinit var image: ImageView

        init {
            ButterKnife.bind(this, v)
        }
    }
}

class BannerCatalogSectionAdapter(
    private val context: Context,
    private val items: List<SectionItem> = arrayListOf()
) : RecyclerView.Adapter<BannerCatalogSectionAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.banner_section_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.image.loadImage(context = context, imageUrl = items[position].imageUrl)
    }

    override fun getItemCount(): Int = items.size

    @SuppressLint("NonConstantResourceId")
    class ItemViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        @BindView(R.id.image)
        lateinit var image: ImageView

        init {
            ButterKnife.bind(this, v)
        }
    }
}

private fun CatalogSection.createLayoutManager(context: Context): RecyclerView.LayoutManager? = when (uiType) {
    UI_TYPE_LINEAR -> LinearLayoutManager(context, RecyclerView.VERTICAL, false)
    UI_TYPE_SLIDER -> LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
    UI_TYPE_GRID -> GridLayoutManager(context, rowCount, RecyclerView.VERTICAL, false)
    else -> null
}

private fun CatalogSection.createAdapter(context: Context): RecyclerView.Adapter<*>? = when (dataType) {
    DATA_TYPE_SMART -> SmartCatalogSectionAdapter(context, data)
    DATA_TYPE_GROUP -> GroupCatalogSectionAdapter(context, data)
    DATA_TYPE_BANNER -> BannerCatalogSectionAdapter(context, data)
    else -> null
}

private const val SMART_SECTION_BACKGROUND = "#EEEEEE"