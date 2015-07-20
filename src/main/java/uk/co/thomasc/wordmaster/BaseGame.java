package uk.co.thomasc.wordmaster;

import lombok.Getter;

import java.util.ArrayList;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.games.Games;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import uk.co.thomasc.wordmaster.api.ServerAPI;
import uk.co.thomasc.wordmaster.game.Achievements;
import uk.co.thomasc.wordmaster.gcm.RegistrationIntentService;
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
import uk.co.thomasc.wordmaster.view.create.PersonAdapter;
import uk.co.thomasc.wordmaster.view.menu.MenuDetailFragment;
import uk.co.thomasc.wordmaster.view.menu.MenuListFragment;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class BaseGame extends FragmentActivity implements OnIabPurchaseFinishedListener, ConnectionCallbacks, OnConnectionFailedListener {

	public static Typeface russo;
	public static boolean wideLayout = false;

	private String goToGameId = "";

	public static IabHelper mBHelper;
	public static String upgradeSKU = "wordmaster_upgrade";
	@Getter private boolean iabAvailable;

	private BroadcastReceiver mRegistrationBroadcastReceiver;

	@Getter private static ServerAPI serverApi;

	private static GoogleApiClient mGoogleApiClient;
	private boolean mSignInFlow = true;
	private static final int RC_SIGN_IN = 9001;
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9002;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.empty_screen);

		int screenLayoutSize = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
		wideLayout = screenLayoutSize > 2;
		setRequestedOrientation(wideLayout ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		BaseGame.russo = Typeface.createFromAsset(getAssets(), "fonts/Russo_One.ttf");

		BaseGame.mGoogleApiClient = new GoogleApiClient.Builder(this)
			.addConnectionCallbacks(this)
			.addOnConnectionFailedListener(this)
			.addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
			.addApi(Games.API).addScope(Games.SCOPE_GAMES)
			.build();

		BaseGame.serverApi = new ServerAPI();

		mRegistrationBroadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Bundle extras = intent.getExtras();
				if (extras != null && extras.containsKey("id")) {
					goToGame(intent.getExtras().getString("id"));
				} else {
					goToGame(goToGameId);
				}
			}
		};

		if (savedInstanceState != null) {
			Game.restoreState(savedInstanceState);
		} else {
			getSupportFragmentManager().beginTransaction().add(R.id.empty, new MenuListFragment(), MenuListFragment.TAG).addToBackStack("top").commit();
		}

		checkIntent(getIntent());

		String base64PublicKey = Game.keySegment + Turn.keySegment + User.keySegment + PersonAdapter.keySegment;
		BaseGame.mBHelper = new IabHelper(this, base64PublicKey);
		BaseGame.mBHelper.startSetup(new OnIabSetupFinishedListener() {
			@Override
			public void onIabSetupFinished(IabResult result) {
				if (!result.isSuccess()) {
					iabAvailable = false;
				} else {
					iabAvailable = true;
				}
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter("open-game"));
	}

	@Override
	protected void onPause() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
		super.onPause();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// Pass on the activity result to the helper for handling
		if (!BaseGame.mBHelper.handleActivityResult(requestCode, resultCode, data)) {
			if (requestCode == BaseGame.RC_SIGN_IN) {
				if (resultCode == Activity.RESULT_OK) {
					signIn();
				} else {
					// Show error?
				}
			}
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
			goToGame(extras.getString("gameid"));
		}
	}

	public void goToGame(String gameid) {
		if (Game.getGame(gameid) != null) {
			getMenuFragment().goToGame(gameid);
		} else {
			goToGameId = gameid;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (BaseGame.mBHelper != null) {
			BaseGame.mBHelper.dispose();
		}
		BaseGame.mBHelper = null;
	}

	public void buyUpgrade() {
		BaseGame.mBHelper.launchPurchaseFlow(this, BaseGame.upgradeSKU, 1902, this, User.getCurrentUser().getPlusID());
	}

	public static void consumeUpgrades() {
		BaseGame.mBHelper.queryInventoryAsync(new QueryInventoryFinishedListener() {
			@Override
			public void onQueryInventoryFinished(IabResult result, Inventory inv) {
				if (inv.hasPurchase(BaseGame.upgradeSKU)) {
					BaseGame.mBHelper.consumeAsync(inv.getPurchase(BaseGame.upgradeSKU), null);
				}
			}
		});
	}

	@Override
	public void onIabPurchaseFinished(IabResult result, Purchase info) {
		if (result.isFailure()) {
			if (result.getResponse() != IabHelper.IABHELPER_USER_CANCELLED) {
				getMenuFragment().getUpgradeFragment().upgradeFailed();
			}
		} else {
			getMenuFragment().getUpgradeFragment().upgradeComplete();
			BaseGame.getServerApi().upgradePurchased(info.getToken());
		}
	}

	public static void queryInventory(QueryInventoryFinishedListener listener) {
		ArrayList<String> additionalSkuList = new ArrayList<String>();
		additionalSkuList.add(BaseGame.upgradeSKU);
		BaseGame.mBHelper.queryInventoryAsync(true, additionalSkuList, listener);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Game.saveState(outState);
	}

	@Override
	public void onBackPressed() {
		String topId = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
		if (topId.equals("game")) {
			getGameFragment().hideKeyboard();
			getMenuFragment().adapter.setSelectedGid("");
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
			getMenuFragment().showPopup(findViewById(R.id.dropdown));
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		String topId = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion < android.os.Build.VERSION_CODES.HONEYCOMB && BaseGame.isSignedIn() && topId.equals("top")) {
			getMenuInflater().inflate(R.menu.main_menu, menu);
			if (!getMenuFragment().isHiddenGames()) {
				menu.removeItem(R.id.unhide_game);
			}
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	public static boolean isSignedIn() {
		return BaseGame.mGoogleApiClient != null && BaseGame.mGoogleApiClient.isConnected();
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		String topId = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion < android.os.Build.VERSION_CODES.HONEYCOMB && BaseGame.isSignedIn() && topId.equals("top")) {
			if (menu.findItem(R.id.unhide_game) != null) {
				if (!getMenuFragment().isHiddenGames()) {
					menu.removeItem(R.id.unhide_game);
				}
			} else {
				if (getMenuFragment().isHiddenGames()) {
					menu.add(Menu.NONE, R.id.unhide_game, Menu.NONE, R.string.unhide_title);
				}
			}
			return true;
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (BaseGame.isSignedIn()) {
			switch (item.getItemId()) {
				case R.id.unhide_game:
					getMenuFragment().unhideGame();
					return true;
				case R.id.show_achievements:
					startActivityForResult(Games.Achievements.getAchievementsIntent(BaseGame.getApiClient()), 1001);
					return true;
				case R.id.action_logout:
					signOut();
					return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}

	public void signOut() {
		if (BaseGame.mGoogleApiClient.isConnected()) {
			Games.signOut(BaseGame.mGoogleApiClient);
			BaseGame.mGoogleApiClient.disconnect();
		}

		try {
			getSupportFragmentManager().popBackStack("top", 0); // Close any open games
		} catch (IllegalStateException e) {
			// We've quit already :(
		}

		getMenuFragment().onSignInFailed();
		BaseGame.serverApi.revoke();
		/*
		 * for (Game game : Game.games.values()) {
		 * game.clearTurns();
		 * }
		 */
	}

	public static void unlockAchievement(Achievements achievement, int increment) {
		if (BaseGame.isSignedIn()) {
			for (String id : achievement.getIds()) {
				if (!achievement.isIncremental()) {
					Games.Achievements.unlock(BaseGame.getApiClient(), id);
				} else if (increment > 0) {
					Games.Achievements.increment(BaseGame.getApiClient(), id, increment);
				}
			}
		}
	}

	public static void unlockAchievement(Achievements achievement) {
		BaseGame.unlockAchievement(achievement, 0);
	}

	public static int convertDip2Pixels(Resources resources, int dip) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, resources.getDisplayMetrics());
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		signOut();
		if (mSignInFlow) {
			mSignInFlow = false;
			resolveConnectionFailure(arg0);
		}
	}

	private boolean resolveConnectionFailure(ConnectionResult result) {
		if (result.hasResolution()) {
			try {
				result.startResolutionForResult(this, BaseGame.RC_SIGN_IN);
				return true;
			} catch (IntentSender.SendIntentException e) {
				signIn();
				return false;
			}
		} else {
			int errorCode = result.getErrorCode();
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(errorCode, this, BaseGame.RC_SIGN_IN);
			if (dialog != null) {
				dialog.show();
			} else {
				new AlertDialog.Builder(this).setMessage(getString(R.string.signin_other_error)).setNeutralButton(android.R.string.ok, null).create().show();
			}
			return false;
		}
	}

	@Override
	public void onConnected(Bundle arg0) {
		if (BaseGame.getApiClient().isConnected()) {
			new Thread() {
				@Override
				public void run() {
					String authToken;
					try {
						authToken = GoogleAuthUtil.getToken(BaseGame.this, Plus.AccountApi.getAccountName(BaseGame.getApiClient()), "oauth2:" + Scopes.GAMES);
						BaseGame.getServerApi().identify(authToken, BaseGame.this);
					} catch (Exception e) {
						// signOut(); - 07-16 22:25:11.171: E/AndroidRuntime(12582): java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
						e.printStackTrace();
					}
				}
			}.start();
		}
	}

	public static GoogleApiClient getApiClient() {
		return BaseGame.mGoogleApiClient;
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		signIn();
	}

	@Override
	protected void onStart() {
		super.onStart();
		signIn();
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (BaseGame.mGoogleApiClient.isConnected()) {
			BaseGame.mGoogleApiClient.disconnect();
		}
		BaseGame.serverApi.revoke();
	}

	public void beginUserInitiatedSignIn() {
		mSignInFlow = true;
		signIn();
	}

	public void signIn() {
		BaseGame.mGoogleApiClient.reconnect();
		getMenuFragment().getView().findViewById(R.id.signin_progress).setVisibility(View.VISIBLE);
		getMenuFragment().getView().findViewById(R.id.button_sign_in).setVisibility(View.GONE);
		getMenuFragment().getView().findViewById(R.id.whysignin).setVisibility(View.GONE);
	}

	public boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
					BaseGame.PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				finish();
			}
			return false;
		}
		return true;
	}

	public String getGoToGameId() {
		String temp = goToGameId;
		goToGameId = "";
		return temp;
	}

	public void onIdentified() {
		if (BaseGame.getApiClient().isConnected()) {
			Person person = Plus.PeopleApi.getCurrentPerson(BaseGame.getApiClient());
			User.onPlusConnected(person);

			getMenuFragment().onSignInSucceeded();

			if (checkPlayServices()) {
				Intent intent = new Intent(this, RegistrationIntentService.class);
				startService(intent);
			}
		}
	}
	
	public MenuListFragment getMenuFragment() {
		return (MenuListFragment) getSupportFragmentManager().findFragmentByTag(MenuListFragment.TAG);
	}

	public MenuDetailFragment getGameFragment() {
		return (MenuDetailFragment) getSupportFragmentManager().findFragmentByTag(MenuDetailFragment.TAG);
	}

}
