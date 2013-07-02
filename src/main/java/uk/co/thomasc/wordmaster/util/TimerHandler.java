package uk.co.thomasc.wordmaster.util;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;

import uk.co.thomasc.wordmaster.R.anim;
import uk.co.thomasc.wordmaster.view.DialogPanel;

public class TimerHandler extends Handler {
	private DialogPanel dialogPanel;

	public TimerHandler(DialogPanel dialogPanel) {
		this.dialogPanel = dialogPanel;
	}

	@Override
	public void handleMessage(Message msg) {
		System.out.println(msg.what + ", " + dialogPanel.getId());
		if (dialogPanel.container.getVisibility() == View.VISIBLE) {
			Animation removeAnim = AnimationUtils.loadAnimation(dialogPanel.getContext(), anim.slide_up);
			removeAnim.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationEnd(Animation animation) {
					dialogPanel.container.setVisibility(View.GONE);
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationStart(Animation animation) {
				}
			});
			dialogPanel.container.startAnimation(removeAnim);
		}
	}
}
