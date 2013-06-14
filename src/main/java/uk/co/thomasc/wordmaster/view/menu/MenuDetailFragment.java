package uk.co.thomasc.wordmaster.view.menu;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import uk.co.thomasc.wordmaster.BaseGame;
import uk.co.thomasc.wordmaster.R;
import uk.co.thomasc.wordmaster.objects.Game;
import uk.co.thomasc.wordmaster.util.CapsLockLimiter;
import uk.co.thomasc.wordmaster.view.game.GameLayout;
import uk.co.thomasc.wordmaster.view.game.SwipeController;
import uk.co.thomasc.wordmaster.view.game.SwipeListener;

public class MenuDetailFragment extends Fragment {

	public static final String ARG_ITEM_ID = "gameid";
	private Game game;
	private String gameid;
	private EditText input;

	public MenuDetailFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments().containsKey(MenuDetailFragment.ARG_ITEM_ID)) {
			gameid = getArguments().getString(MenuDetailFragment.ARG_ITEM_ID);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(input, InputMethodManager.SHOW_FORCED);
		getActivity().getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
	}

	public void hideKeyboard() {
		((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(input.getWindowToken(), 0);
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.game_screen, container, false);

		game = ((BaseGame) getActivity()).gameForGameID(gameid);
		// TODO: populate view

		input = (EditText) rootView.findViewById(R.id.editText1);
		input.addTextChangedListener(new CapsLockLimiter(input));

		((GameLayout) rootView.findViewById(R.id.screen_game)).setActivity(getActivity());

		SwipeController swipe = new SwipeController(getActivity().getSupportFragmentManager(), gameid);
		ViewPager mPager = (ViewPager) rootView.findViewById(R.id.pager);
		mPager.setAdapter(swipe);
		mPager.setOnPageChangeListener(new SwipeListener((ImageView) rootView.findViewById(R.id.indicator)));

		return rootView;
	}
}
