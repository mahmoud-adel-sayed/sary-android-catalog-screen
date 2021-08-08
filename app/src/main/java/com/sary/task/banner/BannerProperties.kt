package com.sary.task.banner

import android.graphics.Color
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.ColorInt

class BannerProperties(
    val isRtl: Boolean = false,
    val showArrows: Boolean = false,
    val showBullets: Boolean = true,
    val autoScrollEnabled: Boolean = true,
    val slideShowDuration: Long = 5000,
    @ColorInt val activeBulletColor: Int = Color.parseColor("#FFFFFF"),
    @ColorInt val inactiveBulletColor: Int = Color.parseColor("#999999")
) : Parcelable {

    private constructor(parcel: Parcel) : this(
        isRtl = parcel.readByte().toInt() != 0,
        showArrows = parcel.readByte().toInt() != 0,
        showBullets = parcel.readByte().toInt() != 0,
        autoScrollEnabled = parcel.readByte().toInt() != 0,
        slideShowDuration = parcel.readLong(),
        activeBulletColor = parcel.readInt(),
        inactiveBulletColor = parcel.readInt()
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeByte((if (isRtl) 1 else 0).toByte())
        dest.writeByte((if (showArrows) 1 else 0).toByte())
        dest.writeByte((if (showBullets) 1 else 0).toByte())
        dest.writeByte((if (autoScrollEnabled) 1 else 0).toByte())
        dest.writeLong(slideShowDuration)
        dest.writeInt(activeBulletColor)
        dest.writeInt(inactiveBulletColor)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<BannerProperties> {
        override fun createFromParcel(source: Parcel): BannerProperties = BannerProperties(source)

        override fun newArray(size: Int): Array<BannerProperties?> = arrayOfNulls(size)
    }
}