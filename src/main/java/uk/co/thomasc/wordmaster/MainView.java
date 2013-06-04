package uk.co.thomasc.wordmaster;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

public class MainView extends SurfaceView implements SurfaceHolder.Callback {

	List<Drawable> onscreen = new ArrayList<Drawable>();

	public MainView(Context context) {
		super(context);
		init();
	}

	public MainView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public MainView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		getHolder().addCallback(this);
		new DrawingThread().start();
		onscreen.add(new GameRow());
	}

	@Override
	public void surfaceCreated(SurfaceHolder surfaceHolder) {
		System.out.println("created");
	}

	@Override
	public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
		System.out.println("changed");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
		System.out.println("destroyed");
	}

	private class DrawingThread extends Thread {

		private byte frameTarget = 30;

		@Override
		public void run() {
			while (true) {
				long frametime = System.currentTimeMillis();
				Canvas c = getHolder().lockCanvas();
				if (c != null) {
					c.drawColor(0xFF4A4AD3);
					for (Drawable draw : onscreen) {
						draw.draw(c);
					}
					getHolder().unlockCanvasAndPost(c);
				}
				frametime = System.currentTimeMillis() - frametime;
				int waitfor = (int) ((1000 / frameTarget) - frametime);
				if (waitfor > 0) {
					try {
						Thread.sleep(waitfor);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
