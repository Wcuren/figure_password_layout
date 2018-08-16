package com.lj.keyboard

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.lj.keyboard.widget.NumberLayout
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"
    var mPasswords: MutableList<String> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    private fun initView() {
        number_layout.mFigureChangeListener = object : NumberLayout.FigureChangeListener {
            override fun onAddFigure(figure: String) {
                mPasswords.add(figure)
            }

            override fun onDeleteFigure() {
                mPasswords.removeAt(mPasswords.size - 1)
            }

            override fun onEnter() {
                Log.d(TAG, "password is:" + mPasswords.toString())
            }
        }
    }
}
