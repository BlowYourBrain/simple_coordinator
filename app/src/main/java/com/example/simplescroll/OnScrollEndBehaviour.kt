package com.example.simplescroll

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView

class OnScrollEndBehaviour : CoordinatorLayout.Behavior<View> {

    constructor() : super()

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    private var buttonHeight: Int = 0

    init {
        Log.d("fuck", "init onScrollEndBehaviour")
    }

    override fun onLayoutChild(parent: CoordinatorLayout, child: View, layoutDirection: Int): Boolean {
        parent.clipChildren = false
        parent.clipToPadding = false
        buttonHeight = child.measuredHeight
        child.x = 0f
        child.y = parent.bottom.toFloat()
        return false
    }

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL
    }

    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int
    ) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type)
        val scrollChild = (target as ViewGroup).getChildAt(0)
        val nestedScrollView: NestedScrollView = (target as NestedScrollView)
        val diff = scrollChild.bottom - nestedScrollView.height - nestedScrollView.scrollY

        if (diff <= buttonHeight) {
            child.y = (coordinatorLayout.bottom - buttonHeight + diff).toFloat()
        } else if (child.y < coordinatorLayout.bottom) {
            child.y = coordinatorLayout.bottom.toFloat()
        }
    }

}