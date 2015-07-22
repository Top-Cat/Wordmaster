package uk.co.thomasc.wordmaster.view.menu;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import uk.co.thomasc.wordmaster.BaseGame;
import uk.co.thomasc.wordmaster.R;
import uk.co.thomasc.wordmaster.objects.Game;
import uk.co.thomasc.wordmaster.objects.Turn;
import uk.co.thomasc.wordmaster.objects.User;
import uk.co.thomasc.wordmaster.objects.callbacks.GameListener;
import uk.co.thomasc.wordmaster.util.CapsLockLimiter;
import uk.co.thomasc.wordmaster.util.TurnMaker;
import uk.co.thomasc.wordmaster.view.AvatarView;
import uk.co.thomasc.wordmaster.view.game.GameLayout;
import uk.co.thomasc.wordmaster.view.game.SwipeController;
import uk.co.thomasc.wordmaster.view.game.SwipeListener;

public class MenuDetailFragment extends Fragment implements GameListener, OnTouchListener {

	public static final String TAG = "MenuDetailFragment";

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
	public void onDestroyView() {
		super.onDestroyView();
		if (game != null) {
			game.removeTurnListener(this);
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

	public void showKeyboard() {
		((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(input, 0);
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.game_screen, container, false);

		game = Game.getGame(gameid);
		if (game.isLoaded()) {
			((AvatarView) rootView.findViewById(R.id.playerAvatar)).setUser(User.getCurrentUser());
			((AvatarView) rootView.findViewById(R.id.oppAvatar)).setUser(game.getOpponent());

			((TextView) rootView.findViewById(R.id.turn)).setText(Integer.toString(game.getTurnNumber()));
			((TextView) rootView.findViewById(R.id.playerscore)).setText(Integer.toString(game.getPlayerScore()));
			((TextView) rootView.findViewById(R.id.oppscore)).setText(Integer.toString(game.getOpponentScore()));

			if (game.isNeedingWord()) {
				rootView.findViewById(R.id.setword_msg).setVisibility(View.VISIBLE);
				RelativeLayout footer = (RelativeLayout) rootView.findViewById(R.id.footer);
				footer.getLayoutParams().height = BaseGame.convertDip2Pixels(getResources(), 70);
			}

			game.addTurnListener(this);

			Drawable guessEnabled = ContextCompat.getDrawable(getActivity(), R.drawable.guess);
			Drawable guessDisabled = ContextCompat.getDrawable(getActivity(), R.drawable.guess_disabled);

			input = (EditText) rootView.findViewById(R.id.guess_input);
			input.addTextChangedListener(new CapsLockLimiter(input, rootView, guessEnabled, guessDisabled));

			rootView.clearFocus();

			((ImageView) rootView.findViewById(R.id.guess_button)).setOnClickListener(new TurnMaker(game, rootView, this));

			((GameLayout) rootView.findViewById(R.id.screen_game)).setActivity(getActivity());

			SwipeController swipe = new SwipeController((BaseGame) getActivity(), gameid);
			ViewPager mPager = (ViewPager) rootView.findViewById(R.id.pager);

			mPager.setPageMargin(BaseGame.convertDip2Pixels(getResources(), 2));
			mPager.setPageMarginDrawable(R.color.divider);

			if (BaseGame.wideLayout) {
				mPager.setOnTouchListener(this);
				rootView.findViewById(R.id.indicator).setVisibility(View.INVISIBLE);
			} else {
				mPager.addOnPageChangeListener(new SwipeListener((ImageView) rootView.findViewById(R.id.indicator)));
			}

			mPager.setAdapter(swipe);

			showKeyboard();
		} else {
			getFragmentManager().popBackStack("top", 0);
		}

		return rootView;
	}

	@Override
	public void onTurnAdded(final Game game, Turn turn) {
		getView().post(new Runnable() {
			@Override
			public void run() {
				if (getView() != null) {
					((TextView) getView().findViewById(R.id.turn)).setText(Integer.toString(game.getTurnNumber()));
				}
			}
		});
	}

	@Override
	public void onGameUpdated(final Game game) {
		getView().post(new Runnable() {
			@Override
			public void run() {
				getView().findViewById(R.id.setword_msg).setVisibility(game.isNeedingWord() ? View.VISIBLE : View.GONE);
			}
		});
		RelativeLayout footer = (RelativeLayout) getView().findViewById(R.id.footer);
		footer.getLayoutParams().height = BaseGame.convertDip2Pixels(getResources(), game.isNeedingWord() ? 70 : 50);
	}

	public void startSpinner() {
		getView().post(new Runnable() {
			@Override
			public void run() {
				getView().findViewById(R.id.turn_progress).setVisibility(View.VISIBLE);
				getView().findViewById(R.id.guess_button).setVisibility(View.GONE);
			}
		});
	}

	public void stopSpinner() {
		getView().post(new Runnable() {
			@Override
			public void run() {
				getView().findViewById(R.id.guess_button).setVisibility(View.VISIBLE);
				getView().findViewById(R.id.turn_progress).setVisibility(View.GONE);
			}
		});
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			v.performClick();
		}

		return true;
	}
}
