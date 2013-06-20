package uk.co.thomasc.wordmaster;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.LinearLayout;

import uk.co.thomasc.wordmaster.objects.User;
import uk.co.thomasc.wordmaster.util.BaseGameActivity;
import uk.co.thomasc.wordmaster.view.menu.MenuDetailFragment;
import uk.co.thomasc.wordmaster.view.menu.MenuListFragment;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class BaseGame extends BaseGameActivity {

	public static Typeface russo;

	public MenuListFragment menuFragment;
	public MenuDetailFragment menuDetail;
	public boolean wideLayout = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.empty_screen);
		
		int screenLayoutSize = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
		wideLayout = screenLayoutSize > 2;
		if (!wideLayout) {
			((LinearLayout) findViewById(R.id.empty)).setWeightSum(1F);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		menuFragment = new MenuListFragment();

		BaseGame.russo = Typeface.createFromAsset(getAssets(), "fonts/Russo_One.ttf");

		getSupportFragmentManager().beginTransaction().add(R.id.empty, menuFragment).addToBackStack("top").commit();
	}

	@Override
	public void onSignInFailed() {
		getSupportFragmentManager().popBackStack("top", 0); // Close any open games
		menuFragment.onSignInFailed();
		System.out.println("oh noes!");
	}

	@Override
	public void onSignInSucceeded() {
		User.getUser(mHelper.getPlusClient().getCurrentPerson(), this); // Load local user into cache
		menuFragment.onSignInSucceeded();
	}

	@Override
	public void onBackPressed() {
		String topId = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
		if (menuDetail != null && topId.equals("game")) {
			menuDetail.hideKeyboard();
			menuDetail = null;
		}
		if (topId.equals("top")) {
			finish();
		} else {
			super.onBackPressed();
		}
	}

}
