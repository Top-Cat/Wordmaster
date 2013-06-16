package uk.co.thomasc.wordmaster.view.menu;

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

import uk.co.thomasc.wordmaster.BaseGame;
import uk.co.thomasc.wordmaster.R;
import uk.co.thomasc.wordmaster.api.GetMatchesRequestListener;
import uk.co.thomasc.wordmaster.api.ServerAPI;
import uk.co.thomasc.wordmaster.objects.Game;
import uk.co.thomasc.wordmaster.util.BaseGameActivity;
import uk.co.thomasc.wordmaster.view.create.CreateGameFragment;

public class MenuListFragment extends Fragment implements OnClickListener, GetMatchesRequestListener, OnItemClickListener {

	private MenuAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.menu_screen, container, false);

		SignInButton button = (SignInButton) v.findViewById(R.id.button_sign_in);
		button.setSize(SignInButton.SIZE_WIDE); // I commend anyone who can do this in XML

		adapter = new MenuAdapter(getActivity());
		ListView list = (ListView) v.findViewById(R.id.main_feed);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);

		int services = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
		if (services == ConnectionResult.SUCCESS) {
			button.setOnClickListener(this);
		} else {
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(services, getActivity(), 1);
			dialog.show();
		}

		return v;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
		BaseGame act = (BaseGame) getActivity();
		if (act.getSupportFragmentManager().getBackStackEntryCount() > 0) {
			act.getSupportFragmentManager().popBackStack();
		}
		Bundle args = new Bundle();
		args.putString(MenuDetailFragment.ARG_ITEM_ID, adapter.getItem(position).getID());
		Fragment fragment = act.menuDetail = new MenuDetailFragment();
		fragment.setArguments(args);
		FragmentTransaction ft = act.getSupportFragmentManager().beginTransaction()
			.setCustomAnimations(R.anim.slide_right, R.anim.slide_left, R.anim.slide_left_2, R.anim.slide_right_2)
			.addToBackStack("menu")
			.add(R.id.empty, fragment);
		if (!act.wideLayout) {
			ft.hide(act.menuFragment);
		}
		ft.commit();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.button_sign_in) {
			((BaseGameActivity) getActivity()).beginUserInitiatedSignIn();
		} else if (v.getId() == R.id.refresh && getView().findViewById(R.id.refresh).getVisibility() == View.VISIBLE) {
			getView().findViewById(R.id.refresh).setVisibility(View.GONE);
			getView().findViewById(R.id.refresh_progress).setVisibility(View.VISIBLE);
			loadGames();
		} else if (v.getId() == R.id.startnew) {
			Fragment fragment = new CreateGameFragment();
			getActivity().getSupportFragmentManager().beginTransaction()
				.setCustomAnimations(R.anim.fadein, 0, 0, R.anim.fadeout)
				.addToBackStack("userpicker")
				.add(R.id.outer, fragment)
				.commit();
		}
	}

	private void loadGames() {
		((BaseGame) getActivity()).games.clear();
		ServerAPI.getMatches(((BaseGameActivity) getActivity()).getUserId(), (BaseGame) getActivity(), this);
	}

	@Override
	public void onRequestComplete(final Game[] games) {
		final BaseGame act = (BaseGame) getActivity();
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				adapter.clear();
				for (Game game : games) {
					act.games.put(game.getID(), game);
					adapter.add(game);
				}
				refreshOver();
			}
		});
	}

	@Override
	public void onRequestFailed() {
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// TODO Tell the user their parents have been murdered
				refreshOver();
			}
		});
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

}
