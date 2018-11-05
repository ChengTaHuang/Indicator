package com.kotlin.indicator

import android.animation.AnimatorSet
import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.request.RequestOptions
import com.kotlin.indicator.glide.GlideApp
import io.reactivex.Completable
import io.reactivex.subjects.CompletableSubject
import kotlinx.android.synthetic.main.indicator.view.*
import org.w3c.dom.Text
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.view.animation.*
import io.reactivex.CompletableSource
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction


class Triangle : ConstraintLayout {
    private val view: View

    private lateinit var image1 : ImageView
    private lateinit var text1 : TextView
    private lateinit var image2 : ImageView
    private lateinit var text2 : TextView
    private lateinit var image3 : ImageView
    private lateinit var text3 : TextView
    private lateinit var image4 : ImageView
    private lateinit var text4 : TextView
    private lateinit var imageIndicator : ImageView

    private var firstX : Float = 0.0f
    private var secondX : Float = 0.0f
    private var thirdX : Float = 0.0f
    private var fourthX : Float = 0.0f

    var blueBorder  = ContextCompat.getDrawable(context , R.drawable.circle_blue)
    var grayBorder  = ContextCompat.getDrawable(context , R.drawable.circle_gray)

    constructor(context: Context) : super(context) {
        view = View.inflate(context, R.layout.indicator, this)
        initViews(view)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        view = View.inflate(context, R.layout.indicator, this)
        initViews(view)
    }

    private fun initViews(view: View) {
        image1 = view.findViewById(R.id.image1)
        image2 = view.findViewById(R.id.image2)
        image3 = view.findViewById(R.id.image3)
        image4 = view.findViewById(R.id.image4)

        GlideApp.with(view.context)
            .load(R.drawable.ic_child)
            .apply(RequestOptions.circleCropTransform())
            .into(image1)

        GlideApp.with(view.context)
            .load(R.drawable.ic_pre_teen)
            .apply(RequestOptions.circleCropTransform())
            .into(image2)

        GlideApp.with(view.context)
            .load(R.drawable.ic_teen)
            .apply(RequestOptions.circleCropTransform())
            .into(image3)

        image1.setOnClickListener {
            imageIndicator.moveX(firstX , 700)
                .together(image1.fadeIn(500))
                .andThen(image2.beNormal())
                .andThen(image3.beNormal())
                .andThen(image4.beNormal())
                .subscribe()
        }

        image2.setOnClickListener {

            imageIndicator.moveX(secondX , 700)
                .together(image2.fadeIn(500))
                .andThen(image1.beNormal())
                .andThen(image3.beNormal())
                .andThen(image4.beNormal())
                .subscribe()
        }

        image3.setOnClickListener {

            imageIndicator.moveX(thirdX , 700)
                .together(image3.fadeIn(500))
                .andThen(image1.beNormal())
                .andThen(image2.beNormal())
                .andThen(image4.beNormal())
                .subscribe()

        }

        image4.setOnClickListener {

            imageIndicator.moveX(fourthX , 700)
                .together(image4.fadeIn(500))
                .andThen(image1.beNormal())
                .andThen(image2.beNormal())
                .andThen(image3.beNormal())
                .subscribe()
        }

        imageIndicator = view.findViewById(R.id.indicator)

        text1 = view.findViewById(R.id.text1)
        text2 = view.findViewById(R.id.text2)
        text3 = view.findViewById(R.id.text3)
        text4 = view.findViewById(R.id.text4)

        text1.text = "Child"
        text2.text = "Pre-teen"
        text3.text = "Teen"
        text4.text = "Custom"

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        calculateIndex(width)
        moveIndicator(firstX)
    }

    private fun calculateIndex(width : Int){
        val offset = width/8.0f *2
        firstX = width/8.0f
        secondX = firstX + offset
        thirdX = secondX + offset
        fourthX = thirdX + offset
    }

    fun View.moveX(x : Float , duration: Long): Completable {
        val animationSubject = CompletableSubject.create()
        return animationSubject.doOnSubscribe {
            ViewCompat.animate(this)
                .translationX(x)
                .setDuration(duration)
                .setInterpolator(OvershootInterpolator())
                .withEndAction {
                    animationSubject.onComplete()
                }
        }
    }

    fun View.fadeIn(duration: Long): Completable {

        val animationSubject = CompletableSubject.create()
        return animationSubject.doOnSubscribe {
            this.alpha = 0.2f
            this.background = blueBorder
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
            this.background = grayBorder
            animationSubject.onComplete()
        }
    }

    fun Completable.together(animation : Completable): Completable {
        return Completable.mergeArray(this , animation)
    }

    private fun moveIndicator(x : Float){
        imageIndicator.animate()
            .translationX(x)
            .setDuration(700)
            .setInterpolator(BounceInterpolator())
    }
}