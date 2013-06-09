package uk.co.thomasc.wordmaster;

import android.content.Context;
import android.graphics.Typeface;

import com.google.android.gms.common.SignInButton;

import uk.co.thomasc.wordmaster.objects.Game;
import uk.co.thomasc.wordmaster.objects.User;
import uk.co.thomasc.wordmaster.util.BaseGameActivity;
import uk.co.thomasc.wordmaster.util.CapsLockLimiter;
import uk.co.thomasc.wordmaster.view.game.SwipeController;
import uk.co.thomasc.wordmaster.view.game.SwipeListener;
import uk.co.thomasc.wordmaster.view.menu.MenuAdapter;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class BaseGame extends BaseGameActivity implements OnClickListener {
	
	public static Typeface russo;
	private EditText input;

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
		button.setSize(SignInButton.SIZE_WIDE); // I commend anyone who can do this in XML
		
		MenuAdapter adapter = new MenuAdapter(this);
		// TODO: Delete examples
		adapter.add(new Game("123", new User("123", "Josh", Uri.EMPTY), new User("124", "Adam", Uri.EMPTY)));
		adapter.add(new Game("123", new User("123", "Josh", Uri.EMPTY), new User("124", "Adam", Uri.EMPTY)));
		adapter.add(new Game("123", new User("123", "Josh", Uri.EMPTY), new User("124", "Adam", Uri.EMPTY)));
		((ListView) findViewById(R.id.main_feed)).setAdapter(adapter);
		
		input = (EditText) findViewById(R.id.editText1);
		input.addTextChangedListener(new CapsLockLimiter(input));
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.button_sign_in) {
			//beginUserInitiatedSignIn(); For now we skip this
			onSignInSucceeded();
		}
	}
	
	@Override
	public void onSignInFailed() {
		System.out.println("oh noes!");
	}
	
	@Override
	public void onSignInSucceeded() {
		findViewById(R.id.button_sign_in).setVisibility(View.GONE);
		findViewById(R.id.whysignin).setVisibility(View.GONE);
		findViewById(R.id.main_feed).setVisibility(View.VISIBLE);
		
		//TODO: Populate menu feed
		
		//signOut();
	}
	
	public String getUserId() {
		//return getGamesClient().getCurrentPlayer().getPlayerId();
		return "1";
	}
	
	private void showGameScreen() {
		((LinearLayout) findViewById(R.id.screen_menu)).setVisibility(View.GONE);
		((LinearLayout) findViewById(R.id.screen_game)).setVisibility(View.VISIBLE);
		
		((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(input, InputMethodManager.SHOW_FORCED);
		getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
	}
}
