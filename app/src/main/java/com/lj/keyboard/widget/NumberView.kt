package com.lj.keyboard.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.lj.keyboard.R

class NumberView: View{
    val TAG = "NumberView"
    val DEFAULT_SIZE = 40
    var mContext: Context = context
    var isInputState: Boolean = false
    var isShowRemindLine: Boolean = false
    var mDrawRemindLineState: Boolean = false
    var isDrawText: Boolean = false
    var mPaint: Paint = Paint()
    var mShowPassType: Int = 0
    var mPasswordText: String = ""
    var mInputStateColor: Int = 0
    var mNoInputStateColor: Int = 0
    var mInputTextColor: Int = 0
    var mRemindLineColor: Int = 0
    var mTextSize: Int = 0
    var mBoxLineSize: Int = 0
    constructor(context: Context, attrs : AttributeSet?) : super(context, attrs) {
        Log.d(TAG, "inited!")
    }

    fun drawInputBox(canvas: Canvas?) {
        mPaint.reset()
        if (isInputState) {
            mPaint.color = ContextCompat.getColor(mContext, mInputStateColor)
        } else {
            mPaint.color = ContextCompat.getColor(mContext, mNoInputStateColor)
        }
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = mBoxLineSize.toFloat()
        mPaint.isAntiAlias = true
        val rect = RectF(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat())
        canvas?.drawRect(rect, mPaint)
    }

    fun drawRemindLine(canvas: Canvas?) {
        mPaint.reset()
        if (mDrawRemindLineState && isShowRemindLine) {
            val lineHeight = measuredHeight / 2
            mPaint.style = Paint.Style.FILL
            mPaint.color = ContextCompat.getColor(mContext, mRemindLineColor)
            canvas?.drawLine((measuredWidth / 2).toFloat(),
                    (measuredHeight / 2 - lineHeight / 2).toFloat(),
                    (measuredWidth / 2).toFloat(),
                    (measuredHeight / 2 + lineHeight / 2).toFloat(), mPaint)
        }
    }

    fun drawPassword(canvas: Canvas?) {
        if (isDrawText) {
            mPaint.reset()
            mPaint.color = ContextCompat.getColor(mContext, mInputTextColor)
            mPaint.style = Paint.Style.FILL
            mPaint.isAntiAlias = true
            when (mShowPassType) {
                0  // .
                -> canvas?.drawCircle((measuredWidth / 2).toFloat(), (measuredHeight / 2).toFloat(), (measuredWidth / 4).toFloat(), mPaint)
                1  // *
                -> {
                    mPaint.textSize = (measuredWidth / 2 + 10).toFloat()
                    val strWidth = mPaint.measureText("*")
                    val baseY = measuredHeight / 2 - (mPaint.descent() + mPaint.ascent()) / 2 + strWidth / 3
                    val baseX = measuredWidth / 2 - strWidth / 2
                    canvas?.drawText("*", baseX, baseY, mPaint)
                }
                2  //figure
                -> {
                    mPaint.textSize = mContext.resources.getDimensionPixelOffset(R.dimen.size_figure_pwd_text).toFloat()
                    val strWidth2 = mPaint.measureText(mPasswordText)
                    val baseY2 = measuredHeight / 2 - (mPaint.descent() + mPaint.ascent()) / 2 + strWidth2 / 5
                    val baseX2 = measuredWidth / 2 - strWidth2 / 2
                    canvas?.drawText(mPasswordText, baseX2, baseY2, mPaint)
                }
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        Log.d(TAG, "onDraw!")
        super.onDraw(canvas)
        drawInputBox(canvas)
        drawRemindLine(canvas)
        drawPassword(canvas)
    }

    /**
     * 计算自定义view的宽高尺寸，通过MeasureSpec辅助计算，MeasureSpec是size和mode通过位运算得到的一个整型值，
     * 其中mode有三种值：UNSPECIFIED，EXACTLY，AT_MOST
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = measureSize(widthMeasureSpec)
        val height = measureSize(heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    /**
     * UNSPECIFIED:表示父布局对子view不限制大小
     * EXACTLY:表示父view对子view的大小设置具体的值，一般layout_width和layout_height为match_parent或固定值时，这里的mode为EXACTLY
     * AT_MOST:表示父view对子view的大小限定不能超过某个最大值，一般layout_width和layout_height为wrap_content时，这里的mode为AT_MOST
     */
    fun measureSize(measureSpec: Int) : Int {
        val mode = MeasureSpec.getMode(measureSpec)
        val size = MeasureSpec.getSize(measureSpec)
        when (mode) {
            MeasureSpec.AT_MOST -> return DEFAULT_SIZE
            MeasureSpec.EXACTLY -> return size
            MeasureSpec.UNSPECIFIED -> return DEFAULT_SIZE
            else -> {
                return DEFAULT_SIZE
            }
        }
    }
}