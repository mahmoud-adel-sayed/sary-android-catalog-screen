package com.sary.task

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.BaseTransientBottomBar.Duration
import com.google.android.material.snackbar.Snackbar

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

@JvmOverloads
fun View.showSnackBar(
    message: String,
    @Duration duration: Int = Snackbar.LENGTH_INDEFINITE,
    actionLabel: String? = null,
    action: ((v: View) -> Unit)? = null
) {
    val snackBar = Snackbar.make(this, message, duration)
    if (actionLabel != null && action != null) {
        snackBar.setAction(actionLabel, action)
    }
    snackBar.show()
    val tv = snackBar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
    tv?.gravity = Gravity.CENTER_HORIZONTAL
}

@Suppress("DEPRECATION")
fun Fragment.getScreenWidthInPixels(): Int {
    val display = requireActivity().windowManager.defaultDisplay
    val size = Point()
    display.getSize(size)
    return size.x
}

val Number.toPx get() = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this.toFloat(),
    Resources.getSystem().displayMetrics
)