package com.example.simplescroll

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView

class OnScrollEndBehaviour : CoordinatorLayout.Behavior<View> {

    constructor() : super()

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    private var buttonHeight: Int = 0

    override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        return dependency is NestedScrollView
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        parent.clipChildren = false
        parent.clipToPadding = false

        buttonHeight = child.measuredHeight

        val scrollChild = (dependency as ViewGroup).getChildAt(0).apply {
            if (paddingBottom < buttonHeight) {
                setPadding(paddingLeft, paddingTop, paddingRight, buttonHeight)
            }
        }

        val parentScrollHeightDifference =
            parent.measuredHeight - (scrollChild.measuredHeight - scrollChild.paddingBottom)

        if (parentScrollHeightDifference > buttonHeight) {
            return false
        }

        if (parentScrollHeightDifference > 0) {
            child.y = (parent.bottom - parentScrollHeightDifference).toFloat()
            return true
        }

        child.y = parent.bottom.toFloat()

        return true
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

        val scrollViewChild = (target as ViewGroup).getChildAt(0)
        val nestedScrollView: NestedScrollView = (target as NestedScrollView)

        val diff = scrollViewChild.bottom - nestedScrollView.height - nestedScrollView.scrollY

        if (diff <= buttonHeight) {
            child.y = (coordinatorLayout.bottom - buttonHeight + diff).toFloat()
        } else if (child.y < coordinatorLayout.bottom) {
            child.y = coordinatorLayout.bottom.toFloat()
        }
    }

}