package uk.co.thomasc.wordmaster.view.game;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import uk.co.thomasc.wordmaster.BaseGame;
import uk.co.thomasc.wordmaster.R;
import uk.co.thomasc.wordmaster.api.GetTurnsRequestListener;
import uk.co.thomasc.wordmaster.api.ServerAPI;
import uk.co.thomasc.wordmaster.objects.Game;
import uk.co.thomasc.wordmaster.objects.Turn;
import uk.co.thomasc.wordmaster.objects.callbacks.GameListener;
import uk.co.thomasc.wordmaster.view.DialogPanel;
import uk.co.thomasc.wordmaster.view.Errors;
import uk.co.thomasc.wordmaster.view.RussoText;
import uk.co.thomasc.wordmaster.view.menu.MenuDetailFragment;

public class SwipeController extends FragmentStatePagerAdapter {

	private static String gid;
	private float pageWidth = 1.0f;

	public SwipeController(BaseGame fm, String gameid) {
		super(fm.getSupportFragmentManager());
		pageWidth = fm.wideLayout ? 0.5f : 1.0f;
		SwipeController.gid = gameid;
	}

	@Override
	public float getPageWidth(int position) {
		return pageWidth;
	}

	@Override
	public Fragment getItem(int arg0) {
		Fragment fragment = new Pages();
		Bundle args = new Bundle();

		args.putBoolean(Pages.ARG_OBJECT, arg0 == 0);
		args.putString(MenuDetailFragment.ARG_ITEM_ID, SwipeController.gid);
		fragment.setArguments(args);

		return fragment;
	}

	@Override
	public int getCount() {
		return 2;
	}

	public static class Pages extends Fragment implements GameListener, OnScrollListener, OnClickListener {
		public static final String ARG_OBJECT = "object";
		private GameAdapter adapter;
		private Game game;
		private ListView listView;
		private LinearLayout alpha;

		private boolean refreshTriggered = false;

		@Override
		public void onDestroy() {
			super.onDestroy();
			if (game != null) {
				game.removeTurnListener(this);
			}
		}

		@Override
		public void onTurnAdded(final Game game, final Turn turn) {
			if (listView != null) {
				listView.post(new Runnable() {
					@Override
					public void run() {
						listView.setSelection(listView.getAdapter().getCount() - 1);
					}
				});
			}
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView;
			game = Game.getGame(SwipeController.gid);

			game.addTurnListener(this);

			if (getArguments().getBoolean(Pages.ARG_OBJECT)) {
				rootView = new ListView(getActivity());

				((BaseGame) getActivity()).gameAdapter = adapter = game.getAdapter((BaseGame) getActivity());
				listView = (ListView) rootView;
				listView.setAdapter(adapter);
				listView.setBackgroundColor(Color.WHITE);
				listView.setCacheColorHint(Color.WHITE);

				listView.setSelection(listView.getAdapter().getCount() - 1);
				listView.setOnScrollListener(this);
			} else {
				rootView = inflater.inflate(R.layout.alphabet, container, false);

				alpha = (LinearLayout) rootView;
				onGameUpdated(game);
			}
			return rootView;
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {

		}

		@Override
		public void onScroll(AbsListView view, final int firstVisibleItem, int visibleItemCount, final int totalItemCount) {
			if (firstVisibleItem < 2 && !refreshTriggered && game.getPivotOldest() > 0) {
				refreshTriggered = true;
				ServerAPI.getTurns(game.getID(), game.getPivotOldest(), -10, (BaseGame) getActivity(), new GetTurnsRequestListener() {

					@Override
					public void onRequestFailed() {
						refreshTriggered = false;

						final DialogPanel errorMessage = (DialogPanel) getActivity().findViewById(R.id.errorMessage);
						errorMessage.post(new Runnable() {
							@Override
							public void run() {
								errorMessage.show(Errors.NETWORK);
							}
						});
					}

					@Override
					public void onRequestComplete() {
						listView.clearFocus();
						listView.post(new Runnable() {
							@Override
							public void run() {
								listView.setSelection(firstVisibleItem - totalItemCount + adapter.getCount());
							}
						});
						refreshTriggered = false;
					}
				});
			}
		}

		@Override
		public void onClick(View v) {
			RussoText txt = (RussoText) v;
			txt.setTextColor(getResources().getColor(txt.isStrike() ? R.color.maintext : R.color.hiddenletter));
			txt.setStrike(!txt.isStrike());
			game.updateAlpha(txt.getId(), txt.isStrike(), (BaseGame) getActivity());
		}

		@Override
		public void onGameUpdated(final Game game) {
			if (alpha != null) {
				alpha.post(new Runnable() {
					@Override
					public void run() {
						int index = 0;
						for (int i = 0; i < alpha.getChildCount(); i++) {
							LinearLayout child = (LinearLayout) alpha.getChildAt(i);
							for (int j = 0; j < child.getChildCount(); j++) {
								RussoText txt = (RussoText) child.getChildAt(j);
								txt.setOnClickListener(Pages.this);
								txt.setId(index);
								boolean strike = game.getAlpha(index++);
								txt.setTextColor(getResources().getColor(strike ? R.color.hiddenletter : R.color.maintext));
								txt.setStrike(strike);
							}
						}
					}
				});
			}
		}
	}

}
