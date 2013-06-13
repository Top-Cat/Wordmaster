package uk.co.thomasc.wordmaster.view.menu;

import uk.co.thomasc.wordmaster.R;
import uk.co.thomasc.wordmaster.objects.Game;
import uk.co.thomasc.wordmaster.util.BaseGameActivity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MenuDetailFragment extends Fragment {
	public static final String ARG_ITEM_ID = "gameid";
	private Game game;
	private BaseGameActivity act;
	
	public MenuDetailFragment() {
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments().containsKey(ARG_ITEM_ID)) {
			String gameid = getArguments().getString(ARG_ITEM_ID);
		}
	}
	
	@Override
	public void onAttach(Activity activity) {
		this.act = (BaseGameActivity) activity;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.game_screen, container, false);
		
		// TODO: Get game from id and populate view
		//game = ??
		
		return rootView;
	}
}