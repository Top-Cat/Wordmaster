package uk.co.thomasc.wordmaster.view.menu;

import uk.co.thomasc.wordmaster.BaseGame;
import uk.co.thomasc.wordmaster.R;
import uk.co.thomasc.wordmaster.api.CreateGameRequestListener;
import uk.co.thomasc.wordmaster.api.GetMatchesRequestListener;
import uk.co.thomasc.wordmaster.api.ServerAPI;
import uk.co.thomasc.wordmaster.objects.Game;
import uk.co.thomasc.wordmaster.objects.callbacks.GameCreationListener;
import uk.co.thomasc.wordmaster.util.BaseGameActivity;
import uk.co.thomasc.wordmaster.view.DialogPanel;
import uk.co.thomasc.wordmaster.view.Errors;
import uk.co.thomasc.wordmaster.view.create.CreateGameFragment;
import uk.co.thomasc.wordmaster.view.upgrade.UpgradeFragment;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;

public class MenuListFragment extends Fragment implements OnClickListener, GetMatchesRequestListener, OnItemClickListener, GameCreationListener {

	public MenuAdapter adapter;
	
	private CreateGameFragment createGameFragment;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.menu_screen, container, false);
		
		((BaseGame) getActivity()).menuFragment = this;

		SignInButton button = (SignInButton) v.findViewById(R.id.button_sign_in);
		button.setSize(SignInButton.SIZE_WIDE); // I commend anyone who can do this in XML

		adapter = new MenuAdapter(getActivity());
		ListView list = (ListView) v.findViewById(R.id.main_feed);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		
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
		} else {
			int services = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
			if (services == ConnectionResult.SUCCESS) {
				button.setOnClickListener(this);
			} else {
				Dialog dialog = GooglePlayServicesUtil.getErrorDialog(services, getActivity(), 1);
				dialog.show();
			}
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
			if (((BaseGame) getActivity()).isSignedIn()) {
				onSignInSucceeded();
			} else {
				((BaseGameActivity) getActivity()).beginUserInitiatedSignIn();
			}
		} else if (v.getId() == R.id.refresh && getView().findViewById(R.id.refresh).getVisibility() == View.VISIBLE) {
			loadGames();
		} else if (v.getId() == R.id.startnew) {
			createGameFragment = new CreateGameFragment();
			createGameFragment.setGameCreatedListener(this);
			getActivity().getSupportFragmentManager().beginTransaction()
				.setCustomAnimations(R.anim.fadein, 0, 0, R.anim.fadeout)
				.addToBackStack("userpicker")
				.add(R.id.outer, createGameFragment)
				.commit();
		}
	}

	public void loadGames() {
		//Change UI
		getView().findViewById(R.id.refresh).setVisibility(View.GONE);
		getView().findViewById(R.id.refresh_progress).setVisibility(View.VISIBLE);
		
		ServerAPI.getMatches(((BaseGame) getActivity()).getUserId(), (BaseGame) getActivity(), this);
	}

	@Override
	public void onRequestComplete(final Game[] games) {
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				adapter.clear();
				for (Game game : games) {
					adapter.add(game);
				}
				refreshOver();
				
				String gameid = ((BaseGame) getActivity()).goToGameId;
				if (gameid.length() > 0) {
					goToGame(gameid);
				}
			}
		});
	}

	@Override
	public void onRequestFailed() {
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				DialogPanel netError = (DialogPanel) getView().findViewById(R.id.dialog_panel);
				netError.show(Errors.NETWORK);
				
				refreshOver();
			}
		});
	}
	
	public void goToGame(String gameID) {
		BaseGame act = (BaseGame) getActivity();
		act.getSupportFragmentManager().popBackStack("game", 1);
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

	private void refreshOver() {
		getView().findViewById(R.id.refresh).setVisibility(View.VISIBLE);
		getView().findViewById(R.id.refresh_progress).setVisibility(View.GONE);
	}

	public void onSignInSucceeded() {
		getView().findViewById(R.id.button_sign_in).setVisibility(View.GONE);
		getView().findViewById(R.id.whysignin).setVisibility(View.GONE);
		getView().findViewById(R.id.main_feed).setVisibility(View.VISIBLE);

		// Enable UI
		getView().findViewById(R.id.refresh).setOnClickListener(this);
		getView().findViewById(R.id.startnew).setOnClickListener(this);

		loadGames();
	}

	public void onSignInFailed() {
		// Clear games list
		adapter.clear();
		
		// Show login button
		getView().findViewById(R.id.main_feed).setVisibility(View.GONE);
		getView().findViewById(R.id.button_sign_in).setVisibility(View.VISIBLE);
		getView().findViewById(R.id.whysignin).setVisibility(View.VISIBLE);

		// Disable UI
		getView().findViewById(R.id.refresh).setOnClickListener(null);
		getView().findViewById(R.id.startnew).setOnClickListener(null);
	}

	@Override
	public void onCreateGame(String playerID, String opponentID) {
		getActivity().getSupportFragmentManager().popBackStack("userpicker", 1);
		Game existingGame = Game.getGame(playerID, opponentID);
		if (existingGame != null) {
			goToGame(existingGame.getID());
		} else {
			ServerAPI.createGame(playerID, opponentID, (BaseGame) getActivity(), new CreateGameRequestListener() {
				@Override
				public void onRequestFailed(boolean hasPaid) {
					if (hasPaid) {
						// TODO: Tell the user unicorns died out with the dinosaurs
					} else {
						UpgradeFragment fragment = new UpgradeFragment();
						FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
						ft.setCustomAnimations(R.anim.fadein, 0, 0, R.anim.fadeout);
						ft.addToBackStack("upgrade")
							.add(R.id.outer, fragment)
							.commit();
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
		}
	}

}
