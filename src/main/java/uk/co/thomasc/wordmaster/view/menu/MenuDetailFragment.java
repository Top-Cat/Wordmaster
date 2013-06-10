package uk.co.thomasc.wordmaster.view.menu;

import uk.co.thomasc.wordmaster.R;
import uk.co.thomasc.wordmaster.objects.Game;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MenuDetailFragment extends Fragment {
	public static final String ARG_ITEM_ID = "gameid";
	private Game game;
	
	public MenuDetailFragment() {
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments().containsKey(ARG_ITEM_ID)) {
			String gameid = getArguments().getString(ARG_ITEM_ID);
			// TODO: Get game from id
			//game = ??
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.game_screen, container, false);
		
		if (game != null) {
			//TODO: Populate view
		}
		return rootView;
	}
}