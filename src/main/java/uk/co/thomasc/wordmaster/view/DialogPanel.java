package uk.co.thomasc.wordmaster.view;

import uk.co.thomasc.wordmaster.R;
import uk.co.thomasc.wordmaster.util.TimerHandler;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

public class DialogPanel extends FrameLayout {

	public View container;
	private TimerHandler h;
	private final int displayTime = 5000;
	
	public DialogPanel(Context context) {
		super(context);
		init();
	}
	
	public DialogPanel(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public DialogPanel(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	public void init() {
		h = new TimerHandler(this);
		
		inflate(getContext(), R.layout.network_error, this);
		container = findViewById(R.id.network_error);
		container.setVisibility(View.GONE);
	}
	
	public void show(Errors error) {
		if (container.getVisibility() == View.GONE) {
			container.setVisibility(View.VISIBLE);
			Animation showAnim = AnimationUtils.loadAnimation(getContext(), R.anim.slide_down);
			showAnim.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {}
				@Override
				public void onAnimationRepeat(Animation animation) {}
				@Override
				public void onAnimationEnd(Animation animation) {
					h.sendEmptyMessageDelayed(getId(), displayTime); // Time to show dialog for
				}
			});
			container.startAnimation(showAnim);
		} else {
			h.removeMessages(getId());
			h.sendEmptyMessageDelayed(getId(), displayTime);
		}
		((TextView) findViewById(R.id.errortxt)).setText(error.getTitle());
		((TextView) findViewById(R.id.errorhelp)).setText(error.getSubtitle());
	}
	
	public void setTitle(String title) {
		((TextView) findViewById(R.id.errortxt)).setText(title);
	}
	
	public void setSubtitle(String subtitle) {
		((TextView) findViewById(R.id.errorhelp)).setText(subtitle);
	}
	
}
