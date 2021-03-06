package uk.co.thomasc.wordmaster.view.unhide;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import uk.co.thomasc.wordmaster.BaseGame;
import uk.co.thomasc.wordmaster.R;
import uk.co.thomasc.wordmaster.objects.Game;

public class UnhideGameFragment extends Fragment implements OnClickListener, OnItemClickListener {

	public static final String TAG = "UnhideGameFragment";
	public static final String ARG_ID = "hidden_games";

	public UnhideAdapter adapter;
	private String[] hiddenGames;

	public UnhideGameFragment() {

	}

	@Override
	public void setArguments(Bundle args) {
		hiddenGames = args.getStringArray(UnhideGameFragment.ARG_ID);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.unhide_game, container, false);

		rootView.setOnClickListener(this);
		rootView.findViewById(R.id.action_close).setOnClickListener(this);

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
		if (v.getId() == R.id.action_close) {
			getFragmentManager().popBackStack("unhide", 1);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		BaseGame.getServerApi().setGameVisible(adapter.getItem(position).getID(), true, null);
		getFragmentManager().popBackStack("unhide", 1);
	}

}
