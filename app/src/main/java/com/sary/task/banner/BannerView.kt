package com.sary.task.banner

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
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
    private val viewPager: ViewPager2

    private val errorContainer: ViewGroup
    private val errorLabel: TextView
    private val retryBTN: View
    private val emptyStateContainer: ViewGroup

    private var slides: List<Slide> = emptyList()
    private var bullets: Array<TextView>? = null

    private val properties: Properties
    private var listener: Listener? = null

    private var slideShowHandler: Handler? = null
    private var slideShowRunnable: Runnable = Runnable { }
    private var isDragging = false
    private var isFirstSlide = true
    private var isLastSlide = false

    interface Listener {
        fun onSlideSelected(slide: Slide, position: Int, isLastSlide: Boolean) { }
    }

    init {
        val view = inflate(context, R.layout.banner_layout, this)
        nextBTN = view.findViewById<View>(R.id.btn_next).also {
            it.setOnClickListener { onNext(fromArrow = true) }
        }
        previousBTN = view.findViewById<View>(R.id.btn_previous).also {
            it.setOnClickListener { onPrevious() }
        }
        bulletsIndicator = view.findViewById(R.id.bullets_indicator)
        viewPager = view.findViewById(R.id.view_pager)

        errorContainer = view.findViewById(R.id.error_container)
        errorLabel = view.findViewById(R.id.label_error)
        retryBTN = view.findViewById(R.id.btn_retry)
        emptyStateContainer = view.findViewById(R.id.empty_state_container)

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
        setSlideShow(enabled = properties.autoScrollEnabled)
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (visibility == View.VISIBLE) {
            // onResume() called
            slideShowHandler?.postDelayed(slideShowRunnable, properties.slideShowDuration)
        } else {
            // onPause() called
            slideShowHandler?.removeCallbacks(slideShowRunnable)
        }
    }

    fun setListener(listener: Listener?) {
        this.listener = listener
    }

    fun setError(message: String?, onRetryClick: () -> Unit) {
        errorContainer.visibility = View.VISIBLE
        errorLabel.text = message
        retryBTN.setOnClickListener {
            errorContainer.visibility = View.GONE
            onRetryClick()
        }
        invalidate()
        requestLayout()
    }

    fun showEmptyState(view: View? = null) {
        emptyStateContainer.visibility = View.VISIBLE
        emptyStateContainer.addView(view)
        invalidate()
        requestLayout()
    }

    fun removeEmptyState() {
        emptyStateContainer.removeAllViews()
        emptyStateContainer.visibility = View.GONE
        invalidate()
        requestLayout()
    }

    var showArrows: Boolean
        get() = properties.showArrows
        set(value) {
            properties.showArrows = value
            showArrows(show = value)
            invalidate()
            requestLayout()
        }

    var showBullets: Boolean
        get() = properties.showBullets
        set(value) {
            properties.showBullets = value
            bulletsIndicator.visibility = if (value) View.VISIBLE else View.GONE
            invalidate()
            requestLayout()
        }

    var isAutoScrollEnabled: Boolean
        get() = properties.autoScrollEnabled
        set(value) {
            properties.autoScrollEnabled = value
            setSlideShow(enabled = value)
            invalidate()
            requestLayout()
        }

    var slideShowDuration: Long
        get() = properties.slideShowDuration
        set(value) {
            properties.slideShowDuration = value
            resetSlideShow()
            invalidate()
            requestLayout()
        }

    var activeBulletColor: Int
        @ColorInt
        get() = properties.activeBulletColor
        set(@ColorInt value) {
            properties.activeBulletColor = value
            activateBullet(viewPager.currentItem)
            invalidate()
            requestLayout()
        }

    var inactiveBulletColor: Int
        @ColorInt
        get() = properties.inactiveBulletColor
        set(@ColorInt value) {
            properties.inactiveBulletColor = value
            activateBullet(viewPager.currentItem)
            invalidate()
            requestLayout()
        }

    fun setSlides(
        slides: List<Slide>,
        onSlideView: (LayoutInflater, ViewGroup?) -> SlideView
    ) {
        if (slides.size == 1) {
            showArrows(show = false)
        }
        this.slides = slides
        resetSlideShow()
        setupBulletsIndicator()
        setupViewPager(BannerPagerAdapter(slides, onSlideView))
        invalidate()
        requestLayout()
    }

    private fun setSlideShow(enabled: Boolean) {
        if (enabled) {
            slideShowHandler = Handler(Looper.getMainLooper())
            slideShowRunnable = Runnable {
                onNext()
                slideShowHandler?.postDelayed(slideShowRunnable, properties.slideShowDuration)
            }
        } else {
            slideShowHandler?.removeCallbacks(slideShowRunnable)
            slideShowHandler = null
            slideShowRunnable = Runnable { }
        }
    }

    private fun setupViewPager(pagerAdapter: BannerPagerAdapter) {
        with(viewPager) {
            offscreenPageLimit = pagerAdapter.itemCount
            adapter = pagerAdapter
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    isFirstSlide = isFirstSlide(position)
                    isLastSlide = isLastSlide(position)
                    activateBullet(position)
                    checkArrows()
                    listener?.onSlideSelected(slides[position], position, isLastSlide)
                }

                override fun onPageScrollStateChanged(state: Int) {
                    if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                        slideShowHandler?.removeCallbacks(slideShowRunnable)
                        isDragging = true
                    }
                    else if (state == ViewPager2.SCROLL_STATE_SETTLING && isDragging) {
                        slideShowHandler?.postDelayed(slideShowRunnable, properties.slideShowDuration)
                        isDragging = false
                    }
                }
            })
            // Activate the first page
            currentItem = 0
        }
    }

    private fun setupBulletsIndicator() {
        if (properties.showBullets) {
            bulletsIndicator.removeAllViews()
            bullets = Array(slides.size) {
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
            val bulletsList = bullets!!
            if (bulletsList.isNotEmpty()) {
                bulletsList[0].setTextColor(properties.activeBulletColor)
            }
        }
    }

    private fun activateBullet(position: Int) {
        if (bullets == null) {
            return
        }
        val bulletsList = bullets!!
        bulletsList.forEach { it.setTextColor(properties.inactiveBulletColor) }
        if (bulletsList.isNotEmpty()) {
            bulletsList[position].setTextColor(properties.activeBulletColor)
        }
    }

    private fun showArrows(show: Boolean) {
        if (show) {
            nextBTN.visibility = View.VISIBLE
            previousBTN.visibility = View.VISIBLE
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
            viewPager.setCurrentItem(0, true)
            return
        }
        var currentIndex = viewPager.currentItem
        viewPager.setCurrentItem(++currentIndex, true)
        if (fromArrow) {
            resetSlideShow()
        }
    }

    private fun onPrevious(fromArrow: Boolean = true) {
        var currentIndex = viewPager.currentItem
        viewPager.setCurrentItem(--currentIndex, true)
        if (fromArrow) {
            resetSlideShow()
        }
    }

    private fun selectSlide(position: Int) {
        resetSlideShow()
        viewPager.setCurrentItem(position, true)
    }

    private fun resetSlideShow() {
        slideShowHandler?.removeCallbacks(slideShowRunnable)
        slideShowHandler?.postDelayed(slideShowRunnable, properties.slideShowDuration)
    }

    private fun isFirstSlide(position: Int) = (position == 0)

    private fun isLastSlide(position: Int) = (position == slides.size - 1)

    private class BannerPagerAdapter(
        private val slides: List<Slide>,
        private val onSlideView: (LayoutInflater, ViewGroup?) -> SlideView
    ) : RecyclerView.Adapter<BannerPagerAdapter.SlideViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlideViewHolder {
            return SlideViewHolder(onSlideView(LayoutInflater.from(parent.context), parent))
        }

        override fun onBindViewHolder(holder: SlideViewHolder, position: Int) {
            holder.bind(position, slides[position])
        }

        override fun getItemCount() = slides.size

        class SlideViewHolder(
            private val slideView: SlideView
        ) : RecyclerView.ViewHolder(slideView.view) {
            internal fun bind(position: Int, slide: Slide) = slideView.bind(position, slide)
        }
    }
}

data class Slide(val imageUrl: String? = null, val onClick: () -> Unit = { })

abstract class SlideView {
    abstract val view: View
    abstract fun bind(position: Int, slide: Slide)
}

private data class Properties(
    var showArrows: Boolean,
    var showBullets: Boolean,
    var autoScrollEnabled: Boolean,
    var slideShowDuration: Long,
    @ColorInt var activeBulletColor: Int,
    @ColorInt var inactiveBulletColor: Int
)