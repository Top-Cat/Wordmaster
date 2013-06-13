package uk.co.thomasc.wordmaster.view.menu;

import uk.co.thomasc.wordmaster.BaseGame;
import uk.co.thomasc.wordmaster.R;
import uk.co.thomasc.wordmaster.objects.Game;
import uk.co.thomasc.wordmaster.util.BaseGameActivity;
import uk.co.thomasc.wordmaster.util.CapsLockLimiter;
import uk.co.thomasc.wordmaster.view.game.GameLayout;
import uk.co.thomasc.wordmaster.view.game.SwipeController;
import uk.co.thomasc.wordmaster.view.game.SwipeListener;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MenuDetailFragment extends Fragment {
	
	public static final String ARG_ITEM_ID = "gameid";
	private Game game;
	private BaseGameActivity act;
	private String gameid;
	private EditText input;
	
	public MenuDetailFragment() {
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments().containsKey(ARG_ITEM_ID)) {
			gameid = getArguments().getString(ARG_ITEM_ID);
		}
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.act = (BaseGameActivity) activity;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.game_screen, container, false);
		
		game = BaseGame.gameForGameID(gameid);
		// TODO: populate view
		
		
		input = (EditText) rootView.findViewById(R.id.editText1);
		input.addTextChangedListener(new CapsLockLimiter(input));
		
		((GameLayout) rootView.findViewById(R.id.screen_game)).setActivity(act);
		
		SwipeController swipe = new SwipeController(act.getSupportFragmentManager());
		ViewPager mPager = ((ViewPager) rootView.findViewById(R.id.pager));
		mPager.setAdapter(swipe);
		mPager.setOnPageChangeListener(new SwipeListener((ImageView) rootView.findViewById(R.id.indicator)));
		
		return rootView;
	}
}