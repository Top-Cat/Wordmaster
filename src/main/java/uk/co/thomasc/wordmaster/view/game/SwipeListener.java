package uk.co.thomasc.wordmaster.view.game;

import uk.co.thomasc.wordmaster.R;

import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.ImageView;

public class SwipeListener implements OnPageChangeListener {

	private ImageView indicator;
	
	public SwipeListener(ImageView indicator) {
		this.indicator = indicator;
	}
	
	@Override
	public void onPageScrollStateChanged(int arg0) {
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		
	}

	@Override
	public void onPageSelected(int arg0) {
		if (arg0 == 0) {
			indicator.setImageResource(R.drawable.page_note_left);
		} else {
			indicator.setImageResource(R.drawable.page_note_right);
		}
	}
	
}