package com.sary.task.banner

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.ConfigurationCompat
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.sary.task.R

@Suppress("unused")
class BannerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private val nextBTN: View
    private val previousBTN: View
    private val bulletsIndicator: LinearLayout
    private val viewPager: ViewPager // TODO: Replace it with ViewPager2

    private var slideViews: Array<View> = arrayOf()
    private val pagerAdapter: BannerPagerAdapter
    private var bullets: Array<TextView>? = null

    private val properties: Properties
    private var listener: Listener? = null

    private var slideShowHandler: Handler? = null
    private var slideShowRunnable: Runnable = Runnable { }
    private var isDragging = false
    private var duration = 0L // in milliseconds
    private var isFirstSlide = true
    private var isLastSlide = false

    interface Listener {
        fun onSlideSelected(view: View?, position: Int, isLastSlide: Boolean) { }
    }

    init {
        val view = inflate(context, R.layout.banner_layout, this)
        nextBTN = view.findViewById(R.id.btn_next)
        previousBTN = view.findViewById(R.id.btn_previous)
        bulletsIndicator = view.findViewById(R.id.bullets_indicator)
        viewPager = view.findViewById(R.id.view_pager)

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.BannerView)
        try {
            val showArrows = typedArray.getBoolean(R.styleable.BannerView_showArrows, false)
            val showBullets = typedArray.getBoolean(R.styleable.BannerView_showBullets, true)
            val autoScrollEnabled = typedArray.getBoolean(R.styleable.BannerView_autoScrollEnabled, true)
            val slideShowDuration = typedArray.getInt(R.styleable.BannerView_slideShowDuration, 5000)
            val activeBulletColor = typedArray.getColor(
                R.styleable.BannerView_activeBulletColor,
                ResourcesCompat.getColor(resources, R.color.white, null)
            )
            val inactiveBulletColor = typedArray.getColor(
                R.styleable.BannerView_inactiveBulletColor,
                ResourcesCompat.getColor(resources, R.color.gray, null)
            )
            properties = Properties(
                isRtl = ConfigurationCompat.getLocales(resources.configuration)[0].language == "ar",
                showArrows = showArrows,
                showBullets = showBullets,
                autoScrollEnabled = autoScrollEnabled,
                slideShowDuration = slideShowDuration.toLong(),
                activeBulletColor = activeBulletColor,
                inactiveBulletColor = inactiveBulletColor
            )
        } finally {
            typedArray.recycle()
        }

        showArrows(show = properties.showArrows)
        bulletsIndicator.visibility = if (properties.showBullets) View.VISIBLE else View.GONE

        setupSlideShow()
        pagerAdapter = BannerPagerAdapter()
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (visibility == View.VISIBLE) {
            // onResume() called
            slideShowHandler?.postDelayed(slideShowRunnable, duration)
        } else {
            // onPause() called
            slideShowHandler?.removeCallbacks(slideShowRunnable)
        }
    }

    fun setListener(listener: Listener?) {
        this.listener = listener
    }

    val showArrows: Boolean get() = properties.showArrows

    val showBullets: Boolean get() = properties.showBullets

    val isAutoScrollEnabled: Boolean get() = properties.autoScrollEnabled

    val slideShowDuration: Long get() = properties.slideShowDuration

    val activeBulletColor: Int
        @ColorInt
        get() = properties.activeBulletColor

    val inactiveBulletColor: Int
        @ColorInt
        get() = properties.inactiveBulletColor

    fun setSlides(views: Array<View>) {
        if (views.isEmpty()) {
            return
        }
        if (views.size == 1) {
            showArrows(show = false)
        }
        slideViews = views
        resetSlideShow()
        setupBulletsIndicator()
        setupViewPager()
        pagerAdapter.notifyDataSetChanged()
        invalidate()
        requestLayout()
    }

    private fun setupSlideShow() {
        if (properties.autoScrollEnabled) {
            duration = properties.slideShowDuration
            slideShowHandler = Handler(Looper.getMainLooper())
            slideShowRunnable = Runnable {
                onNext()
                slideShowHandler?.postDelayed(slideShowRunnable, duration)
            }
        }
    }

    private fun setupViewPager() {
        with(viewPager) {
            offscreenPageLimit = pagerAdapter.count
            adapter = pagerAdapter
            addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) { }

                override fun onPageSelected(position: Int) {
                    isFirstSlide = isFirstSlide(position)
                    isLastSlide = isLastSlide(position)
                    activateBullet(position)
                    checkArrows()
                    val view = slideViews[position]
                    listener?.onSlideSelected(view, getLocaleBasedPosition(position), isLastSlide)
                }

                override fun onPageScrollStateChanged(state: Int) {
                    if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                        slideShowHandler?.removeCallbacks(slideShowRunnable)
                        isDragging = true
                    }
                    else if (state == ViewPager.SCROLL_STATE_SETTLING && isDragging) {
                        slideShowHandler?.postDelayed(slideShowRunnable, duration)
                        isDragging = false
                    }
                }
            })
            // Activate the first page
            currentItem = getLocaleBasedPosition(0)
        }
    }

    private fun setupBulletsIndicator() {
        if (properties.showBullets) {
            bulletsIndicator.removeAllViews()
            bullets = Array(slideViews.size) {
                val textView = TextView(context).apply {
                    @Suppress("DEPRECATION")
                    text = Html.fromHtml("&#8226;")
                    textSize = 35F
                    setTextColor(properties.inactiveBulletColor)
                }
                bulletsIndicator.addView(textView)
                textView
            }
            // Activate the first bullet
            bullets!![getLocaleBasedPosition(0)].setTextColor(properties.activeBulletColor)
        }
    }

    private fun activateBullet(position: Int) {
        if (bullets == null) {
            return
        }
        val bulletsList = bullets!!
        for (bullet in bulletsList) {
            bullet.setTextColor(properties.inactiveBulletColor)
        }
        bulletsList[position].setTextColor(properties.activeBulletColor)
    }

    private fun showArrows(show: Boolean) {
        if (show) {
            with(nextBTN) {
                visibility = View.VISIBLE
                setOnClickListener { onNext(fromArrow = true) }
            }
            with(previousBTN) {
                visibility = View.GONE
                setOnClickListener { onPrevious() }
            }
        } else {
            nextBTN.visibility = View.GONE
            previousBTN.visibility = View.GONE
        }
    }

    private fun checkArrows() {
        if (!properties.showArrows) return

        if (isFirstSlide) {
            nextBTN.visibility = View.VISIBLE
            previousBTN.visibility = View.GONE
            return
        }
        if (isLastSlide) {
            nextBTN.visibility = View.GONE
            previousBTN.visibility = View.VISIBLE
            return
        }

        nextBTN.visibility = View.VISIBLE
        previousBTN.visibility = View.VISIBLE
    }

    private fun onNext(fromArrow: Boolean = false) {
        if (isLastSlide) {
            viewPager.setCurrentItem(getLocaleBasedPosition(0), true)
            return
        }
        var currentIndex = viewPager.currentItem
        if (properties.isRtl) {
            currentIndex--
        } else {
            currentIndex++
        }
        viewPager.setCurrentItem(currentIndex, true)
        if (fromArrow) {
            resetSlideShow()
        }
    }

    private fun onPrevious(fromArrow: Boolean = true) {
        var currentIndex = viewPager.currentItem
        if (properties.isRtl) {
            currentIndex++
        } else {
            currentIndex--
        }
        viewPager.setCurrentItem(currentIndex, true)
        if (fromArrow) {
            resetSlideShow()
        }
    }

    private fun selectSlide(position: Int) {
        resetSlideShow()
        viewPager.setCurrentItem(getLocaleBasedPosition(position), true)
    }

    private fun resetSlideShow() {
        slideShowHandler?.removeCallbacks(slideShowRunnable)
        slideShowHandler?.postDelayed(slideShowRunnable, duration)
    }

    private fun getLocaleBasedPosition(position: Int): Int =
        if (properties.isRtl) pagerAdapter.count - position - 1 else position

    private fun isFirstSlide(position: Int): Boolean = getLocaleBasedPosition(position) == 0

    private fun isLastSlide(position: Int): Boolean =
        getLocaleBasedPosition(position) == (pagerAdapter.count - 1)

    private inner class BannerPagerAdapter : PagerAdapter() {

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view = slideViews[getLocaleBasedPosition(position)]
            container.addView(view)
            return view
        }

        override fun isViewFromObject(v: View, o: Any): Boolean = (v == o)

        override fun getCount(): Int = slideViews.size

        override fun destroyItem(container: View, position: Int, o: Any) { }
    }
}

private data class Properties(
    val isRtl: Boolean,
    val showArrows: Boolean,
    val showBullets: Boolean,
    val autoScrollEnabled: Boolean,
    val slideShowDuration: Long,
    @ColorInt val activeBulletColor: Int,
    @ColorInt val inactiveBulletColor: Int
)