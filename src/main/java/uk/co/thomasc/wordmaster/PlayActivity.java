package uk.co.thomasc.wordmaster;

import uk.co.thomasc.wordmaster.util.CapsLockLimiter;
import uk.co.thomasc.wordmaster.view.game.SwipeController;
import uk.co.thomasc.wordmaster.view.game.SwipeListener;
import uk.co.thomasc.wordmaster.view.menu.MenuDetailFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

public class PlayActivity extends FragmentActivity {
	
	private EditText input;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
				
		setContentView(R.layout.game_screen);
		
		overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
		
		if (arg0 == null) {
			Bundle args = new Bundle();
			args.putString(MenuDetailFragment.ARG_ITEM_ID, getIntent().getStringExtra(MenuDetailFragment.ARG_ITEM_ID));
		}
		
		input = (EditText) findViewById(R.id.editText1);
		input.addTextChangedListener(new CapsLockLimiter(input));
		
		((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(input, InputMethodManager.SHOW_FORCED);
		getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		
		SwipeController swipe = new SwipeController(getSupportFragmentManager());
		ViewPager mPager = ((ViewPager) findViewById(R.id.pager));
		mPager.setAdapter(swipe);
		mPager.setOnPageChangeListener(new SwipeListener((ImageView) findViewById(R.id.indicator)));
	}
	
	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.slide_left_2, R.anim.slide_right_2);
	}
	
}