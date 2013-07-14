package uk.co.thomasc.wordmaster.view.game;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.RelativeLayout;

public class GameLayout extends RelativeLayout {

	private Activity playActivity;

	public GameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public GameLayout(Context context) {
		super(context);
	}

	public void setActivity(Activity playActivity) {
		this.playActivity = playActivity;
	}

	@Override
	public boolean dispatchKeyEventPreIme(KeyEvent event) {
		if (playActivity != null && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			KeyEvent.DispatcherState state = getKeyDispatcherState();
			if (state != null) {
				if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
					state.startTracking(event, this);
					return true;
				} else if (event.getAction() == KeyEvent.ACTION_UP && !event.isCanceled() && state.isTracking(event)) {
					playActivity.onBackPressed();
					return true;
				}
			}
		}
		return super.dispatchKeyEventPreIme(event);
	}

}
