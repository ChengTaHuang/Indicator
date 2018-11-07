package com.kotlin.indicator

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.kotlin.indicator.horizontal.HorizontalIndicator
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val data = mutableListOf<HorizontalIndicator.ViewData>()
        data.add(HorizontalIndicator.ViewData(R.drawable.ic_child , R.string.temp))
        data.add(HorizontalIndicator.ViewData(R.drawable.ic_teen , R.string.temp1))
        data.add(HorizontalIndicator.ViewData(R.drawable.ic_pre_teen , R.string.temp2))
        data.add(HorizontalIndicator.ViewData(R.drawable.ic_child , R.string.temp3))
        data.add(HorizontalIndicator.ViewData(R.drawable.ic_child , R.string.temp))
        data.add(HorizontalIndicator.ViewData(R.drawable.ic_teen , R.string.temp1))
        data.add(HorizontalIndicator.ViewData(R.drawable.ic_pre_teen , R.string.temp2))
        data.add(HorizontalIndicator.ViewData(R.drawable.ic_child , R.string.temp3))
        data.add(HorizontalIndicator.ViewData(R.drawable.ic_child , R.string.temp))
        data.add(HorizontalIndicator.ViewData(R.drawable.ic_teen , R.string.temp1))
        data.add(HorizontalIndicator.ViewData(R.drawable.ic_pre_teen , R.string.temp2))
        data.add(HorizontalIndicator.ViewData(R.drawable.ic_child , R.string.temp3))
        data.add(HorizontalIndicator.ViewData(R.drawable.ic_child , R.string.temp))
        data.add(HorizontalIndicator.ViewData(R.drawable.ic_teen , R.string.temp1))
        data.add(HorizontalIndicator.ViewData(R.drawable.ic_pre_teen , R.string.temp2))
        data.add(HorizontalIndicator.ViewData(R.drawable.ic_child , R.string.temp3))
        data.add(HorizontalIndicator.ViewData(R.drawable.ic_child , R.string.temp))
        data.add(HorizontalIndicator.ViewData(R.drawable.ic_teen , R.string.temp1))
        data.add(HorizontalIndicator.ViewData(R.drawable.ic_pre_teen , R.string.temp2))
        data.add(HorizontalIndicator.ViewData(R.drawable.ic_child , R.string.temp3))

        horizontalIndicator.addViews(data)
        horizontalIndicator.setOnClickItemListener {
            Log.i("check" , "$it")
        }
        horizontalIndicator.setOnAutoMoveListener {
            Log.i("check" , "$it")
        }
    }
}
