package hibernate.v2.testyourandroid.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

import com.blankj.utilcode.util.ConvertUtils;

public class TestMultiTouchView extends View {

	private static final float SIZE = 480f;

	private SparseArray<PointF> mActivePointers;
	private Paint mPaint;
	private int[] colors = {Color.parseColor("#3F51B5"), Color.parseColor("#4CAF50"), Color.parseColor("#9C27B0"),
			Color.parseColor("#FF9800"), Color.parseColor("#CDDC39"), Color.parseColor("#03A9F4"), Color.parseColor("#B71C1C"), Color.parseColor("#263238"),
			Color.parseColor("#607D8B"), Color.parseColor("#FFEB3B")};


	public TestMultiTouchView(Context context) {
		super(context);
		initView();
	}

	private void initView() {
		mActivePointers = new SparseArray<>();
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		// set painter color to a color you like
		mPaint.setColor(colors[0]);
		mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		textPaint.setTextSize(20);
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// get pointer index from the event object
		int pointerIndex = event.getActionIndex();

		// get pointer ID
		int pointerId = event.getPointerId(pointerIndex);

		// get masked (not specific to a pointer) action
		int maskedAction = event.getActionMasked();

		switch (maskedAction) {

			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_POINTER_DOWN: {
				// We have a new pointer. Lets add it to the list of pointers

				PointF f = new PointF();
				f.x = event.getX(pointerIndex);
				f.y = event.getY(pointerIndex);
				mActivePointers.put(pointerId, f);
				break;
			}
			case MotionEvent.ACTION_MOVE: { // a pointer was moved
				for (int size = event.getPointerCount(), i = 0; i < size; i++) {
					PointF point = mActivePointers.get(event.getPointerId(i));
					if (point != null) {
						point.x = event.getX(i);
						point.y = event.getY(i);
					}
				}
				break;
			}
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_POINTER_UP:
			case MotionEvent.ACTION_CANCEL: {
				mActivePointers.remove(pointerId);
				break;
			}
		}
		invalidate();

		return true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// draw all pointers
		for (int size = mActivePointers.size(), i = 0; i < size; i++) {
			PointF point = mActivePointers.valueAt(i);
			if (point != null) {
				mPaint.setColor(colors[i % 9]);
				canvas.drawCircle(point.x, point.y, ConvertUtils.px2dp(SIZE), mPaint);
			}
		}
	}

}