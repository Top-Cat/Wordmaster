package uk.co.thomasc.wordmaster.view.menu;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.games.Games;

import android.annotation.SuppressLint;
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

import uk.co.thomasc.wordmaster.BaseGame;
import uk.co.thomasc.wordmaster.R;
import uk.co.thomasc.wordmaster.api.GetMatchesResponse;
import uk.co.thomasc.wordmaster.gcm.TurnReceiver;
import uk.co.thomasc.wordmaster.objects.Game;
import uk.co.thomasc.wordmaster.view.DialogPanel;
import uk.co.thomasc.wordmaster.view.Errors;
import uk.co.thomasc.wordmaster.view.create.CreateGameFragment;
import uk.co.thomasc.wordmaster.view.unhide.UnhideGameFragment;
import uk.co.thomasc.wordmaster.view.upgrade.UpgradeFragment;

public class MenuListFragment extends Fragment implements OnClickListener, OnItemClickListener {

	public static final String TAG = "MenuListFragment"; 
	
	private final Set<Game> games = new HashSet<Game>();
	public MenuAdapter adapter;
	@Getter public boolean hiddenGames;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.menu_screen, container, false);

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
			if (!BaseGame.wideLayout) {
				// Check to see if a game is open
				for (int i = 0; i < getFragmentManager().getBackStackEntryCount(); i++) {
					if (getFragmentManager().getBackStackEntryAt(i).getName().equals("game")) {
						// WE NEED TO HIDE!
						getFragmentManager().beginTransaction().hide(this).commit();
						break;
					}
				}
			}
		}

		if (BaseGame.isSignedIn()) {
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
			if (BaseGame.isSignedIn()) {
				onSignInSucceeded();
			} else {
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

		if (!isHiddenGames()) {
			popup.getMenu().removeItem(R.id.unhide_game);
		}

		popup.show();
		popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				if (BaseGame.isSignedIn()) {
					if (item.getItemId() == R.id.startnew) {
						startNew();
					} else if (item.getItemId() == R.id.show_achievements) {
						startActivityForResult(Games.Achievements.getAchievementsIntent(BaseGame.getApiClient()), 1001);
					} else if (item.getItemId() == R.id.action_logout) {
						BaseGame.getServerApi().registerGCM("", null);
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
		getFragmentManager().beginTransaction()
			.setCustomAnimations(R.anim.fadein, 0, 0, R.anim.fadeout)
			.addToBackStack("userpicker")
			.add(R.id.outer, new CreateGameFragment(), CreateGameFragment.TAG)
			.commit();
	}

	public void unhideGame() {
		List<String> hiddenGames = new ArrayList<String>();
		for (Game game : games) {
			if (!game.isVisible()) {
				hiddenGames.add(game.getID());
			}
		}
		
		Bundle args = new Bundle();
		args.putStringArray(UnhideGameFragment.ARG_ID, hiddenGames.toArray(new String[hiddenGames.size()]));
		UnhideGameFragment fragment = new UnhideGameFragment();
		fragment.setArguments(args);
		getFragmentManager().beginTransaction()
			.setCustomAnimations(R.anim.fadein, 0, 0, R.anim.fadeout)
			.addToBackStack("unhide")
			.add(R.id.outer, fragment, UnhideGameFragment.TAG)
			.commit();
	}

	public void loadGames() {
		// Change UI
		getView().findViewById(R.id.refresh).setVisibility(View.GONE);
		getView().findViewById(R.id.refresh_progress).setVisibility(View.VISIBLE);

		BaseGame.getServerApi().getMatches(new GetMatchesResponse() {

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

			@Override
			public void onRequestComplete(final Game[] gameResult) {
				MenuListFragment.this.onRequestComplete(gameResult);
			}
		});
	}

	public void onRequestComplete(final Game[] gameResult) {
		if (isAdded()) {
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					games.addAll(Arrays.asList(gameResult));
					Game.saveStates(getActivity());

					adapter.clear();
					hiddenGames = false;
					for (Game game : games) {
						if (game.isVisible()) {
							adapter.add(game);
						} else {
							hiddenGames = true;
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

	public void goToGame(String gameID) {
		adapter.setSelectedGid(gameID);

		if (!isAdded()) {
			getFragmentManager().popBackStack("game", 1);
		}

		if (gameID.length() > 0) {
			Bundle args = new Bundle();
			args.putString(MenuDetailFragment.ARG_ITEM_ID, gameID);

			Fragment fragment = new MenuDetailFragment();
			fragment.setArguments(args);

			FragmentTransaction ft = getFragmentManager().beginTransaction();
			if (!BaseGame.wideLayout) {
				ft.setCustomAnimations(R.anim.slide_right, R.anim.slide_left, R.anim.slide_left_2, R.anim.slide_right_2).hide(this);
			} else {
				ft.setCustomAnimations(R.anim.wide_in, 0, 0, R.anim.wide_out);
			}
			ft.addToBackStack("game")
				.add(R.id.empty, fragment, MenuDetailFragment.TAG)
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
				getView().findViewById(R.id.signin_progress).setVisibility(View.GONE);
				getView().findViewById(R.id.main_feed).setVisibility(View.VISIBLE);

				enableUI();
			}
		});

		longPoll();
	}

	private void longPoll() {
		BaseGame.getServerApi().longPoll(Game.updatePoint, new GetMatchesResponse() {
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
		getView().post(new Runnable() {
			@Override
			public void run() {
				// Clear games list
				adapter.clear();
				adapter.setSelectedGid("");
				Game.updatePoint = 0;

				// Show login button
				getView().findViewById(R.id.main_feed).setVisibility(View.GONE);
				getView().findViewById(R.id.signin_progress).setVisibility(View.GONE);
				getView().findViewById(R.id.button_sign_in).setVisibility(View.VISIBLE);
				getView().findViewById(R.id.whysignin).setVisibility(View.VISIBLE);

				disableUI();
			}
		});
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
			BaseGame.getServerApi().setGameVisible(game.getID(), false, null);
			return true;
		}
		return super.onContextItemSelected(item);
	}
	
	public UpgradeFragment getUpgradeFragment() {
		return (UpgradeFragment) getFragmentManager().findFragmentByTag(UpgradeFragment.TAG);
	}
	
	public CreateGameFragment getCreateGameFragment() {
		return (CreateGameFragment) getFragmentManager().findFragmentByTag(CreateGameFragment.TAG);
	}

}
