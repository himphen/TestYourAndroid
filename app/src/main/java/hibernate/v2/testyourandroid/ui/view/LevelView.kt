package hibernate.v2.testyourandroid.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View
import hibernate.v2.testyourandroid.R
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * https://blog.csdn.net/canney_chen/article/details/54693563
 */
class LevelView : View {
    private var mLimitRadius = 0f
    private var mBubbleRadius = 0f
    private var mLimitColor = 0
    private var mLimitCircleWidth = 0f
    private var mBubbleRuleColor = 0
    private var mBubbleRuleWidth = 0f
    private var mBubbleRuleRadius = 0f
    private var mHorizontalColor = 0
    private var mBubbleColor = 0
    private lateinit var mBubblePaint: Paint
    private lateinit var mLimitPaint: Paint
    private lateinit var mBubbleRulePaint: Paint
    private val centerPnt = PointF()
    private var bubblePoint: PointF? = null
    private var pitchAngle = -90.0
    private var rollAngle = -90.0

    constructor(context: Context?) : super(context) {
        init(null, 0)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) { // Load attributes
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.LevelView, defStyle, 0
        )
        mBubbleRuleColor = a.getColor(R.styleable.LevelView_bubbleRuleColor, mBubbleRuleColor)
        mBubbleColor = a.getColor(R.styleable.LevelView_bubbleColor, mBubbleColor)
        mLimitColor = a.getColor(R.styleable.LevelView_limitColor, mLimitColor)
        mHorizontalColor = a.getColor(R.styleable.LevelView_horizontalColor, mHorizontalColor)
        mLimitRadius = a.getDimension(R.styleable.LevelView_limitRadius, mLimitRadius)
        mBubbleRadius = a.getDimension(R.styleable.LevelView_bubbleRadius, mBubbleRadius)
        mLimitCircleWidth =
            a.getDimension(R.styleable.LevelView_limitCircleWidth, mLimitCircleWidth)
        mBubbleRuleWidth = a.getDimension(R.styleable.LevelView_bubbleRuleWidth, mBubbleRuleWidth)
        mBubbleRuleRadius =
            a.getDimension(R.styleable.LevelView_bubbleRuleRadius, mBubbleRuleRadius)
        a.recycle()
        mBubblePaint = Paint()
        mBubblePaint.color = mBubbleColor
        mBubblePaint.style = Paint.Style.FILL
        mBubblePaint.isAntiAlias = true
        mLimitPaint = Paint()
        mLimitPaint.style = Paint.Style.STROKE
        mLimitPaint.color = mLimitColor
        mLimitPaint.strokeWidth = mLimitCircleWidth
        mLimitPaint.isAntiAlias = true
        mBubbleRulePaint = Paint()
        mBubbleRulePaint.color = mBubbleRuleColor
        mBubbleRulePaint.style = Paint.Style.STROKE
        mBubbleRulePaint.strokeWidth = mBubbleRuleWidth
        mBubbleRulePaint.isAntiAlias = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        calculateCenter(widthMeasureSpec, heightMeasureSpec)
    }

    private fun calculateCenter(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.makeMeasureSpec(widthMeasureSpec, MeasureSpec.UNSPECIFIED)
        val height = MeasureSpec.makeMeasureSpec(heightMeasureSpec, MeasureSpec.UNSPECIFIED)
        val center = width.coerceAtMost(height) / 2
        centerPnt[center.toFloat()] = center.toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val isCenter = isCenter(bubblePoint)
        val limitCircleColor = if (isCenter) mHorizontalColor else mLimitColor
        val bubbleColor = if (isCenter) mHorizontalColor else mBubbleColor
        mBubblePaint.color = bubbleColor
        mLimitPaint.color = limitCircleColor
        canvas.drawCircle(centerPnt.x, centerPnt.y, mBubbleRuleRadius, mBubbleRulePaint)
        canvas.drawCircle(centerPnt.x, centerPnt.y, mLimitRadius, mLimitPaint)
        drawBubble(canvas)
    }

    private fun isCenter(bubblePoint: PointF?): Boolean {
        return if (bubblePoint == null) {
            false
        } else abs(bubblePoint.x - centerPnt.x) < 3 && abs(bubblePoint.y - centerPnt.y) < 3
    }

    private fun drawBubble(canvas: Canvas) {
        bubblePoint?.let { bubblePoint ->
            canvas.drawCircle(bubblePoint.x, bubblePoint.y, mBubbleRadius, mBubblePaint)
        }
    }

    /**
     * Convert angle to screen coordinate point.
     *
     * @param rollAngle  double
     * @param pitchAngle double
     * @param radius     double
     * @return PointF
     */
    private fun convertCoordinate(rollAngle: Double, pitchAngle: Double, radius: Double): PointF {
        val scale = radius / Math.toRadians(90.0)
        val x0 = -(rollAngle * scale)
        val y0 = -(pitchAngle * scale)
        val x = centerPnt.x - x0
        val y = centerPnt.y - y0
        return PointF(x.toFloat(), y.toFloat())
    }

    /**
     * @param pitchAngle double
     * @param rollAngle  double
     */
    fun setAngle(rollAngle: Double, pitchAngle: Double) {
        this.pitchAngle = pitchAngle
        this.rollAngle = rollAngle
        val limitRadius = mLimitRadius - mBubbleRadius
        bubblePoint = convertCoordinate(rollAngle, pitchAngle, mLimitRadius.toDouble())
        if (outLimit(bubblePoint, limitRadius)) {
            onCirclePoint(bubblePoint, limitRadius.toDouble())
        }
        invalidate()
    }

    /**
     * @param bubblePnt   PointF
     * @param limitRadius float
     * @return boolean
     */
    private fun outLimit(bubblePnt: PointF?, limitRadius: Float): Boolean {
        bubblePnt?.let {
            val cSqrt = ((bubblePnt.x - centerPnt.x) * (bubblePnt.x - centerPnt.x)
                    + (centerPnt.y - bubblePnt.y) * +(centerPnt.y - bubblePnt.y))
            return cSqrt - limitRadius * limitRadius > 0
        }

        return false
    }

    /**
     * @param bubblePnt   PointF
     * @param limitRadius double
     * @return PointF
     */
    private fun onCirclePoint(bubblePnt: PointF?, limitRadius: Double): PointF? {
        bubblePnt?.let {
            var azimuth = atan2(
                (bubblePnt.y - centerPnt.y).toDouble(),
                (bubblePnt.x - centerPnt.x).toDouble()
            )
            azimuth = if (azimuth < 0) 2 * Math.PI + azimuth else azimuth
            val x1 = centerPnt.x + limitRadius * cos(azimuth)
            val y1 = centerPnt.y + limitRadius * sin(azimuth)
            bubblePnt[x1.toFloat()] = y1.toFloat()
        }
        return bubblePnt
    }

}