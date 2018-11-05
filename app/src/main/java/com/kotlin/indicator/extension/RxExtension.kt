package com.kotlin.indicator.extension

import io.reactivex.Completable

fun Completable.together(animation : Completable): Completable {
    return Completable.mergeArray(this , animation)
}