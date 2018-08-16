package com.lj.keyboard.widget

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.BaseInputConnection
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import com.lj.keyboard.R
import com.lj.keyboard.util.DensityUtil

class NumberLayout: LinearLayout {

    val TAG = "NumberLayout"
    val DEFAULT_SIZE = 0
    var maxLength: Int = 0
    var mIsShowInputLine: Boolean = false
    var itemWidth: Int = 0
    var itemHeight: Int = 0
    var inputColor: Int = 0
    var noinputColor: Int = 0
    var lineColor: Int = 0
    var txtInputColor: Int = 0
    var interval: Int = 0
    var txtSize: Int = 0
    var boxLineSize: Int = 0
    var drawType: Int = 0
    var showPassType: Int = 0
    var mFigureCursor: Int = -1
    var mFigurePwdViews: MutableList<NumberView> = mutableListOf()

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        Log.d(TAG, "NumberLayout inited!")
        val types = context.obtainStyledAttributes(attrs, R.styleable.NumberLayoutStyle)
        inputColor = types.getResourceId(R.styleable.NumberLayoutStyle_box_input_color, R.color.colorPrimary)
        noinputColor = types.getResourceId(R.styleable.NumberLayoutStyle_box_no_input_color, R.color.text_color_99)
        lineColor = types.getResourceId(R.styleable.NumberLayoutStyle_input_line_color, R.color.text_color_99)
        txtInputColor = types.getResourceId(R.styleable.NumberLayoutStyle_text_input_color, R.color.black)
        drawType= types.getInt(R.styleable.NumberLayoutStyle_box_draw_type, 0)
        interval = types.getInt(R.styleable.NumberLayoutStyle_interval_width, 10)
        maxLength = types.getInt(R.styleable.NumberLayoutStyle_pass_leng, 6)
        itemWidth = types.getInt(R.styleable.NumberLayoutStyle_item_width, 40)
        itemHeight = types.getInt(R.styleable.NumberLayoutStyle_item_height, 40)
        showPassType = types.getInt(R.styleable.NumberLayoutStyle_pass_tips_type, 0)
        txtSize = types.getInt(R.styleable.NumberLayoutStyle_draw_txt_size, 18)
        boxLineSize = types.getInt(R.styleable.NumberLayoutStyle_draw_box_line_size, 4)
        mIsShowInputLine = types.getBoolean(R.styleable.NumberLayoutStyle_is_show_input_line, true)
        types.recycle()
        initView(context)
    }

    fun initView(context: Context) {
        mFigurePwdViews.clear()
        for (i in 0 until maxLength) {
            var view = NumberView(context,null)
            var lp = LayoutParams(DensityUtil.dp2px(context, itemWidth.toFloat()),
                    DensityUtil.dp2px(context, itemHeight.toFloat()))
            if (i != 0) {
                lp.setMarginStart(DensityUtil.dp2px(context, interval.toFloat()))
            }
            view.mInputStateColor = inputColor
            view.mNoInputStateColor = noinputColor
            view.mInputTextColor = txtInputColor
            view.mRemindLineColor = lineColor
            view.mShowPassType = showPassType
            view.mTextSize = txtSize
            view.mBoxLineSize = boxLineSize
            view.isShowRemindLine = mIsShowInputLine
            mFigurePwdViews.add(view)
            addView(view, lp)
        }
        setOnClickListener({layoutClicked()})
        setOnKeyListener(CustomFigureKeyListener())
    }

    /**
     * 点击弹出键盘
     */
    private fun layoutClicked() {
        setFocusable(true)
        setFocusableInTouchMode(true)
        requestFocus()
        val inputMethodManager: InputMethodManager =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }

    /**
     * 这个函数的作用是建立view与输入法的联系
     */
    override fun onCreateInputConnection(outAttrs: EditorInfo?): InputConnection {
        outAttrs?.inputType = InputType.TYPE_CLASS_NUMBER    //这一步是用来限定显示数字键盘
        outAttrs?.imeOptions =  EditorInfo.IME_FLAG_NO_EXTRACT_UI

        return CustomInputConnection(this, true)
    }

    fun delItemPassword() {
        if (mFigureCursor > -1) {
            mFigurePwdViews.get(mFigureCursor).isDrawText = false
            mFigurePwdViews.get(mFigureCursor).invalidate()
            mFigureCursor --
            mFigureChangeListener?.onDeleteFigure()
        }
    }

    fun setItemPassword(password: String) {
        if (mFigureCursor < (mFigurePwdViews.size - 1)) {
            mFigureCursor++
            mFigurePwdViews.get(mFigureCursor).isDrawText = true
            mFigurePwdViews.get(mFigureCursor).mPasswordText = password
            mFigurePwdViews.get(mFigureCursor).invalidate()
            mFigureChangeListener?.onAddFigure(password)
        }
    }

    /**
     * 定义这个内部类的原因是：有的输入法在onkeyListener中无法监听到Delete这个键，所以在发送这个键的地方做监听处理
     */
    inner class CustomInputConnection(targetView: View, fullEditor: Boolean):
            BaseInputConnection(targetView, fullEditor) {
        override fun sendKeyEvent(event: KeyEvent?): Boolean {
            if (event?.action == KeyEvent.ACTION_DOWN &&
                    event?.keyCode == KeyEvent.KEYCODE_DEL) {
                delItemPassword()
                return true
            }
            return super.sendKeyEvent(event)
        }

        override fun deleteSurroundingText(beforeLength: Int, afterLength: Int): Boolean {
            if (mFigureCursor > -1 ){
                return sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
                && sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP,KeyEvent.KEYCODE_DEL))
            } else {
                return super.deleteSurroundingText(beforeLength, afterLength)
            }
        }
    }

    inner class CustomFigureKeyListener(): OnKeyListener {

        override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
            if (event?.action == KeyEvent.ACTION_DOWN) {
                if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) {
                    setItemPassword((keyCode - 7).toString())
                    return true
                } else if (keyCode == KeyEvent.KEYCODE_DEL) {
                    delItemPassword()
                    return true
                } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    mFigureChangeListener?.onEnter()
                    return true
                }
            }
            return false
        }
    }

    var mFigureChangeListener: FigureChangeListener ?= null

    interface FigureChangeListener {
        fun onAddFigure(figure: String)
        fun onDeleteFigure()
        fun onEnter()
    }
}