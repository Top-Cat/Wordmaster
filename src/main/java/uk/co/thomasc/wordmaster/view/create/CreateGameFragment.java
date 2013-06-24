package uk.co.thomasc.wordmaster.view.create;

import uk.co.thomasc.wordmaster.BaseGame;
import uk.co.thomasc.wordmaster.R;
import uk.co.thomasc.wordmaster.objects.Game;
import uk.co.thomasc.wordmaster.objects.callbacks.GameCreationListener;
import uk.co.thomasc.wordmaster.util.BaseGameActivity;
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
import com.google.android.gms.plus.model.people.Person.Collection;
import com.google.android.gms.plus.model.people.PersonBuffer;

public class CreateGameFragment extends Fragment implements OnClickListener, OnItemClickListener {

	public PersonAdapter adapter;
	private GameCreationListener listener;
	
	public CreateGameFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.user_picker, container, false);
		
		rootView.setOnClickListener(this);
		
		ListView users = (ListView) rootView.findViewById(R.id.user_picker);
		adapter = new PersonAdapter(getActivity());
		((BaseGameActivity) getActivity()).getPlusClient().loadPeople(new OnPeopleLoadedListener() {
			@Override
			public void onPeopleLoaded(ConnectionResult status, PersonBuffer personBuffer, String nextPageToken) {
				try {
					int count = personBuffer.getCount();
					for (int i = 0; i < count; i++) {
						adapter.add(personBuffer.get(i).freeze());
					}
				} finally {
					personBuffer.close();
				}
			}
		}, Collection.VISIBLE);
		users.setAdapter(adapter);
		users.setOnItemClickListener(this);
		
		return rootView;
	}

	@Override
	public void onClick(View v) {
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		String userID = ((BaseGame) getActivity()).getUserId();
		String oppID = adapter.getItem(position).getId();
		listener.onCreateGame(userID, oppID);
	}
	
	public void setGameCreatedListener(GameCreationListener listener) {
		this.listener = listener;
	}
	
}
