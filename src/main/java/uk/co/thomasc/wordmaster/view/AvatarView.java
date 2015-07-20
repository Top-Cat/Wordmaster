package uk.co.thomasc.wordmaster.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import uk.co.thomasc.wordmaster.objects.User;
import uk.co.thomasc.wordmaster.objects.callbacks.UserListener;

public class AvatarView extends ImageView implements UserListener {

	private User user = User.none;

	public AvatarView(Context context) {
		super(context);
	}

	public AvatarView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public AvatarView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setUser(User user) {
		this.user.removeListener(this);
		this.user = user;

		onImageLoaded(user);

		user.addListener(this);
	}

	@Override
	public void onImageLoaded(final User user) {
		post(new Runnable() {
			@Override
			public void run() {
				Drawable d = user.getAvatar();
				if (d != null) {
					AvatarView.this.setImageDrawable(d);
				}
			}
		});
	}

	@Override
	public void onNameLoaded(User user) {

	}

}
