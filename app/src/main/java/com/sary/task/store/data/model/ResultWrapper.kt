package com.sary.task.store.data.model

import com.google.gson.annotations.SerializedName

@Suppress("unused")
class ResultWrapper<out T>(
    @field:SerializedName("result")
    val result: T,

    @field:SerializedName("status")
    val status: Boolean,

    @field:SerializedName("message")
    val message: String?
)