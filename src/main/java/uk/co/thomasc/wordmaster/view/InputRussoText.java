package uk.co.thomasc.wordmaster.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import uk.co.thomasc.wordmaster.R;

public class InputRussoText extends RussoText {

	Paint paint = new Paint();

	public InputRussoText(Context context) {
		super(context);
		setupPaint();
	}

	public InputRussoText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setupPaint();
	}

	public InputRussoText(Context context, AttributeSet attrs) {
		super(context, attrs);
		setupPaint();
	}

	private void setupPaint() {
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(3);
		paint.setColor(getResources().getColor(R.color.footer_text));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (getText().length() == 0) {
			canvas.drawLine(0, getHeight() - 10, getWidth(), getHeight() - 10, paint);
		}
		super.onDraw(canvas);
	}

}
