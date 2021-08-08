package com.sary.task.store.data.model

import com.google.gson.annotations.SerializedName

data class CatalogSection(
    @field:SerializedName("id")
    val id: Long,

    @field:SerializedName("title")
    val title: String,

    @field:SerializedName("subtitle")
    val subtitle: String,

    @field:SerializedName("data")
    val data: List<SectionItem>,

    @field:SerializedName("show_title")
    val showTitle: Boolean,

    @field:SerializedName("data_type")
    val dataType: String,

    @field:SerializedName("ui_type")
    val uiType: String,

    @field:SerializedName("row_count")
    val rowCount: Int
) {
    companion object {
        const val DATA_TYPE_SMART = "smart"
        const val DATA_TYPE_GROUP = "group"
        const val DATA_TYPE_BANNER = "banner"

        const val UI_TYPE_GRID = "grid"
        const val UI_TYPE_LINEAR = "linear"
        const val UI_TYPE_SLIDER = "slider"
    }
}

data class SectionItem(
    @field:SerializedName("name")
    val name: String?,

    @field:SerializedName("image")
    val imageUrl: String,

    @field:SerializedName("cover")
    val coverUrl: String?,

    @field:SerializedName("deep_link")
    val deepLink: String?
)