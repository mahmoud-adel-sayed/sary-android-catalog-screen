package com.sary.task.store.ui

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.request.RequestOptions
import com.sary.task.R
import com.sary.task.loadImage
import com.sary.task.store.data.model.SectionItem

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