package uk.co.thomasc.wordmaster;

import uk.co.thomasc.wordmaster.util.BaseGameActivity;
import uk.co.thomasc.wordmaster.util.CapsLockLimiter;
import uk.co.thomasc.wordmaster.view.game.GameLayout;
import uk.co.thomasc.wordmaster.view.game.SwipeController;
import uk.co.thomasc.wordmaster.view.game.SwipeListener;
import uk.co.thomasc.wordmaster.view.menu.MenuDetailFragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.EditText;
import android.widget.ImageView;

public class PlayActivity extends BaseGameActivity {
	
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
		
		((GameLayout) findViewById(R.id.screen_game)).setActivity(this);
		
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

	@Override
	public void onSignInFailed() {
		
	}

	@Override
	public void onSignInSucceeded() {
		
	}
	
}