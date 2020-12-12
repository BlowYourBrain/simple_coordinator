package com.example.simplescroll.view_overlay

import android.content.Context

fun Int.toDp(context: Context): Float = context.resources.displayMetrics.density * this