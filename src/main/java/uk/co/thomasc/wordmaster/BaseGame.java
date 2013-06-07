package uk.co.thomasc.wordmaster;

import android.content.Context;
import android.graphics.Typeface;

import com.google.android.gms.common.SignInButton;

import uk.co.thomasc.wordmaster.util.CapsLockLimiter;
import uk.co.thomasc.wordmaster.view.game.SwipeController;
import uk.co.thomasc.wordmaster.view.game.SwipeListener;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class BaseGame extends BaseGameActivity implements OnClickListener {
	
	public static Typeface russo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		russo = Typeface.createFromAsset(getAssets(), "fonts/Russo_One.ttf");

		setContentView(R.layout.activity_fullscreen);
		
		SwipeController swipe = new SwipeController(getSupportFragmentManager());
		ViewPager mPager = ((ViewPager) findViewById(R.id.pager));
		mPager.setAdapter(swipe);
		mPager.setOnPageChangeListener(new SwipeListener((ImageView) findViewById(R.id.indicator)));
		
		SignInButton button = (SignInButton) findViewById(R.id.button_sign_in);
		button.setOnClickListener(this);
		button.setStyle(SignInButton.SIZE_WIDE, SignInButton.COLOR_LIGHT); // I commend anyone who can do this in XML
		
		EditText input = (EditText) findViewById(R.id.editText1);
		((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(input, InputMethodManager.SHOW_FORCED);
		getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		input.addTextChangedListener(new CapsLockLimiter(input));
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.button_sign_in) {
			System.out.println("click");
			beginUserInitiatedSignIn();
		}
	}
	
	@Override
	public void onSignInFailed() {
		System.out.println("oh noes!");
	}

	@Override
	public void onSignInSucceeded() {
		((LinearLayout) findViewById(R.id.screen_login)).setVisibility(View.GONE);
		((LinearLayout) findViewById(R.id.screen_game)).setVisibility(View.VISIBLE);
	}
}
