package uk.co.thomasc.wordmaster.view.menu;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.games.Games;

import uk.co.thomasc.wordmaster.BaseGame;
import uk.co.thomasc.wordmaster.R;
import uk.co.thomasc.wordmaster.api.CreateGameRequestListener;
import uk.co.thomasc.wordmaster.api.GetMatchesRequestListener;
import uk.co.thomasc.wordmaster.api.ServerAPI;
import uk.co.thomasc.wordmaster.gcm.TurnReceiver;
import uk.co.thomasc.wordmaster.objects.Game;
import uk.co.thomasc.wordmaster.objects.callbacks.GameCreationListener;
import uk.co.thomasc.wordmaster.objects.callbacks.UnhideGameListener;
import uk.co.thomasc.wordmaster.view.DialogPanel;
import uk.co.thomasc.wordmaster.view.Errors;
import uk.co.thomasc.wordmaster.view.create.CreateGameFragment;
import uk.co.thomasc.wordmaster.view.unhide.UnhideGameFragment;
import uk.co.thomasc.wordmaster.view.upgrade.UpgradeFragment;

public class MenuListFragment extends Fragment implements OnClickListener, GetMatchesRequestListener, OnItemClickListener, GameCreationListener, UnhideGameListener {

	public MenuAdapter adapter;
	
	private Set<Game> games = new HashSet<Game>(); 

	private CreateGameFragment createGameFragment;
	private UnhideGameFragment unhideGameFragment;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.menu_screen, container, false);

		((BaseGame) getActivity()).menuFragment = this;

		SignInButton button = (SignInButton) v.findViewById(R.id.button_sign_in);
		button.setSize(SignInButton.SIZE_WIDE); // I commend anyone who can do this in XML
		button.setOnClickListener(this);

		adapter = new MenuAdapter(getActivity());
		ListView list = (ListView) v.findViewById(R.id.main_feed);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		list.setCacheColorHint(Color.WHITE);
		registerForContextMenu(list);

		if (savedInstanceState != null) {
			BaseGame act = (BaseGame) getActivity();
			if (!act.wideLayout) {
				// Check to see if a game is open
				for (int i = 0; i < act.getSupportFragmentManager().getBackStackEntryCount(); i++) {
					if (act.getSupportFragmentManager().getBackStackEntryAt(i).getName().equals("game")) {
						// WE NEED TO HIDE!
						act.getSupportFragmentManager().beginTransaction().hide(this).commit();
						break;
					}
				}
			}
		}

		if (((BaseGame) getActivity()).isSignedIn()) {
			onSignInSucceeded();
		}

		return v;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
		goToGame(adapter.getItem(position).getID());
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.button_sign_in) {
			// TODO: ((BaseGame) getActivity()).checkPlayServices();
			if (((BaseGame) getActivity()).isSignedIn()) {
				onSignInSucceeded();
			} else  {
				((BaseGame) getActivity()).beginUserInitiatedSignIn();
			}
		} else if (v.getId() == R.id.refresh && getView().findViewById(R.id.refresh).getVisibility() == View.VISIBLE) {
			loadGames();
		} else if (v.getId() == R.id.startnew) {
			startNew();
		} else if (v.getId() == R.id.dropdown) {
			showPopup(v);
		}
	}

	@SuppressLint("NewApi")
	public void showPopup(View v) {
		PopupMenu popup = new PopupMenu(getActivity(), v);
		popup.getMenuInflater().inflate(R.menu.main_menu, popup.getMenu());

		final Set<String> hiddenGames = ((BaseGame) getActivity()).getHiddenGamesPreferences().getAll().keySet();
		if (hiddenGames.isEmpty()) {
			popup.getMenu().removeItem(R.id.unhide_game);
		}

		popup.show();
		popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				if (((BaseGame) getActivity()).isSignedIn()) {
					if (item.getItemId() == R.id.startnew) {
						startNew();
					} else if (item.getItemId() == R.id.show_achievements) {
						startActivityForResult(Games.Achievements.getAchievementsIntent(((BaseGame) getActivity()).getApiClient()), 1001);
					} else if (item.getItemId() == R.id.action_logout) {
						ServerAPI.registerGCM("", (BaseGame) getActivity());
						TurnReceiver.resetNotifications(getActivity());
						((BaseGame) getActivity()).signOut();
					} else if (item.getItemId() == R.id.unhide_game) {
						unhideGame();
					}
				}
				return true;
			}
		});
	}

	private void startNew() {
		createGameFragment = new CreateGameFragment();
		createGameFragment.setGameCreatedListener(this);
		getActivity().getSupportFragmentManager().beginTransaction()
				.setCustomAnimations(R.anim.fadein, 0, 0, R.anim.fadeout)
				.addToBackStack("userpicker")
				.add(R.id.outer, createGameFragment)
				.commit();
	}

	public void unhideGame() {
		unhideGameFragment = new UnhideGameFragment();
		unhideGameFragment.setUnhideGameListener(this);
		String[] hiddenGames = new String[((BaseGame) getActivity()).getHiddenGamesPreferences().getAll().size()];
		hiddenGames = ((BaseGame) getActivity()).getHiddenGamesPreferences().getAll().keySet().toArray(hiddenGames);
		Bundle args = new Bundle();
		args.putStringArray(UnhideGameFragment.ARG_ID, hiddenGames);
		unhideGameFragment.setArguments(args);
		getActivity().getSupportFragmentManager().beginTransaction()
				.setCustomAnimations(R.anim.fadein, 0, 0, R.anim.fadeout)
				.addToBackStack("unhide")
				.add(R.id.outer, unhideGameFragment)
				.commit();
	}

	public void loadGames() {
		//Change UI
		getView().findViewById(R.id.refresh).setVisibility(View.GONE);
		getView().findViewById(R.id.refresh_progress).setVisibility(View.VISIBLE);

		ServerAPI.getMatches((BaseGame) getActivity(), this);
	}

	@Override
	public void onRequestComplete(final Game[] gameResult) {
		if (isAdded()) {
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					games.addAll(Arrays.asList(gameResult));
					
					adapter.clear();
					SharedPreferences prefs = ((BaseGame) getActivity()).getHiddenGamesPreferences();
					for (Game game : games) {
						if (prefs.contains(game.getID())) {
							if (!prefs.getBoolean(game.getID(), false)) {
								adapter.add(game);
							}
						} else {
							adapter.add(game);
						}
					}
					refreshOver();

					if (isResumed()) {
						String gameid = ((BaseGame) getActivity()).getGoToGameId();
						if (gameid.length() > 0) {
							goToGame(gameid);
						}
					}
				}
			});
		}
	}

	@Override
	public void onRequestFailed(int errorCode) {
		if (isAdded()) {
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					DialogPanel netError = (DialogPanel) getView().findViewById(R.id.dialog_panel);
					netError.show(Errors.NETWORK);

					refreshOver();
				}
			});
		}
	}

	public void goToGame(String gameID) {
		BaseGame act = (BaseGame) getActivity();
		adapter.setSelectedGid(gameID);
		
		if (!isAdded()) {
			act.getSupportFragmentManager().popBackStack("game", 1);
		}
		
		if (gameID.length() > 0) {
			Bundle args = new Bundle();
			args.putString(MenuDetailFragment.ARG_ITEM_ID, gameID);
			
			Fragment fragment = new MenuDetailFragment();
			fragment.setArguments(args);
			
			FragmentTransaction ft = act.getSupportFragmentManager().beginTransaction();
			if (!act.wideLayout) {
				ft.setCustomAnimations(R.anim.slide_right, R.anim.slide_left, R.anim.slide_left_2, R.anim.slide_right_2)
						.hide(this);
			} else {
				ft.setCustomAnimations(R.anim.wide_in, 0, 0, R.anim.wide_out);
			}
			ft.addToBackStack("game")
					.add(R.id.empty, fragment)
					.commit();
		}
	}

	private void refreshOver() {
		getView().findViewById(R.id.refresh).setVisibility(View.VISIBLE);
		getView().findViewById(R.id.refresh_progress).setVisibility(View.GONE);
	}

	public void onSignInSucceeded() {
		getView().post(new Runnable() {
			@Override
			public void run() {
				getView().findViewById(R.id.button_sign_in).setVisibility(View.GONE);
				getView().findViewById(R.id.whysignin).setVisibility(View.GONE);
				getView().findViewById(R.id.main_feed).setVisibility(View.VISIBLE);
			}
		});

		enableUI();
		
		longPoll();
	}

	private void longPoll() {
		ServerAPI.longPoll((BaseGame) getActivity(), Game.updatePoint, new GetMatchesRequestListener() {
			@Override
			public void onRequestFailed(int errorCode) {
				if (errorCode != -2) {
					longPoll();
				}
			}
			@Override
			public void onRequestComplete(Game[] games) {
				MenuListFragment.this.onRequestComplete(games);
				longPoll();
			}
		});
	}

	public void onSignInFailed() {
		// Clear games list
		adapter.clear();
		adapter.setSelectedGid("");

		// Show login button
		getView().findViewById(R.id.main_feed).setVisibility(View.GONE);
		getView().findViewById(R.id.button_sign_in).setVisibility(View.VISIBLE);
		getView().findViewById(R.id.whysignin).setVisibility(View.VISIBLE);

		disableUI();
	}

	private void safeSetOnClickListener(int id, OnClickListener listener) {
		View view = getView().findViewById(id);
		if (view != null) {
			view.setOnClickListener(listener);
		}
	}

	private void safeSetVisiblity(int id, int visibility) {
		View view = getView().findViewById(id);
		if (view != null) {
			view.setVisibility(visibility);
		}
	}

	private void disableUI() {
		getView().findViewById(R.id.refresh).setVisibility(View.GONE);
		safeSetOnClickListener(R.id.startnew, null);
		safeSetVisiblity(R.id.startnew, View.GONE);
		safeSetOnClickListener(R.id.dropdown, null);
		safeSetVisiblity(R.id.dropdown, View.GONE);
	}

	private void enableUI() {
		getView().findViewById(R.id.refresh).setVisibility(View.VISIBLE);
		getView().findViewById(R.id.refresh).setOnClickListener(this);
		safeSetOnClickListener(R.id.startnew, this);
		safeSetVisiblity(R.id.startnew, View.VISIBLE);
		safeSetOnClickListener(R.id.dropdown, this);
		safeSetVisiblity(R.id.dropdown, View.VISIBLE);
	}

	@Override
	public void onCreateGame(String opponentID) {
		getActivity().getSupportFragmentManager().popBackStack("userpicker", 1);
		//TODO: Unhide game on response from server
		/*Game existingGame = Game.getGame(playerID, opponentID);
		if (existingGame != null) { // TODO: This needs to use isLoaded
			if (((BaseGame) getActivity()).getHiddenGamesPreferences().contains(existingGame.getID())) {
				Editor editor = ((BaseGame) getActivity()).getHiddenGamesPreferences().edit();
				editor.remove(existingGame.getID());
				editor.commit();
				adapter.add(existingGame);
			}
			goToGame(existingGame.getID());
		} else {*/
			ServerAPI.createGame(opponentID, (BaseGame) getActivity(), new CreateGameRequestListener() {
				@Override
				public void onRequestFailed(final int errorCode) {
					if (errorCode == 4) {
						UpgradeFragment fragment = new UpgradeFragment();
						((BaseGame) getActivity()).upgradeFragment = fragment;
						FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
						ft.setCustomAnimations(R.anim.fadein, 0, 0, R.anim.fadeout);
						ft.addToBackStack("upgrade")
								.add(R.id.outer, fragment)
								.commit();
					} else {
						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								DialogPanel netError = (DialogPanel) getView().findViewById(R.id.dialog_panel);
								if (errorCode == 5) {
									netError.show(Errors.MATCH);
								} else if (errorCode == 9) {
									netError.show(Errors.AUTOMATCH);
								} else {
									netError.show(Errors.SERVER);
								}
							}
						});
					}
				}

				@Override
				public void onRequestComplete(final Game game) {
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							adapter.add(game);
						}
					});
					goToGame(game.getID());
				}
			});
		//}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo info) {
		super.onCreateContextMenu(menu, v, info);
		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		if (item.getItemId() == R.id.hide_game) {
			Game game = adapter.getItem(info.position);
			Editor editor = ((BaseGame) getActivity()).getHiddenGamesPreferences().edit();
			editor.putBoolean(game.getID(), true);
			editor.commit();
			adapter.remove(game);
			return true;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void onUnhideGame(Game game) {
		getActivity().getSupportFragmentManager().popBackStack("unhide", 1);
		Editor editor = ((BaseGame) getActivity()).getHiddenGamesPreferences().edit();
		editor.remove(game.getID());
		editor.commit();
		adapter.add(game);
	}

}
