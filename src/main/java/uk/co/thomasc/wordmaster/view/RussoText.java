package uk.co.thomasc.wordmaster.view;

import lombok.Getter;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;
import uk.co.thomasc.wordmaster.BaseGame;
import uk.co.thomasc.wordmaster.R;

public class RussoText extends TextView {

	@Getter private boolean strike;
	private Paint paint = new Paint();

	public RussoText(Context context) {
		super(context);
		this.setTypeface(BaseGame.russo);
	}

	public RussoText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.setTypeface(BaseGame.russo);
	}

	public RussoText(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setTypeface(BaseGame.russo);
	}

	public void setStrike(boolean strike) {
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(5);
		paint.setColor(getResources().getColor(R.color.strike));
		this.strike = strike;
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (strike) {
			canvas.drawLine(0, getHeight() - 10, getWidth(), 10, paint);
		}
	}

}
