package hibernate.v2.testyourandroid.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.SparseArray
import android.view.MotionEvent
import android.view.View
import com.blankj.utilcode.util.ConvertUtils

class TestMultiTouchView(context: Context?) : View(context) {
    private lateinit var mActivePointers: SparseArray<PointF>
    private lateinit var mPaint: Paint

    private val colors = intArrayOf(
        Color.parseColor("#3F51B5"),
        Color.parseColor("#4CAF50"),
        Color.parseColor("#9C27B0"),
        Color.parseColor("#FF9800"),
        Color.parseColor("#CDDC39"),
        Color.parseColor("#03A9F4"),
        Color.parseColor("#B71C1C"),
        Color.parseColor("#263238"),
        Color.parseColor("#607D8B"),
        Color.parseColor("#FFEB3B")
    )

    private fun initView() {
        mActivePointers = SparseArray()
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        // set painter color to a color you like
        mPaint.color = colors[0]
        mPaint.style = Paint.Style.FILL_AND_STROKE
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        textPaint.textSize = 20f
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean { // get pointer index from the event object
        val pointerIndex = event.actionIndex
        // get pointer ID
        val pointerId = event.getPointerId(pointerIndex)
        // get masked (not specific to a pointer) action
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                // We have a new pointer. Lets add it to the list of pointers
                val f = PointF()
                f.x = event.getX(pointerIndex)
                f.y = event.getY(pointerIndex)
                mActivePointers.put(pointerId, f)
            }
            MotionEvent.ACTION_MOVE -> {
                // a pointer was moved
                val size = event.pointerCount
                var i = 0
                while (i < size) {
                    val point = mActivePointers[event.getPointerId(i)]
                    if (point != null) {
                        point.x = event.getX(i)
                        point.y = event.getY(i)
                    }
                    i++
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP, MotionEvent.ACTION_CANCEL -> {
                mActivePointers.remove(pointerId)
            }
        }
        invalidate()
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // draw all pointers
        val size = mActivePointers.size()
        var i = 0
        while (i < size) {
            val point = mActivePointers.valueAt(i)
            if (point != null) {
                mPaint.color = colors[i % 9]
                canvas.drawCircle(point.x, point.y, ConvertUtils.px2dp(SIZE).toFloat(), mPaint)
            }
            i++
        }
    }

    companion object {
        private const val SIZE = 480f
    }

    init {
        initView()
    }
}