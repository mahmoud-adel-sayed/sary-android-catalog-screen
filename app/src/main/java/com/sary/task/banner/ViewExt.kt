package com.sary.task.banner

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions

@JvmOverloads
fun ImageView.loadImage(
    context: Context,
    imageUrl: String?,
    requestOptions: RequestOptions = RequestOptions.noTransformation(),
    errorDrawable: Drawable = ColorDrawable(Color.GRAY),
    listener: RequestListener<Drawable>? = null
) {
    if (imageUrl == null) {
        Glide.with(context).load(errorDrawable).into(this)
        return
    }
    Glide.with(context)
        .load(imageUrl)
        .apply(requestOptions)
        .error(errorDrawable)
        .listener(listener)
        .into(this)
}