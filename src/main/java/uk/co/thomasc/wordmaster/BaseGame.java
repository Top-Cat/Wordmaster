package uk.co.thomasc.wordmaster;

import java.util.ArrayList;
import java.util.Set;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.google.android.gms.plus.model.people.Person;

import uk.co.thomasc.wordmaster.api.ServerAPI;
import uk.co.thomasc.wordmaster.game.Achievements;
import uk.co.thomasc.wordmaster.gcm.RegisterThread;
import uk.co.thomasc.wordmaster.gcm.TurnReceiver;
import uk.co.thomasc.wordmaster.iab.IabHelper;
import uk.co.thomasc.wordmaster.iab.IabHelper.OnIabPurchaseFinishedListener;
import uk.co.thomasc.wordmaster.iab.IabHelper.OnIabSetupFinishedListener;
import uk.co.thomasc.wordmaster.iab.IabHelper.QueryInventoryFinishedListener;
import uk.co.thomasc.wordmaster.iab.IabResult;
import uk.co.thomasc.wordmaster.iab.Inventory;
import uk.co.thomasc.wordmaster.iab.Purchase;
import uk.co.thomasc.wordmaster.objects.Game;
import uk.co.thomasc.wordmaster.objects.Turn;
import uk.co.thomasc.wordmaster.objects.User;
import uk.co.thomasc.wordmaster.util.BaseGameActivity;
import uk.co.thomasc.wordmaster.view.create.PersonAdapter;
import uk.co.thomasc.wordmaster.view.game.GameAdapter;
import uk.co.thomasc.wordmaster.view.menu.MenuDetailFragment;
import uk.co.thomasc.wordmaster.view.menu.MenuListFragment;
import uk.co.thomasc.wordmaster.view.upgrade.UpgradeFragment;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class BaseGame extends BaseGameActivity implements OnIabPurchaseFinishedListener {

	public static Typeface russo;

	public MenuListFragment menuFragment;
	public MenuDetailFragment menuDetail;
	public GameAdapter gameAdapter;
	public boolean wideLayout = false;

	private String userId = "";
	public String goToGameId = "";

	public static IabHelper mHelper;
	public static String upgradeSKU = "wordmaster_upgrade";
	public UpgradeFragment upgradeFragment;
	private boolean iabAvailable;

	private String PREFS = "WM_HIDDEN_GAMES_";
	private SharedPreferences prefs;

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

		BaseGame.russo = Typeface.createFromAsset(getAssets(), "fonts/Russo_One.ttf");

		if (savedInstanceState != null) {
			Game.restoreState(savedInstanceState, this);
		} else {
			getSupportFragmentManager().beginTransaction().add(R.id.empty, new MenuListFragment()).addToBackStack("top").commit();
		}

		checkIntent(getIntent());

		String base64PublicKey = Game.keySegment + Turn.keySegment + User.keySegment + PersonAdapter.keySegment;
		BaseGame.mHelper = new IabHelper(this, base64PublicKey);
		BaseGame.mHelper.startSetup(new OnIabSetupFinishedListener() {
			@Override
			public void onIabSetupFinished(IabResult result) {
				if (!result.isSuccess()) {
					iabAvailable = false;
				} else {
					iabAvailable = true;
					// for testing purposes, tell google play we've consumed the upgrade SKUs
					consumeUpgrades();
					// TODO: Get rid of this before release!
				}
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// Pass on the activity result to the helper for handling
		if (!BaseGame.mHelper.handleActivityResult(requestCode, resultCode, data)) {
			super.onActivityResult(requestCode, resultCode, data);
		} else {

		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		checkIntent(intent);
	}

	private void checkIntent(Intent intent) {
		TurnReceiver.resetNotifications(this);

		Bundle extras = intent.getExtras();
		if (extras != null && extras.containsKey("gameid")) {
			String gameid = extras.getString("gameid");
			if (Game.getGame(gameid) != null) {
				menuFragment.goToGame(gameid);
			} else {
				goToGameId = gameid;
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (BaseGame.mHelper != null) {
			BaseGame.mHelper.dispose();
		}
		BaseGame.mHelper = null;
	}

	public void buyUpgrade() {
		BaseGame.mHelper.launchPurchaseFlow(this, BaseGame.upgradeSKU, 1902, this, userId);
	}

	public static void consumeUpgrades() {
		BaseGame.mHelper.queryInventoryAsync(new QueryInventoryFinishedListener() {
			@Override
			public void onQueryInventoryFinished(IabResult result, Inventory inv) {
				if (inv.hasPurchase(BaseGame.upgradeSKU)) {
					BaseGame.mHelper.consumeAsync(inv.getPurchase(BaseGame.upgradeSKU), null);
				}
			}
		});
	}

	@Override
	public void onIabPurchaseFinished(IabResult result, Purchase info) {
		if (result.isFailure()) {
			if (result.getResponse() != IabHelper.IABHELPER_USER_CANCELLED) {
				upgradeFragment.upgradeFailed();
			}
		} else {
			upgradeFragment.upgradeComplete();
			ServerAPI.upgradePurchased(info.getToken(), this);
		}
	}

	public static void queryInventory(QueryInventoryFinishedListener listener) {
		ArrayList<String> additionalSkuList = new ArrayList<String>();
		additionalSkuList.add(BaseGame.upgradeSKU);
		BaseGame.mHelper.queryInventoryAsync(true, additionalSkuList, listener);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Game.saveState(outState);
		Game.saveState(this);
	}

	@Override
	public void onSignInFailed() {
		getSupportFragmentManager().popBackStack("top", 0); // Close any open games
		menuFragment.onSignInFailed();
		System.out.println("oh noes!");
	}

	@Override
	public void onSignInSucceeded() {
		if (getPlusClient().isConnected()) {
			Person person = getPlusClient().getCurrentPerson();
			userId = person.getId();
			User.onPlusConnected(this);
			User.getUser(person, this); // Load local user into cache
			loadPreferences();
			menuFragment.onSignInSucceeded();
			if (gameAdapter != null) {
				menuDetail.loadTurns();
				gameAdapter.notifyDataSetChanged();
			}
			new RegisterThread(this).start();
		}
	}

	private void loadPreferences() {
		prefs = getSharedPreferences(PREFS + userId, 0);
	}

	public SharedPreferences getHiddenGamesPreferences() {
		return prefs;
	}

	@Override
	public void onBackPressed() {
		String topId = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
		if (menuDetail != null && topId.equals("game")) {
			menuDetail.hideKeyboard();
			menuDetail = null;
			gameAdapter = null;
			menuFragment.loadGames();
		}
		if (topId.equals("top")) {
			finish();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		String topId = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB && keyCode == KeyEvent.KEYCODE_MENU && topId.equals("top")) {
			menuFragment.showPopup(findViewById(R.id.dropdown));
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		String topId = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion < android.os.Build.VERSION_CODES.HONEYCOMB && isSignedIn() && topId.equals("top")) {
			getMenuInflater().inflate(R.menu.main_menu, menu);
			final Set<String> hiddenGames = prefs.getAll().keySet();
			if (hiddenGames.isEmpty()) {
				menu.removeItem(R.id.unhide_game);
			}
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		String topId = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion < android.os.Build.VERSION_CODES.HONEYCOMB && isSignedIn() && topId.equals("top")) {
			final Set<String> hiddenGames = prefs.getAll().keySet();
			if (menu.findItem(R.id.unhide_game) != null) {
				if (hiddenGames.isEmpty()) {
					menu.removeItem(R.id.unhide_game);
				}
			} else {
				if (! hiddenGames.isEmpty()) {
					menu.add(Menu.NONE, R.id.unhide_game, Menu.NONE, R.string.unhide_title);
				}
			}
			return true;
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (isSignedIn()) {
			switch (item.getItemId()) {
				case R.id.unhide_game:
					menuFragment.unhideGame();
					return true;
				case R.id.show_achievements:
					startActivityForResult(getGamesClient().getAchievementsIntent(), 1001);
					return true;
				case R.id.action_logout:
					signOut();
					return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}

	public void unlockAchievement(Achievements achievement, int increment) {
		for (String id : achievement.getIds()) {
			if (!achievement.isIncremental()) {
				getGamesClient().unlockAchievement(id);
			} else if (increment > 0) {
				getGamesClient().incrementAchievement(id, increment);
			}
		}
	}

	public void unlockAchievement(Achievements achievement) {
		unlockAchievement(achievement, 0);
	}

	public String getUserId() {
		return userId;
	}

	public boolean isIabAvailable() {
		return iabAvailable;
	}

}
