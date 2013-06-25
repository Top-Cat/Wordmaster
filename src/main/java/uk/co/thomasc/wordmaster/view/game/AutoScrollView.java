package uk.co.thomasc.wordmaster.view.game;

import android.content.Context;
import android.util.AttributeSet;

public class AutoScrollView extends PullToRefreshListView {

	public AutoScrollView(Context context) {
		super(context);
	}
	
	public AutoScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public AutoScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh) {
		post(new Runnable() {
			@Override
			public void run() {
				setSelection(getAdapter().getCount() - 1);
			}
		});
	}

}
