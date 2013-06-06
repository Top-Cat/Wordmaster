package uk.co.thomasc.wordmaster;

import android.content.Context;
import android.graphics.Typeface;

import uk.co.thomasc.wordmaster.util.CapsLockLimiter;
import uk.co.thomasc.wordmaster.view.game.SwipeController;
import uk.co.thomasc.wordmaster.view.game.SwipeListener;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class BaseGame extends FragmentActivity {
	
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
		
		EditText input = (EditText) findViewById(R.id.editText1);
		((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(input, InputMethodManager.SHOW_FORCED);
		getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		input.addTextChangedListener(new CapsLockLimiter(input));
	}
}
