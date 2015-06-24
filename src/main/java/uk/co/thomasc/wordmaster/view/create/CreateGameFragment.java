package uk.co.thomasc.wordmaster.view.create;

import java.util.HashSet;
import java.util.Set;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.PlayerBuffer;
import com.google.android.gms.games.Players.LoadPlayersResult;

import uk.co.thomasc.wordmaster.BaseGame;
import uk.co.thomasc.wordmaster.R;
import uk.co.thomasc.wordmaster.objects.callbacks.GameCreationListener;
import uk.co.thomasc.wordmaster.util.BaseGameActivity;
import uk.co.thomasc.wordmaster.view.menu.MenuAdapter;

public class CreateGameFragment extends Fragment implements OnClickListener, OnItemClickListener {

	private static int FRIENDS_PER_PAGE = 10;
	
	public PersonAdapter adapter;
	private GameCreationListener listener;
	private ResultCallback<LoadPlayersResult> peopleListener;
	boolean nextPage = true;

	public CreateGameFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.user_picker, container, false);

		rootView.setOnClickListener(this);
		rootView.findViewById(R.id.action_close).setOnClickListener(this);

		final ListView users = (ListView) rootView.findViewById(R.id.user_picker);
		adapter = new PersonAdapter(getActivity(), this);
		peopleListener = new ResultCallback<LoadPlayersResult>() {
			@Override
			public void onResult(LoadPlayersResult arg0) {
				Set<String> existingOpponents = new HashSet<String>();
				MenuAdapter menuAdapter = ((BaseGame) getActivity()).menuFragment.adapter;
				for (int i = 0; i < menuAdapter.getCount(); i++) {
					existingOpponents.add(menuAdapter.getItem(i).getOpponent().getPlusID());
				}
				
				PlayerBuffer personBuffer = arg0.getPlayers();
				int count = 0;
				try {
					count = personBuffer.getCount();
					for (int i = 0; i < count; i++) {
						Player person = personBuffer.get(i).freeze();
						if (!existingOpponents.contains(person.getPlayerId())) {
							adapter.add(personBuffer.get(i).freeze());
						}
					}
				} finally {
					personBuffer.close();
					adapter.notifyDataSetChanged();
					if (count == FRIENDS_PER_PAGE) {
						nextPage = false;
					}
				}
			}
		};
		PendingResult<LoadPlayersResult> result = Games.Players.loadInvitablePlayers(((BaseGameActivity) getActivity()).getApiClient(), FRIENDS_PER_PAGE, false);
		result.setResultCallback(peopleListener);
		users.setAdapter(adapter);
		users.setOnItemClickListener(this);

		return rootView;
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.action_close) {
			getActivity().getSupportFragmentManager().popBackStack("userpicker", 1);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		String userID = ((BaseGame) getActivity()).getUserId();
		if (position == 0) {
			listener.onCreateGame(userID, null);
		} else if (position == adapter.getCount() - 1) {
			if (!nextPage) {
				Games.Players.loadMoreInvitablePlayers(((BaseGameActivity) getActivity()).getApiClient(), FRIENDS_PER_PAGE).setResultCallback(peopleListener);
				nextPage = true;
			}
		} else {
			String oppID = adapter.getItem(position - 1).getPlayerId();
			listener.onCreateGame(userID, oppID);
		}
	}

	public void setGameCreatedListener(GameCreationListener listener) {
		this.listener = listener;
	}

}
