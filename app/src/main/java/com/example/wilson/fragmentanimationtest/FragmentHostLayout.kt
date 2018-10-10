package com.example.wilson.fragmentanimationtest

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Canvas
import android.os.Build
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import java.lang.ref.WeakReference

class FragmentHostLayout : FrameLayout {
    private val drawingOpPool = mutableListOf<DrawingOp>()
    private val drawingOps = mutableListOf<DrawingOp>()

    private var currentFragment: WeakReference<Fragment>? = null

    private var transitionStarted = false

    private val fragmentCallback = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentAttached(fm: FragmentManager?, f: Fragment?, context: Context?) {
            updateCurrentFragment()
        }

        override fun onFragmentDetached(fm: FragmentManager?, f: Fragment?) {
            updateCurrentFragment()
        }
    }

    @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr)

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : super(context, attrs, defStyleAttr, defStyleRes)

    fun updateCurrentFragment() {
        val fragment = getSupportFragmentManager()?.fragments?.firstOrNull { it.id == id && it.isVisible && it.isAdded }
        currentFragment = if (fragment == null) {
            null
        } else {
            WeakReference(fragment)
        }
    }

    private fun getSupportFragmentManager(): FragmentManager? {
        fun getActivity(context: Context): AppCompatActivity? {
            return when (context) {
                is Activity -> context as? AppCompatActivity
                is ContextWrapper -> getActivity(context.baseContext)
                else -> null
            }
        }

        return getActivity(context)?.supportFragmentManager
    }

    override fun onAttachedToWindow() {
        getSupportFragmentManager()?.registerFragmentLifecycleCallbacks(fragmentCallback, false)
        super.onAttachedToWindow()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        getSupportFragmentManager()?.unregisterFragmentLifecycleCallbacks(fragmentCallback)
    }

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)

        val curFragmentView = currentFragment?.get()?.view

        //In normal condition when transition animation ended, endViewTransition() is called and animation of fragment view set null.
        //But sometimes, animation is set to null but endViewTransition() is not called.
        //So when transition is not stared or transition started && animation is null , view of current fragment should be drawn last.
        if (drawingOps.size > 1 && (transitionStarted && curFragmentView?.animation == null || !transitionStarted)) {
            val curFragOp = drawingOps.find { it.child == curFragmentView }
            if (curFragOp != null) {
                drawingOps.remove(curFragOp)
                drawingOps.add(curFragOp)
                drawingOps.forEach {
                    endViewTransition(it.child)
                }
            }
        }

        drawingOps.forEach {
            it.draw()
            drawingOpPool.add(it)
        }
        drawingOps.clear()
    }

    override fun startViewTransition(view: View?) {
        super.startViewTransition(view)
        transitionStarted = true
    }

    override fun endViewTransition(view: View?) {
        super.endViewTransition(view)
        transitionStarted = false
    }

    override fun drawChild(canvas: Canvas?, child: View?, drawingTime: Long): Boolean {
        if (canvas != null && child != null) {
            drawingOps.add(obtainDrawingOp().set(canvas, child, drawingTime))
        }
        return true
    }

    private fun obtainDrawingOp(): DrawingOp {
        return if (drawingOpPool.isEmpty()) {
            DrawingOp()
        } else drawingOpPool.removeAt(drawingOpPool.size - 1)
    }

    private fun performDraw(op: FragmentHostLayout.DrawingOp) {
        super.drawChild(op.canvas, op.child, op.drawingTime);
    }

    inner class DrawingOp {
        var canvas: Canvas? = null
        var child: View? = null
        var drawingTime: Long = 0

        fun set(canvas: Canvas, child: View, drawingTime: Long): DrawingOp {
            this.canvas = canvas
            this.child = child
            this.drawingTime = drawingTime
            return this
        }

        fun draw() {
            performDraw(this)
            canvas = null
            child = null
            drawingTime = 0
        }
    }
}


