package uk.co.thomasc.wordmaster;

import java.util.HashMap;

import uk.co.thomasc.wordmaster.objects.Game;
import uk.co.thomasc.wordmaster.util.BaseGameActivity;
import uk.co.thomasc.wordmaster.view.menu.MenuDetailFragment;
import uk.co.thomasc.wordmaster.view.menu.MenuListFragment;

import android.graphics.Typeface;
import android.os.Bundle;

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
	
	public HashMap<String, Game> games = new HashMap<String, Game>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		menuFragment = new MenuListFragment();
		
		russo = Typeface.createFromAsset(getAssets(), "fonts/Russo_One.ttf");

		setContentView(R.layout.empty_screen);
		
		getSupportFragmentManager().beginTransaction().add(R.id.empty, menuFragment).commit();
	}
	
	@Override
	public void onSignInFailed() {
		System.out.println("oh noes!");
	}
	
	@Override
	public void onSignInSucceeded() {
		menuFragment.onSignInSucceeded();
	}
	
	public Game gameForGameID(String gameID) {
		return games.get(gameID);
	}
	
	@Override
	public void onBackPressed() {
		if (menuDetail != null) {
			menuDetail.hideKeyboard();
			getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_left_2, R.anim.slide_right_2).remove(menuDetail).commit();
		}
	}
	
}
