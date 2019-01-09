package hibernate.v2.testyourandroid.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

import hibernate.v2.testyourandroid.R;

/**
 * https://blog.csdn.net/canney_chen/article/details/54693563
 */
public class LevelView extends View {

	private float mLimitRadius = 0;
	private float mBubbleRadius;
	private int mLimitColor;
	private float mLimitCircleWidth;
	private int mBubbleRuleColor;
	private float mBubbleRuleWidth;
	private float mBubbleRuleRadius;
	private int mHorizontalColor;
	private int mBubbleColor;

	private Paint mBubblePaint;
	private Paint mLimitPaint;
	private Paint mBubbleRulePaint;

	private PointF centerPnt = new PointF();

	private PointF bubblePoint;
	private double pitchAngle = -90;
	private double rollAngle = -90;

	public LevelView(Context context) {
		super(context);
		init(null, 0);
	}

	public LevelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs, 0);
	}

	public LevelView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs, defStyle);
	}

	private void init(AttributeSet attrs, int defStyle) {
		// Load attributes
		final TypedArray a = getContext().obtainStyledAttributes(
				attrs, R.styleable.LevelView, defStyle, 0);

		mBubbleRuleColor = a.getColor(R.styleable.LevelView_bubbleRuleColor, mBubbleRuleColor);

		mBubbleColor = a.getColor(R.styleable.LevelView_bubbleColor, mBubbleColor);
		mLimitColor = a.getColor(R.styleable.LevelView_limitColor, mLimitColor);

		mHorizontalColor = a.getColor(R.styleable.LevelView_horizontalColor, mHorizontalColor);

		mLimitRadius = a.getDimension(R.styleable.LevelView_limitRadius, mLimitRadius);
		mBubbleRadius = a.getDimension(R.styleable.LevelView_bubbleRadius, mBubbleRadius);
		mLimitCircleWidth = a.getDimension(R.styleable.LevelView_limitCircleWidth, mLimitCircleWidth);

		mBubbleRuleWidth = a.getDimension(R.styleable.LevelView_bubbleRuleWidth, mBubbleRuleWidth);

		mBubbleRuleRadius = a.getDimension(R.styleable.LevelView_bubbleRuleRadius, mBubbleRuleRadius);

		a.recycle();

		mBubblePaint = new Paint();

		mBubblePaint.setColor(mBubbleColor);
		mBubblePaint.setStyle(Paint.Style.FILL);
		mBubblePaint.setAntiAlias(true);

		mLimitPaint = new Paint();

		mLimitPaint.setStyle(Paint.Style.STROKE);
		mLimitPaint.setColor(mLimitColor);
		mLimitPaint.setStrokeWidth(mLimitCircleWidth);
		mLimitPaint.setAntiAlias(true);

		mBubbleRulePaint = new Paint();
		mBubbleRulePaint.setColor(mBubbleRuleColor);
		mBubbleRulePaint.setStyle(Paint.Style.STROKE);
		mBubbleRulePaint.setStrokeWidth(mBubbleRuleWidth);
		mBubbleRulePaint.setAntiAlias(true);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		calculateCenter(widthMeasureSpec, heightMeasureSpec);
	}

	private void calculateCenter(int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.makeMeasureSpec(widthMeasureSpec, MeasureSpec.UNSPECIFIED);

		int height = MeasureSpec.makeMeasureSpec(heightMeasureSpec, MeasureSpec.UNSPECIFIED);

		int center = Math.min(width, height) / 2;

		centerPnt.set(center, center);
	}


	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		boolean isCenter = isCenter(bubblePoint);
		int limitCircleColor = isCenter ? mHorizontalColor : mLimitColor;
		int bubbleColor = isCenter ? mHorizontalColor : mBubbleColor;

		mBubblePaint.setColor(bubbleColor);
		mLimitPaint.setColor(limitCircleColor);

		canvas.drawCircle(centerPnt.x, centerPnt.y, mBubbleRuleRadius, mBubbleRulePaint);
		canvas.drawCircle(centerPnt.x, centerPnt.y, mLimitRadius, mLimitPaint);

		drawBubble(canvas);

	}

	private boolean isCenter(PointF bubblePoint) {
		if (bubblePoint == null) {
			return false;
		}

		return Math.abs(bubblePoint.x - centerPnt.x) < 3 && Math.abs(bubblePoint.y - centerPnt.y) < 3;
	}

	private void drawBubble(Canvas canvas) {
		if (bubblePoint != null) {
			canvas.drawCircle(bubblePoint.x, bubblePoint.y, mBubbleRadius, mBubblePaint);
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
	private PointF convertCoordinate(double rollAngle, double pitchAngle, double radius) {
		double scale = radius / Math.toRadians(90);

		double x0 = -(rollAngle * scale);
		double y0 = -(pitchAngle * scale);

		double x = centerPnt.x - x0;
		double y = centerPnt.y - y0;

		return new PointF((float) x, (float) y);
	}

	/**
	 * @param pitchAngle double
	 * @param rollAngle  double
	 */
	public void setAngle(double rollAngle, double pitchAngle) {

		this.pitchAngle = pitchAngle;
		this.rollAngle = rollAngle;

		float limitRadius = mLimitRadius - mBubbleRadius;

		bubblePoint = convertCoordinate(rollAngle, pitchAngle, mLimitRadius);

		if (outLimit(bubblePoint, limitRadius)) {
			onCirclePoint(bubblePoint, limitRadius);
		}

		invalidate();
	}

	/**
	 * @param bubblePnt   PointF
	 * @param limitRadius float
	 * @return boolean
	 */
	private boolean outLimit(PointF bubblePnt, float limitRadius) {
		float cSqrt = (bubblePnt.x - centerPnt.x) * (bubblePnt.x - centerPnt.x)
				+ (centerPnt.y - bubblePnt.y) * +(centerPnt.y - bubblePnt.y);

		return cSqrt - limitRadius * limitRadius > 0;
	}

	/**
	 * @param bubblePnt   PointF
	 * @param limitRadius double
	 * @return PointF
	 */
	private PointF onCirclePoint(PointF bubblePnt, double limitRadius) {
		double azimuth = Math.atan2((bubblePnt.y - centerPnt.y), (bubblePnt.x - centerPnt.x));
		azimuth = azimuth < 0 ? 2 * Math.PI + azimuth : azimuth;

		double x1 = centerPnt.x + limitRadius * Math.cos(azimuth);
		double y1 = centerPnt.y + limitRadius * Math.sin(azimuth);

		bubblePnt.set((float) x1, (float) y1);

		return bubblePnt;
	}

	public double getPitchAngle() {
		return this.pitchAngle;
	}

	public double getRollAngle() {
		return this.rollAngle;
	}
}
