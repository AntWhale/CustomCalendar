package com.boo.sample.widget

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.boo.sample.mypractice.R

class PageIndicatorView @JvmOverloads constructor(context : Context, attrs : AttributeSet? = null)
    : View(context, attrs) {
    private lateinit var typedArray : TypedArray
    private var count : Int
    private val paint = Paint()

        init {
            typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.PageIndicatorView)
            count = typedArray.getInt(R.styleable.PageIndicatorView_piv_count, 0)
            typedArray.recycle()
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        /*val width = when{
            widthMode == MeasureSpec.EXACTLY -> widthSize
            widthMode == MeasureSpec.AT_MOST -> Math.min(desiredWidth, widthSize)
            else -> desiredWidth
        }*/

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val x = width / 2
        val y = height / 2

        paint.setColor(Color.parseColor("#ffffff"))
        canvas?.drawCircle(x.toFloat(), y.toFloat(), y.toFloat(), paint)

    }
}