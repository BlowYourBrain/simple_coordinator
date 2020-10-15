package com.example.simplescroll.swipe_refresh

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.MotionEvent.*
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.Transformation
import android.widget.FrameLayout
import android.widget.ScrollView
import androidx.core.view.*

private const val AVAILABLE_SCROLL = 0.5f
private const val REFRESH_DETERMINANT_COEFFICIENT = 0.5F

private const val UNDEFINED = -1
private const val SCROLL_UP = 0
private const val SCROLL_DOWN = 1

class IOSSwipeRefreshLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private var scrollDistance: Int = 0
    private var consumedScrollDistance: Int = 0

    private var scrollableChild: View? = null
    private var isScrollConsuming: Boolean = false
    private var firstScrollDirection: Int = UNDEFINED
    private val internalHierarchyChangeListener = InternalOnHierarchyChangeListener()
    private val nestedScrollingParentHelper = NestedScrollingParentHelper(this)


    /**
     * Значение от 0f до 1.0f
     * При значении 0 прогресс swipe-to-refresh равен 0%,
     * При значении 1.0f прогресс соответственно 100%.
     * */
    var swipeRefreshProgressCallback: ((progress: Float) -> Unit)? = null

    /**
     * Callback, срабатывающий если пользователь достаточно провел пальцем по swipe-to-refresh.
     * */
    var onRefreshCallback: (() -> Unit)? = null

    init {
        super.setOnHierarchyChangeListener(internalHierarchyChangeListener)
        internalHierarchyChangeListener.onHierarchyChanged = ::findScrollableContainer
    }

    override fun setOnHierarchyChangeListener(listener: OnHierarchyChangeListener?) {
        internalHierarchyChangeListener.hierarchyChangeListener = listener
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        //обновляем значение scrollDistance т.к. изменен размер контейнера.
        scrollDistance = h / 2
    }

    private val animation = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            scrollableChild?.let {
                val endTarget = 0
                val from = consumedScrollDistance
                val targetTop = from + ((endTarget - from) * interpolatedTime).toInt()
                val offset = (targetTop - it.top)
                ViewCompat.offsetTopAndBottom(it, offset)
                calculateProgress(targetTop, scrollDistance)
            }
        }
    }.apply {
        duration = 150
        interpolator = LinearInterpolator()
        setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                isScrollConsuming = false
            }

            override fun onAnimationStart(animation: Animation?) {
            }
        })
    }

    //    //region NestedScrollingParent
    override fun onStartNestedScroll(child: View, target: View, axes: Int): Boolean {
        return isEnabled
                && !isAnimationRunning()
                && (axes and ViewCompat.SCROLL_AXIS_VERTICAL) != 0
                && canScrollDown()
    }

    private fun canScrollDown(): Boolean {
        return (scrollableChild?.canScrollVertically(1) ?: false)
    }

    private fun isAnimationRunning() = animation.hasStarted() xor animation.hasEnded()

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int) {
        consumedScrollDistance = 0
        firstScrollDirection = UNDEFINED
        nestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes)
    }

    override fun onStopNestedScroll(target: View) {
        nestedScrollingParentHelper.onStopNestedScroll(target)
        animation.cancel()
        animation.reset()

        if (consumedScrollDistance > 0) {
            if (shouldInvokeRefreshCallback()) {
                onRefreshCallback?.invoke()
            }
            scrollableChild?.startAnimation(animation)
        }
    }

    override fun getNestedScrollAxes(): Int {
        return nestedScrollingParentHelper.nestedScrollAxes
    }

    private fun hasChildReachTop(): Boolean {
        return scrollableChild?.let { (it as ScrollingView).computeVerticalScrollOffset() == 0 }
            ?: false
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
        //движение пальца сверху вниз
        if (dy < 0 && consumedScrollDistance <= scrollDistance && hasChildReachTop()) {
            val availableConsumeDistance = scrollDistance - consumedScrollDistance

            //т.к. dy < 0, то по сути выражение эквивалентно availableConsumeDistance - dy
            val couldConsume = (availableConsumeDistance + dy) >= 0
            val consume = if (couldConsume) {
                dy
            } else {
                -availableConsumeDistance
            }

            //добавляем к пройденному пути путь, который будет обработан.
            consumedScrollDistance -= consume

            consumed[1] = consume

            scrollableChild?.let { nonNullView ->
                isScrollConsuming = true
                ViewCompat.offsetTopAndBottom(nonNullView, -consume)
                calculateProgress(consumedScrollDistance, scrollDistance)
            }
        }

        //движение пальца снизу вверх
        if (dy > 0 && consumedScrollDistance > 0) {
            val availableConsumeDistance = consumedScrollDistance
            val couldConsume = availableConsumeDistance - dy >= 0
            val consume = if (couldConsume) {
                dy
            } else {
                availableConsumeDistance
            }
            consumedScrollDistance -= consume

            consumed[1] = dy
            scrollableChild?.let { nonNullView ->
                isScrollConsuming = true
                ViewCompat.offsetTopAndBottom(nonNullView, -consume)
                calculateProgress(consumedScrollDistance, scrollDistance)
            }
        }

        if (firstScrollDirection == UNDEFINED) {
            if (dy > 0) {
                firstScrollDirection = SCROLL_UP
            }

            if (dy < 0) {
                firstScrollDirection = SCROLL_DOWN
            }
        }
    }

    override fun onNestedPreFling(target: View?, velocityX: Float, velocityY: Float): Boolean {
        if (isScrollConsuming) {
            return true
        }

        return super.onNestedPreFling(target, velocityX, velocityY)
    }
//    //endregion

    private fun findScrollableContainer() {
        //ищем первую попавшеюся View, которая поддерживает скролл.
        scrollableChild = children.find { it is ScrollingView }
    }

    private fun calculateProgress(offsetDistance: Int, totalDistance: Int) {
        val progress = offsetDistance.toFloat() / totalDistance
        swipeRefreshProgressCallback?.invoke(progress)
    }

    private fun shouldInvokeRefreshCallback(): Boolean {
        return consumedScrollDistance.toFloat() / scrollDistance >= REFRESH_DETERMINANT_COEFFICIENT
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
