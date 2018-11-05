package com.kotlin.indicator.horizontal

import android.content.Context
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.support.constraint.ConstraintSet
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.request.RequestOptions
import com.kotlin.indicator.R
import com.kotlin.indicator.extension.fadeIn
import com.kotlin.indicator.extension.moveX
import com.kotlin.indicator.extension.together
import com.kotlin.indicator.glide.GlideApp
import kotlinx.android.synthetic.main.horizontal_indicator.view.*
import org.jetbrains.annotations.NotNull


class HorizontalIndicator : HorizontalScrollView, IHorizontalIndicator {
    private val view: View
    private val imageIdList = mutableListOf<Int>()
    private val textIdList = mutableListOf<Int>()
    private lateinit var indicator : ImageView
    private val indicatorPositionXMap = linkedMapOf<Int , Float>()
    private val textXWithList = mutableListOf<Float>()
    private var screenWidth : Int = 0

    constructor(context: Context) : super(context) {
        view = View.inflate(context, R.layout.horizontal_indicator, this)
        initViews(view)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        view = View.inflate(context, R.layout.horizontal_indicator, this)
        initViews(view)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        view = View.inflate(context, R.layout.horizontal_indicator, this)
        initViews(view)
    }

    private fun initViews(view: View) {

        isVerticalScrollBarEnabled = false
        isHorizontalScrollBarEnabled = false
        overScrollMode = View.OVER_SCROLL_NEVER;

        indicator = ImageView(context)
        indicator.id = generateViewId()
        indicator.setBackgroundResource(R.drawable.triangle)
    }

    override fun addViews(data: List<ViewData>) {
        for (value in data) {
            if (imageIdList.isEmpty()) {
                addView(value.imageRes, value.textRes)
            } else {
                addAfterFirstViews(value.imageRes , value.textRes)
            }
        }
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)

        screenWidth = measuredWidth
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)

        val curViewWidth = screenWidth + l

        if(l - oldl > 0){
            if(l > indicator.x){

                for(x in indicatorPositionXMap.iterator()){
                    if(l < x.value){
                        indicator.moveX(x.value , 300).subscribe()
                        break
                    }
                }
            }
        }else{
            if (curViewWidth < indicator.x) {
                for (i in imageIdList.size - 1 downTo 0) {
                    if (indicatorPositionXMap[imageIdList[i]]!! < curViewWidth) {
                        indicator.moveX(indicatorPositionXMap[imageIdList[i]]!!, 300).subscribe()
                        break
                    }
                }
            }
        }

    }

    private fun addView(@NotNull @DrawableRes imageRes: Int, @NotNull @StringRes textRes: Int) {
        //new textView
        val textView = TextView(context)
        textView.id = generateViewId()
        textView.text = context.getText(textRes)
        textView.minWidth = dpToPx(100)
        textView.gravity = Gravity.CENTER_HORIZONTAL
        constraintLayout.addView(textView)
        //new imageView
        val imageView = ImageView(context)
        imageView.id = generateViewId()
        imageView.minimumWidth = dpToPx(100)
        imageView.maxHeight = dpToPx(100)
        imageView.setPadding(dpToPx(5), dpToPx(5), dpToPx(5), dpToPx(5))

        constraintLayout.addView(imageView)

        GlideApp.with(view.context)
            .load(imageRes)
            .apply(RequestOptions.circleCropTransform())
            .into(imageView)

        //add indicator
        constraintLayout.addView(indicator)

        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)

        //textView
        constraintSet.constrainWidth(textView.id, ConstraintSet.MATCH_CONSTRAINT)
        constraintSet.constrainHeight(textView.id, ConstraintSet.WRAP_CONTENT)
        constraintSet.connect(textView.id, ConstraintSet.START, constraintLayout.id, ConstraintSet.START, dpToPx(8))
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
        constraintSet.connect(indicator.id, ConstraintSet.TOP , textView.id , ConstraintSet.BOTTOM)
        constraintSet.connect(indicator.id, ConstraintSet.BOTTOM , constraintLayout.id , ConstraintSet.BOTTOM)

        constraintSet.applyTo(constraintLayout)

        imageIdList.add(imageView.id)
        textIdList.add(textView.id)

        val textWidth =  Math.max(getTextWidth(textView , textRes) , dpToPx(100).toFloat())
        textXWithList.add(textWidth)

        val textCenterX = textWidth / 2
        indicatorPositionXMap[imageView.id] = dpToPx(8) + textCenterX

        indicator.x = indicatorPositionXMap[imageView.id]!!

        imageView.setOnClickListener {

            indicator.moveX(indicatorPositionXMap[it.id]!! , 700)
                .together(imageView.fadeIn(500))
//                .andThen(image1.beNormal())
//                .andThen(image2.beNormal())
//                .andThen(image3.beNormal())
                .subscribe()
        }
    }

    private fun getTextWidth(textView: TextView , textRes : Int) : Float{
        val textPaint = textView.paint
        return textPaint.measureText(context.getString(textRes))
    }

    private fun addAfterFirstViews(@NotNull @DrawableRes imageRes: Int, @NotNull @StringRes textRes: Int){
        val lastImageId = imageIdList[imageIdList.size - 1]
        val lastTextId = textIdList[textIdList.size - 1]

        val textView = TextView(context)
        textView.id = generateViewId()
        textView.text = context.getText(textRes)
        textView.minWidth = dpToPx(100)
        textView.gravity = Gravity.CENTER_HORIZONTAL
        constraintLayout.addView(textView)

        val imageView = ImageView(context)
        imageView.id = generateViewId()
        imageView.minimumWidth = dpToPx(100)
        imageView.maxHeight = dpToPx(100)
        imageView.setPadding(dpToPx(5), dpToPx(5), dpToPx(5), dpToPx(5))
        constraintLayout.addView(imageView)

        GlideApp.with(view.context)
            .load(imageRes)
            .apply(RequestOptions.circleCropTransform())
            .into(imageView)

        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)

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

        val textWidth = Math.max(getTextWidth(textView , textRes) , dpToPx(100).toFloat())
        var textCenterX = textWidth / 2
        textXWithList.forEach {
            textCenterX += it + dpToPx(8)
        }
        textXWithList.add(textWidth)

        indicatorPositionXMap[imageView.id] = textCenterX + dpToPx(8)

        imageView.setOnClickListener {

            indicator.moveX(indicatorPositionXMap[it.id]!! , 700)
                .together(imageView.fadeIn(500))
//                .andThen(image1.beNormal())
//                .andThen(image2.beNormal())
//                .andThen(image3.beNormal())
                .subscribe()
        }
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