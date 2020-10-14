package com.example.simplescroll.swipe_refresh

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.Transformation
import android.widget.FrameLayout
import androidx.core.view.*

private const val AVAILABLE_SCROLL = 0.5f

class IOSSwipeRefreshLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs), NestedScrollingParent {

    private var scrollDistance: Float = 0f
    private var consumedScrollDistance: Float = 0f

    private var scrollableChild: View? = null
    private val internalHierarchyChangeListener = InternalOnHierarchyChangeListener()
    private val nestedScrollingParentHelper = NestedScrollingParentHelper(this)

    /**
     * Значение от 0f до 1.0f
     * При значении 0 прогресс swipe-to-refresh равен 0%,
     * При значении 1.0f прогресс соответственно 100%.
     * */
    var swipeRefreshProgressCallback: ((progress: Float) -> Unit)? = null

    init {
        super.setOnHierarchyChangeListener(internalHierarchyChangeListener)
        internalHierarchyChangeListener.onHierarchyChanged = ::findScrollableContainer
    }

    override fun setOnHierarchyChangeListener(listener: OnHierarchyChangeListener?) {
        internalHierarchyChangeListener.hierarchyChangeListener = listener
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        //обновляем значение scrollDistance т.к. изменен размер контейнера.

        scrollDistance = h.toFloat() / 2
    }

    private val animation = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            scrollableChild?.let {
                val endTarget = 0
                val from = consumedScrollDistance
                val targetTop = from + ((endTarget - from) * interpolatedTime)
                val offset = (targetTop - it.top).toInt()
                ViewCompat.offsetTopAndBottom(it, offset)
                calculateProgress(targetTop, scrollDistance)
            }
        }

    }.apply {
        duration = 150
        interpolator = LinearInterpolator()
        setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {}

            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                consumedScrollDistance = 0f
            }
        })
    }

    //region NestedScrollingParent
    override fun onStartNestedScroll(child: View, target: View, axes: Int): Boolean {
        Log.d("fuck", "onStartNestedScroll")
        return isEnabled && !(animation.hasStarted() xor animation.hasEnded()) && (axes and ViewCompat.SCROLL_AXIS_VERTICAL) != 0
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int) {
        Log.d("fuck", "onNestedScrollAccepted")
        nestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes)
    }

    override fun onStopNestedScroll(target: View) {
        Log.d("fuck", "onStopNestedScroll")
        nestedScrollingParentHelper.onStopNestedScroll(target)
        animation.apply {
            reset()

        }

        scrollableChild?.startAnimation(animation)
    }

    override fun getNestedScrollAxes(): Int {
        return nestedScrollingParentHelper.nestedScrollAxes
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int
    ) {
        Log.d("fuck", "onNestedScroll dyConsumed = $dyConsumed, dyUnConsumed = $dyUnconsumed")
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
        Log.d("fuck", "onNestedPreScroll dy = $dy, consumed[1] = ${consumed[1]}")
        if (dy > 0 && consumedScrollDistance > scrollDistance) {
            consumedScrollDistance = 0f
        }

        if (dy < 0 && consumedScrollDistance <= scrollDistance) {
            val availableConsumeDistance = scrollDistance - consumedScrollDistance

            //т.к. dy < 0, то по сути выражение эквивалентно availableConsumeDistance - dy
            val couldConsume = (availableConsumeDistance + dy) >= 0
            val consume = if (couldConsume) {
                dy
            } else {
                -availableConsumeDistance.toInt()
            }

            //добавляем к пройденному пути путь, который будет обработан.
            consumedScrollDistance -= consume

            consumed[1] = consume

            scrollableChild?.let { nonNullView ->
                ViewCompat.offsetTopAndBottom(nonNullView, -consume)
                calculateProgress(consumedScrollDistance, scrollDistance)
            }
        }
    }
    //endregion

    private fun findScrollableContainer() {
        scrollableChild = children.find { it is NestedScrollingParent2 }
    }

    private fun calculateProgress(offsetDistance: Float, totalDistance: Float) {
        val progress = offsetDistance / totalDistance
        swipeRefreshProgressCallback?.invoke(progress)
    }

}

private class InternalOnHierarchyChangeListener() : ViewGroup.OnHierarchyChangeListener {

    var hierarchyChangeListener: ViewGroup.OnHierarchyChangeListener? = null

    var onHierarchyChanged: (() -> Unit)? = null

    override fun onChildViewRemoved(parent: View?, child: View?) {
        onHierarchyChanged?.invoke()
        hierarchyChangeListener?.onChildViewRemoved(parent, child)
    }

    override fun onChildViewAdded(parent: View?, child: View?) {
        onHierarchyChanged?.invoke()
        hierarchyChangeListener?.onChildViewAdded(parent, child)
    }
}
