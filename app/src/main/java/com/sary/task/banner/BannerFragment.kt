package com.sary.task.banner

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import butterknife.BindView
import butterknife.ButterKnife
import com.sary.task.R

/**
 * A simple slider, could be optimized and extracted into a library module.
 */
@SuppressLint("NonConstantResourceId")
class BannerFragment : Fragment() {

    @BindView(R.id.btn_next)
    lateinit var nextBTN: View

    @BindView(R.id.btn_previous)
    lateinit var previousBTN: View

    @BindView(R.id.bullets_indicator)
    lateinit var bulletsIndicator: LinearLayout

    @BindView(R.id.view_pager)
    lateinit var viewPager: ViewPager // TODO: Replace it with ViewPager2

    private lateinit var slideViews: Array<View>
    private lateinit var slideFragments: Array<Fragment>
    private lateinit var pagerAdapter: PagerAdapter
    private var bullets: Array<TextView>? = null

    private lateinit var properties: BannerProperties
    private var callback: Callback? = null

    private var handler: Handler? = null
    private var slideShowRunnable: Runnable = Runnable { }
    private var isDragging = false
    private var duration = 0L // in milliseconds
    private var isFirstSlide = false
    private var isLastSlide = false

    interface Callback {
        fun onSlideSelected(view: View?, position: Int, isLastSlide: Boolean) { }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        properties = requireArguments().getParcelable(KEY_PROPERTIES)!!
        // Slides
        val slidesCount = slideViews.size
        slideFragments = Array(slidesCount) { i -> BannerItemFragment.newInstance(slideViews[i]) }
        // SlideShow
        if (properties.autoScrollEnabled) {
            duration = properties.slideShowDuration
            handler = Handler(Looper.getMainLooper())
            slideShowRunnable = Runnable {
                onNext()
                handler?.postDelayed(slideShowRunnable, duration)
            }
        }
        pagerAdapter = PagerAdapter(childFragmentManager)
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_banner, container, false)
        ButterKnife.bind(this, view)
        setupBulletsIndicator()
        setupViewPager()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Show/Hide arrows
        if (properties.showArrows) {
            with(nextBTN) { visibility = View.VISIBLE; setOnClickListener { onNext() } }
            with(previousBTN) { visibility = View.VISIBLE; setOnClickListener { onPrevious() } }
        } else {
            nextBTN.visibility = View.GONE
            previousBTN.visibility = View.GONE
        }
        // Show/Hide Bullets
        bulletsIndicator.visibility = if (properties.showBullets) View.VISIBLE else View.GONE
    }

    override fun onPause() {
        super.onPause()
        handler?.removeCallbacks(slideShowRunnable)
    }

    override fun onResume() {
        super.onResume()
        handler?.postDelayed(slideShowRunnable, duration)
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
                    val view = slideFragments[position].view
                    callback?.onSlideSelected(view, getLocaleBasedPosition(position), isLastSlide)
                }

                override fun onPageScrollStateChanged(state: Int) {
                    if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                        this@BannerFragment.handler?.removeCallbacks(slideShowRunnable)
                        isDragging = true
                    }
                    else if (state == ViewPager.SCROLL_STATE_SETTLING && isDragging) {
                        this@BannerFragment.handler?.postDelayed(slideShowRunnable, duration)
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

    private fun onNext() {
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
    }

    private fun onPrevious() {
        // TODO: Implement this method
    }

    @Suppress("unused")
    private fun selectSlide(position: Int) {
        resetSlideShow()
        viewPager.setCurrentItem(getLocaleBasedPosition(position), true)
    }

    private fun resetSlideShow() {
        handler?.removeCallbacks(slideShowRunnable)
        handler?.postDelayed(slideShowRunnable, duration)
    }

    private fun getLocaleBasedPosition(position: Int): Int =
        if (properties.isRtl) pagerAdapter.count - position - 1 else position

    private fun isFirstSlide(position: Int): Boolean = getLocaleBasedPosition(position) == 0

    private fun isLastSlide(position: Int): Boolean =
        getLocaleBasedPosition(position) == (pagerAdapter.count - 1)

    private inner class PagerAdapter(
        fm: FragmentManager
    ) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getCount(): Int = slideFragments.size

        override fun getItem(position: Int) = slideFragments[getLocaleBasedPosition(position)]
    }

    companion object {
        private const val KEY_PROPERTIES = "KEY_PROPERTIES"

        @JvmStatic
        fun newInstance(
            slideViews: Array<View>,
            properties: BannerProperties = BannerProperties(),
            callback: Callback? = null
        ): BannerFragment = BannerFragment().apply {
            this.callback = callback
            this.slideViews = slideViews
            arguments = Bundle().apply { putParcelable(KEY_PROPERTIES, properties) }
        }
    }
}

class BannerItemFragment : Fragment() {
    private lateinit var itemView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = itemView

    companion object {
        @JvmStatic
        fun newInstance(view: View): BannerItemFragment = BannerItemFragment().apply {
            itemView = view
        }
    }
}