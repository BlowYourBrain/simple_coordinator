package com.example.simplescroll

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout

class CustomCoordinator : CoordinatorLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    var onScrollListener: ((scrollView: View, scrollViewChild: View) -> Unit)? = null

    override fun onStartNestedScroll(child: View, target: View, nestedScrollAxes: Int): Boolean {
        return true
    }

    override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int) {
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed)

        val scrollView = (target as ViewGroup)
        val scrollViewChild = scrollView.getChildAt(0)

        onScrollListener?.invoke(scrollView, scrollViewChild)
    }
}