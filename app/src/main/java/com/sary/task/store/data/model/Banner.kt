package com.sary.task.store.data.model

import com.google.gson.annotations.SerializedName

data class Banner(
        @field:SerializedName("id")
        val id: Long,

        @field:SerializedName("title")
        val title: String,

        @field:SerializedName("description")
        val description: String,

        @field:SerializedName("button_text")
        val buttonText: String,

        @field:SerializedName("expiry_status")
        val expiryStatus: Boolean,

        @field:SerializedName("created_at")
        val creationDate: String,

        @field:SerializedName("start_date")
        val startDate: String,

        @field:SerializedName("expiry_date")
        val expiryDate: String,

        @field:SerializedName("image")
        val imageUrl: String,

        @field:SerializedName("photo")
        val photoUrl: String,

        @field:SerializedName("link")
        val link: String,

        @field:SerializedName("level")
        val level: String,

        @field:SerializedName("is_available")
        val isAvailable: Boolean,

        @field:SerializedName("priority")
        val priority: Int,

        @field:SerializedName("branches")
        val branches: List<Int>
)