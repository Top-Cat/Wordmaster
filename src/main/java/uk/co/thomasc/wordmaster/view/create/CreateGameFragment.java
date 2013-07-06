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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.plus.PlusClient.OnPeopleLoadedListener;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.Person.Collection;
import com.google.android.gms.plus.model.people.PersonBuffer;

import uk.co.thomasc.wordmaster.BaseGame;
import uk.co.thomasc.wordmaster.R;
import uk.co.thomasc.wordmaster.objects.callbacks.GameCreationListener;
import uk.co.thomasc.wordmaster.util.BaseGameActivity;
import uk.co.thomasc.wordmaster.view.menu.MenuAdapter;

public class CreateGameFragment extends Fragment implements OnClickListener, OnItemClickListener {

	public PersonAdapter adapter;
	private GameCreationListener listener;

	public CreateGameFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.user_picker, container, false);

		rootView.setOnClickListener(this);
		rootView.findViewById(R.id.action_close).setOnClickListener(this);

		final ListView users = (ListView) rootView.findViewById(R.id.user_picker);
		adapter = new PersonAdapter(getActivity());
		((BaseGameActivity) getActivity()).getPlusClient().loadPeople(new OnPeopleLoadedListener() {
			@Override
			public void onPeopleLoaded(ConnectionResult status, PersonBuffer personBuffer, String nextPageToken) {
				try {
					Set<String> existingOpponents = new HashSet<String>();
					MenuAdapter menuAdapter = ((BaseGame) getActivity()).menuFragment.adapter;
					for (int i = 0; i < menuAdapter.getCount(); i++) {
						existingOpponents.add(menuAdapter.getItem(i).getOpponent().getPlusID());
					}
					
					int count = personBuffer.getCount();
					for (int i = 0; i < count; i++) {
						Person person = personBuffer.get(i).freeze();
						if (!existingOpponents.contains(person.getId())) {
							adapter.add(personBuffer.get(i).freeze());
						}
					}
				} finally {
					personBuffer.close();
				}
			}
		}, Collection.VISIBLE, Person.OrderBy.BEST, 50, null);
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
		} else {
			String oppID = adapter.getItem(position - 1).getId();
			listener.onCreateGame(userID, oppID);
		}
	}

	public void setGameCreatedListener(GameCreationListener listener) {
		this.listener = listener;
	}

}
