package com.kotlin.indicator.extension

import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.util.Log
import android.view.View
import android.view.animation.OvershootInterpolator
import com.kotlin.indicator.R
import io.reactivex.Completable
import io.reactivex.subjects.CompletableSubject

fun View.moveX(x : Float, duration: Long): Completable {
    val animationSubject = CompletableSubject.create()
    return animationSubject.doOnSubscribe {
        ViewCompat.animate(this)
            .translationX(x)
            .setDuration(duration)
            .setInterpolator(OvershootInterpolator(0.5f))
            .withEndAction {
                animationSubject.onComplete()
            }
    }
}

fun View.fadeIn(duration: Long): Completable {

    val animationSubject = CompletableSubject.create()
    return animationSubject.doOnSubscribe {
        this.alpha = 0.2f
        this.background = ContextCompat.getDrawable(context , R.drawable.circle_blue)
        ViewCompat.animate(this)
            .setDuration(duration)
            .alpha(1f)
            .withEndAction {
                animationSubject.onComplete()
            }
    }
}

fun View.beNormal(): Completable {

    val animationSubject = CompletableSubject.create()
    return animationSubject.doOnSubscribe {
        this.alpha = 1f
        this.background = ContextCompat.getDrawable(context , R.drawable.circle_gray)
        animationSubject.onComplete()
    }
}