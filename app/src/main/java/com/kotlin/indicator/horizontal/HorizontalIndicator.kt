package com.kotlin.indicator.horizontal

import android.annotation.SuppressLint
import android.content.Context
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.support.constraint.ConstraintSet
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.request.RequestOptions
import com.kotlin.indicator.R
import com.kotlin.indicator.extension.beNormal
import com.kotlin.indicator.extension.fadeIn
import com.kotlin.indicator.extension.moveX
import com.kotlin.indicator.extension.together
import com.kotlin.indicator.glide.GlideApp
import io.reactivex.Completable
import kotlinx.android.synthetic.main.horizontal_indicator.view.*
import org.jetbrains.annotations.NotNull


class HorizontalIndicator : HorizontalScrollView, IHorizontalIndicator {
    private val view: View
    private val imageIdList = mutableListOf<Int>()
    private val textIdList = mutableListOf<Int>()
    private val textMinimumWidth = dpToPx(100)
    private val imageWidth = textMinimumWidth
    private val imageHeight = textMinimumWidth
    private lateinit var indicator: ImageView
    private val indicatorPositionXMap = linkedMapOf<Int, Float>()
    private val textXWithList = mutableListOf<Float>()
    private var screenWidth: Int = 0

    private lateinit var scrollerTask: Runnable
    private var indicatorXRecord: Int = 0
    private var direction: Direction = Direction.NONE

    sealed class Direction {
        object LEFT : Direction()
        object RIGHT : Direction()
        object NONE : Direction()
    }

    private var clickCallback: ((Int) -> Unit)? = null
    fun setOnClickItemListener(callback: (Int) -> Unit) {
        this.clickCallback = callback
    }

    private var authMoveCallback: ((Int) -> Unit)? = null
    fun setOnAutoMoveListener(callback: (Int) -> Unit) {
        this.authMoveCallback = callback
    }

    constructor(context: Context) : super(context) {
        view = View.inflate(context, R.layout.horizontal_indicator, this)
        initViews()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        view = View.inflate(context, R.layout.horizontal_indicator, this)

        initViews()
        if (isInEditMode) {
            initFakeView()
        } else {
            scrollerTask = Runnable {
                val curX = scrollX
                if (indicatorXRecord - curX == 0) {
                    if (direction != Direction.NONE) {
                        autoMoveIndicator(indicator, direction, curX)
                    }
                    direction = Direction.NONE
                } else {
                    indicatorXRecord = scrollX
                    this.postDelayed(scrollerTask, 100)
                }
            }
        }
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        view = View.inflate(context, R.layout.horizontal_indicator, this)
        initViews()
        if (isInEditMode) {
            initFakeView()
        }
    }

    private fun initViews() {

        isVerticalScrollBarEnabled = false
        isHorizontalScrollBarEnabled = false
        overScrollMode = View.OVER_SCROLL_NEVER

        indicator = ImageView(context)
        indicator.id = generateViewId()
        indicator.setBackgroundResource(R.drawable.triangle)
    }

    private fun initFakeView() {

        val params = constraintLayout.layoutParams as FrameLayout.LayoutParams
        params.height = dpToPx(129)
        constraintLayout.layoutParams = params

        val textRes = R.string.app_name
        //new textView
        val textView = createTextView(textRes)
        constraintLayout.addView(textView)
        //new imageView
        val imageView = ImageView(context)
        imageView.id = generateViewId()
        imageView.minimumWidth = imageWidth
        imageView.maxHeight = imageHeight
        imageView.setPadding(dpToPx(5), dpToPx(5), dpToPx(5), dpToPx(5))
        imageView.setBackgroundResource(R.drawable.circle_gray)

        constraintLayout.addView(imageView)

        //add indicator
        constraintLayout.addView(indicator)

        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)

        //textView
        constraintSet.constrainWidth(textView.id, ConstraintSet.MATCH_CONSTRAINT)
        constraintSet.constrainHeight(textView.id, ConstraintSet.WRAP_CONTENT)
        constraintSet.connect(textView.id, ConstraintSet.START, constraintLayout.id, ConstraintSet.START, 0)
        constraintSet.connect(textView.id, ConstraintSet.TOP, imageView.id, ConstraintSet.BOTTOM, 0)

        //imageView
        constraintSet.setDimensionRatio(imageView.id, "1:1")
        constraintSet.constrainWidth(imageView.id, ConstraintSet.WRAP_CONTENT)
        constraintSet.constrainHeight(imageView.id, ConstraintSet.MATCH_CONSTRAINT)
        constraintSet.connect(imageView.id, ConstraintSet.START, textView.id, ConstraintSet.START, 0)
        constraintSet.connect(imageView.id, ConstraintSet.TOP, constraintLayout.id, ConstraintSet.TOP, dpToPx(10))
        constraintSet.connect(imageView.id, ConstraintSet.END, textView.id, ConstraintSet.END, 0)

        constraintSet.constrainWidth(indicator.id, dpToPx(10))
        constraintSet.constrainHeight(indicator.id, dpToPx(10))
        constraintSet.connect(indicator.id, ConstraintSet.TOP, textView.id, ConstraintSet.BOTTOM)
        constraintSet.connect(indicator.id, ConstraintSet.BOTTOM, constraintLayout.id, ConstraintSet.BOTTOM)

        constraintSet.applyTo(constraintLayout)

        imageIdList.add(imageView.id)
        textIdList.add(textView.id)

        val textWidth = Math.max(getTextWidth(textView, textRes), textMinimumWidth.toFloat())
        textXWithList.add(textWidth)

        val textCenterX = textWidth / 2
        indicatorPositionXMap[imageView.id] = dpToPx(8) + textCenterX

        indicator.x = indicatorPositionXMap[imageView.id]!!
    }

    override fun addViews(data: List<ViewData>) {
        for (value in data) {
            if (imageIdList.isEmpty()) {
                addFirstView(value.imageRes, value.textRes)
            } else {
                addAfterFirstView(value.imageRes, value.textRes)
            }
        }
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)

        screenWidth = measuredWidth
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_UP) {
            startScrollerTask()
        }

        return super.onTouchEvent(ev)
    }

    private fun startScrollerTask() {
        indicatorXRecord = scrollX
        postDelayed(scrollerTask, 10)
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        direction = if (l - oldl > 0) Direction.LEFT else Direction.RIGHT
    }

    @SuppressLint("CheckResult")
    private fun autoMoveIndicator(indicator: ImageView, direction: Direction, curX: Int) {
        val curViewWidth = screenWidth + curX
        if (direction == Direction.LEFT) {
            if (curX >= indicator.x) {

                for (x in indicatorPositionXMap.iterator()) {
                    if (curX < x.value) {
                        var flow = indicator.moveX(x.value, 300)

                        imageIdList
                            .forEach { id ->
                                val imageView = findViewById<ImageView>(id)

                                flow = if (x.key == id) {
                                    flow.together(imageView.fadeIn(500))
                                } else {
                                    flow.together(imageView.beNormal())
                                }

                                if(x.key == id) authMoveCallback?.invoke(imageIdList.indexOf(id))
                            }

                        flow.subscribe()

                        break
                    }
                }
            } else {
                postDelayed(scrollerTask, 10)
            }
        } else if (direction == Direction.RIGHT) {
            if (curViewWidth <= indicator.x) {
                var flow: Completable? = null
                for (i in 1 until imageIdList.size) {
                    val frontId = imageIdList[i - 1]
                    val id = imageIdList[i]
                    val frontImageView = findViewById<ImageView>(frontId)
                    val imageView = findViewById<ImageView>(id)

                    if (indicatorPositionXMap[frontId]!! < curViewWidth && indicatorPositionXMap[id]!! >= curViewWidth) {
                        flow = indicator.moveX(indicatorPositionXMap[frontId]!!, 300)
                            .together(frontImageView.fadeIn(500))
                            .together(imageView.beNormal())

                        authMoveCallback?.invoke(i)
                    } else if (indicatorPositionXMap[frontId]!! > curViewWidth && indicatorPositionXMap[id]!! > curViewWidth) {
                        flow = flow?.together(frontImageView.beNormal())
                    }
                }
                flow?.subscribe()
            } else {
                postDelayed(scrollerTask, 10)
            }
        }
    }

    private fun addFirstView(@NotNull @DrawableRes imageRes: Int, @NotNull @StringRes textRes: Int) {
        //new textView
        val textView = createTextView(textRes)
        constraintLayout.addView(textView)
        //new imageView
        val imageView = createImageView(imageRes)
        constraintLayout.addView(imageView)

        //add indicator
        constraintLayout.addView(indicator)

        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)

        //setting constraintLayout relationship
        //textView
        constraintSet.constrainWidth(textView.id, ConstraintSet.MATCH_CONSTRAINT)
        constraintSet.constrainHeight(textView.id, ConstraintSet.WRAP_CONTENT)
        constraintSet.connect(textView.id, ConstraintSet.START, constraintLayout.id, ConstraintSet.START, 0)
        constraintSet.connect(textView.id, ConstraintSet.TOP, imageView.id, ConstraintSet.BOTTOM, 0)

        //imageView
        constraintSet.setDimensionRatio(imageView.id, "1:1")
        constraintSet.constrainWidth(imageView.id, ConstraintSet.WRAP_CONTENT)
        constraintSet.constrainHeight(imageView.id, ConstraintSet.MATCH_CONSTRAINT)
        constraintSet.connect(imageView.id, ConstraintSet.START, textView.id, ConstraintSet.START, 0)
        constraintSet.connect(imageView.id, ConstraintSet.TOP, constraintLayout.id, ConstraintSet.TOP, dpToPx(10))
        constraintSet.connect(imageView.id, ConstraintSet.END, textView.id, ConstraintSet.END, 0)

        constraintSet.constrainWidth(indicator.id, dpToPx(10))
        constraintSet.constrainHeight(indicator.id, dpToPx(10))
        constraintSet.connect(indicator.id, ConstraintSet.TOP, textView.id, ConstraintSet.BOTTOM)
        constraintSet.connect(indicator.id, ConstraintSet.BOTTOM, constraintLayout.id, ConstraintSet.BOTTOM)

        constraintSet.applyTo(constraintLayout)

        imageIdList.add(imageView.id)
        textIdList.add(textView.id)

        val textWidth = Math.max(getTextWidth(textView, textRes), dpToPx(100).toFloat())
        textXWithList.add(textWidth)

        val textCenterX = textWidth / 2
        indicatorPositionXMap[imageView.id] = textCenterX - dpToPx(5)

        //第一次出現，直接減去指示器寬度
        indicator.moveX(indicatorPositionXMap[imageView.id]!!, 0)
            .together(imageView.fadeIn(1)).subscribe()

    }

    private fun getTextWidth(textView: TextView, textRes: Int): Float {
        val textPaint = textView.paint
        return textPaint.measureText(context.getString(textRes))
    }

    private fun addAfterFirstView(@NotNull @DrawableRes imageRes: Int, @NotNull @StringRes textRes: Int) {
        val lastTextId = textIdList[textIdList.size - 1]

        val textView = createTextView(textRes)
        constraintLayout.addView(textView)

        val imageView = createImageView(imageRes)
        constraintLayout.addView(imageView)

        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)

        //setting constraintLayout relationship
        //textView
        constraintSet.constrainWidth(textView.id, ConstraintSet.MATCH_CONSTRAINT)
        constraintSet.constrainHeight(textView.id, ConstraintSet.WRAP_CONTENT)
        constraintSet.connect(textView.id, ConstraintSet.START, lastTextId, ConstraintSet.END, dpToPx(8))
        constraintSet.connect(textView.id, ConstraintSet.TOP, imageView.id, ConstraintSet.BOTTOM, 0)

        //imageView
        constraintSet.setDimensionRatio(imageView.id, "1:1")
        constraintSet.constrainWidth(imageView.id, ConstraintSet.WRAP_CONTENT)
        constraintSet.constrainHeight(imageView.id, ConstraintSet.MATCH_CONSTRAINT)
        constraintSet.connect(imageView.id, ConstraintSet.START, textView.id, ConstraintSet.START, 0)
        constraintSet.connect(imageView.id, ConstraintSet.TOP, constraintLayout.id, ConstraintSet.TOP, dpToPx(10))
        constraintSet.connect(imageView.id, ConstraintSet.END, textView.id, ConstraintSet.END, 0)
        constraintSet.applyTo(constraintLayout)

        imageIdList.add(imageView.id)
        textIdList.add(textView.id)

        val textWidth = Math.max(getTextWidth(textView, textRes), textMinimumWidth.toFloat())
        var textCenterX = textWidth / 2

        for ((index, value) in textXWithList.withIndex()) {
            textCenterX += if (index == 0) {
                value
            } else {
                (value + dpToPx(8))
            }
        }

        textXWithList.add(textWidth)

        indicatorPositionXMap[imageView.id] = textCenterX + dpToPx(8) - dpToPx(5)
    }

    private fun createTextView(textRes: Int): TextView {
        val textView = TextView(context)
        with(textView) {
            id = generateViewId()
            text = context.getText(textRes)
            minWidth = textMinimumWidth
            gravity = Gravity.CENTER_HORIZONTAL
        }

        return textView
    }

    private fun createImageView(imageRes: Int): ImageView {
        val imageView = ImageView(context)
        with(imageView) {
            id = generateViewId()
            minimumWidth = imageWidth
            maxHeight = imageHeight
            setPadding(dpToPx(5), dpToPx(5), dpToPx(5), dpToPx(5))
            setBackgroundResource(R.drawable.circle_gray)
        }

        GlideApp.with(view.context)
            .load(imageRes)
            .apply(RequestOptions.circleCropTransform())
            .into(imageView)

        imageView.setOnClickListener { view ->
            var flow = indicator.moveX(indicatorPositionXMap[view.id]!!, 500)
                .together(imageView.fadeIn(500))

            imageIdList
                .filter { it != view.id }
                .forEach { id ->
                    flow = flow.together(findViewById<ImageView>(id).beNormal())
                }

            flow.subscribe()

            clickCallback?.invoke(imageIdList.indexOf(view.id))
        }

        return imageView
    }

    private fun dpToPx(dp: Int): Int {
        val displayMetrics = context.resources.displayMetrics
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
    }

    data class ViewData(
        @NotNull @DrawableRes val imageRes: Int,
        @NotNull @StringRes val textRes: Int
    )
}