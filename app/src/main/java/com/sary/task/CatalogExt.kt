package com.sary.task

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.TextView
import androidx.core.view.setPadding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.sary.task.store.data.model.CatalogSection
import com.sary.task.store.data.model.CatalogSection.Companion.DATA_TYPE_BANNER
import com.sary.task.store.data.model.CatalogSection.Companion.DATA_TYPE_GROUP
import com.sary.task.store.data.model.CatalogSection.Companion.DATA_TYPE_SMART
import com.sary.task.store.data.model.CatalogSection.Companion.UI_TYPE_GRID
import com.sary.task.store.data.model.CatalogSection.Companion.UI_TYPE_LINEAR
import com.sary.task.store.data.model.CatalogSection.Companion.UI_TYPE_SLIDER
import com.sary.task.store.ui.BannerCatalogSectionAdapter
import com.sary.task.store.ui.GroupCatalogSectionAdapter
import com.sary.task.store.ui.SmartCatalogSectionAdapter

fun CatalogSection.createTitle(context: Activity): TextView? {
    return if (showTitle) {
        @SuppressLint("InflateParams")
        val textView = context.layoutInflater.inflate(R.layout.section_title, null) as TextView
        textView.text = title
        textView
    } else {
        null
    }
}

fun CatalogSection.createRecyclerView(context: Context): RecyclerView = RecyclerView(context).apply {
    overScrollMode = View.OVER_SCROLL_NEVER
    layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
    val px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, resources.displayMetrics)
    setPadding(px.toInt())
    layoutManager = createLayoutManager(context)
    adapter = createAdapter(context)
    if (adapter is SmartCatalogSectionAdapter) {
        setBackgroundColor(Color.parseColor(SMART_SECTION_BACKGROUND))
    }
}

private fun CatalogSection.createLayoutManager(context: Context): LayoutManager? = when (uiType) {
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