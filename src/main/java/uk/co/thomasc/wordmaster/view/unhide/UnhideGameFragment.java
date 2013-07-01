package uk.co.thomasc.wordmaster.view.unhide;

import java.util.ArrayList;

import uk.co.thomasc.wordmaster.R;
import uk.co.thomasc.wordmaster.objects.Game;
import uk.co.thomasc.wordmaster.objects.callbacks.GameCreationListener;
import uk.co.thomasc.wordmaster.objects.callbacks.UnhideGameListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class UnhideGameFragment extends Fragment implements OnClickListener, OnItemClickListener {

	public static final String ARG_ID = "hidden_games";
	
	public UnhideAdapter adapter;
	private UnhideGameListener listener;
	private String[] hiddenGames;
	
	public UnhideGameFragment() {
		
	}
	
	@Override
	public void setArguments(Bundle args) {
		this.hiddenGames = args.getStringArray(ARG_ID);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.unhide_game, container, false);
		
		rootView.setOnClickListener(this);
		
		final ListView users = (ListView) rootView.findViewById(R.id.unhide_game);
		adapter = new UnhideAdapter(getActivity());
		users.setAdapter(adapter);
		users.setOnItemClickListener(this);
		
		for (String id : hiddenGames) {
			Game game = Game.getGame(id);
			adapter.add(game);
		}
		
		return rootView;
	}

	@Override
	public void onClick(View v) {
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		listener.onUnhideGame(adapter.getItem(position));
	}
	
	public void setUnhideGameListener(UnhideGameListener listener) {
		this.listener = listener;
	}
	
}
