package com.example.simplescroll.view_overlay

import android.content.Context
import android.util.AttributeSet
import android.view.View

class SimpleCustomView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), ViewOverlay by ViewOverlayDelegate(context){
}