package com.kotlin.indicator

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.support.constraint.ConstraintSet
import android.view.View.generateViewId
import android.widget.Button
import com.kotlin.indicator.horizontal.HorizontalIndicator


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //horizontalIndicator.addView(R.drawable.ic_child , R.string.temp)
        val data = mutableListOf<HorizontalIndicator.ViewData>()
        data.add(HorizontalIndicator.ViewData(R.drawable.ic_child , R.string.temp))
        data.add(HorizontalIndicator.ViewData(R.drawable.ic_child , R.string.temp1))
        data.add(HorizontalIndicator.ViewData(R.drawable.ic_child , R.string.temp))
        data.add(HorizontalIndicator.ViewData(R.drawable.ic_child , R.string.temp1))
        data.add(HorizontalIndicator.ViewData(R.drawable.ic_child , R.string.temp))
        data.add(HorizontalIndicator.ViewData(R.drawable.ic_child , R.string.temp))
        data.add(HorizontalIndicator.ViewData(R.drawable.ic_child , R.string.temp1))
        data.add(HorizontalIndicator.ViewData(R.drawable.ic_child , R.string.temp))
        data.add(HorizontalIndicator.ViewData(R.drawable.ic_child , R.string.temp1))
        data.add(HorizontalIndicator.ViewData(R.drawable.ic_child , R.string.temp))
        data.add(HorizontalIndicator.ViewData(R.drawable.ic_child , R.string.temp))
        data.add(HorizontalIndicator.ViewData(R.drawable.ic_child , R.string.temp1))
        data.add(HorizontalIndicator.ViewData(R.drawable.ic_child , R.string.temp))
        data.add(HorizontalIndicator.ViewData(R.drawable.ic_child , R.string.temp1))
        data.add(HorizontalIndicator.ViewData(R.drawable.ic_child , R.string.temp))
//        data.add(HorizontalIndicator.ViewData(R.drawable.ic_child , R.string.temp))
//        data.add(HorizontalIndicator.ViewData(R.drawable.ic_child , R.string.temp))
//        data.add(HorizontalIndicator.ViewData(R.drawable.ic_child , R.string.temp))

        horizontalIndicator.addViews(data)
//        val set = ConstraintSet()
//        set.clone(background)
//
//        //Button 1:
//        val button = Button(this)
//        button.setText("Hello")
//        button.id = generateViewId()       // <-- Important
//        background.addView(button)
//        set.connect(button.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
//        set.connect(button.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0)
//        set.connect(button.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0)
//        set.constrainHeight(button.getId(), 200)
//        set.applyTo(background)
//
//
//        //Button 2:
//        val newButton = Button(this)
//        newButton.setText("Yeeey")
//        newButton.id = generateViewId()
//        background.addView(newButton)
//        set.connect(newButton.getId(), ConstraintSet.BOTTOM, button.getId(), ConstraintSet.TOP, 0)
//        set.connect(newButton.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0)
//        set.connect(newButton.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0)
//        set.constrainHeight(newButton.getId(), 200)
//        set.applyTo(background)
    }
}
