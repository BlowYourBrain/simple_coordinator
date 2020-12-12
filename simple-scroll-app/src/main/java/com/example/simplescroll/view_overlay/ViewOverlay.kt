package com.example.simplescroll.view_overlay

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import androidx.core.content.ContextCompat

interface ViewOverlay {

    var isDebug: Boolean

    fun onDraw(canvas: Canvas)

}

class ViewOverlayDelegate(private val context: Context) : ViewOverlay {

    override var isDebug: Boolean = false

    private val redPaint = Paint().apply {
        color = ContextCompat.getColor(context, android.R.color.holo_red_dark)
        style = Paint.Style.STROKE
        strokeWidth = 1.toDp(context)
    }
    private val bluePaint = Paint().apply {
        color = ContextCompat.getColor(context, android.R.color.holo_blue_dark)
        strokeWidth = 2.toDp(context)
    }
    private val cornerLength = 6.toDp(context)

    override fun onDraw(canvas: Canvas) {
        if (isDebug){
            drawRedRectangle(canvas)
            drawBlueCorners(canvas)
        }
    }

    private fun drawRedRectangle(canvas: Canvas) {
        canvas.drawRect(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat(), redPaint)
    }

    private fun drawBlueCorners(canvas: Canvas) {
        val width = canvas.width.toFloat()
        val height = canvas.height.toFloat()
        //верхний левый угол
        canvas.drawLine(0f, 0f, cornerLength, 0f, bluePaint)
        canvas.drawLine(0f, 0f, 0f, cornerLength, bluePaint)

        //верхний правый угол
        canvas.drawLine(width - cornerLength, 0f, width, 0f, bluePaint)
        canvas.drawLine(width, 0f, width, cornerLength, bluePaint)

        //нижний левый угол
        canvas.drawLine(0f, height, 0f, height - cornerLength, bluePaint)
        canvas.drawLine(0f, height, cornerLength, height, bluePaint)

        //нижний правый угол
        canvas.drawLine(width, height, width - cornerLength, height, bluePaint)
        canvas.drawLine(width, height, width, height - cornerLength, bluePaint)
    }

}