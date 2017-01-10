package hibernate.v2.testyourandroid.ui.custom;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import hibernate.v2.testyourandroid.R;

public class TestDrawView extends View {

	private int mov_x;
	private int mov_y;
	private Paint paint;
	private Canvas canvas;
	private Bitmap bitmap;

	public TestDrawView(Context context) {
		super(context);

		DisplayMetrics displayMetrics = new DisplayMetrics();
		((Activity) getContext()).getWindowManager().getDefaultDisplay()
				.getMetrics(displayMetrics);

		paint = new Paint(Paint.DITHER_FLAG);
		bitmap = Bitmap.createBitmap(displayMetrics.widthPixels,
				displayMetrics.heightPixels, Bitmap.Config.ARGB_8888);
		canvas = new Canvas();
		canvas.setBitmap(bitmap);
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(5);
		paint.setColor(context.getResources().getColor(R.color.primary));
		paint.setAntiAlias(true);

	}

	@Override
	protected void onDraw(Canvas canvas) {
		// super.onDraw(canvas);
		canvas.drawBitmap(bitmap, 0, 0, null);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_MOVE) {
			canvas.drawLine(mov_x, mov_y, event.getX(), event.getY(), paint);
			invalidate();
		}
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			mov_x = (int) event.getX();
			mov_y = (int) event.getY();
			canvas.drawPoint(mov_x, mov_y, paint);
			invalidate();

		}
		mov_x = (int) event.getX();
		mov_y = (int) event.getY();
		return true;
	}

}