package uk.co.thomasc.wordmaster;

import android.graphics.Canvas;
import android.graphics.Paint;

public class GameRow extends Drawable {

	int x = 0;
	int y = 0;

	int xd = 1;
	int yd = 1;

    @Override
    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setARGB(255, 255, 255, 255);
        canvas.drawCircle(x += xd, y += yd, 5, paint);

		if (x > canvas.getWidth() || x < 0) {
			xd *= -1;
		}
		if (y > canvas.getHeight() || y < 0) {
			yd *= -1;
		}
    }

}