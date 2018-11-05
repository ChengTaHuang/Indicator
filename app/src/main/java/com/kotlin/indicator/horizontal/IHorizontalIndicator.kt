package com.kotlin.indicator.horizontal

import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import org.jetbrains.annotations.NotNull

interface IHorizontalIndicator {

    //fun addView(@NotNull @DrawableRes imageRes : Int, @NotNull @StringRes textRes : Int)

    fun addViews(data : List<HorizontalIndicator.ViewData>)
}