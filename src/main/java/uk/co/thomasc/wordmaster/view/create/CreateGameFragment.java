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

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.People.LoadPeopleResult;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.PersonBuffer;

import uk.co.thomasc.wordmaster.BaseGame;
import uk.co.thomasc.wordmaster.R;
import uk.co.thomasc.wordmaster.objects.User;
import uk.co.thomasc.wordmaster.objects.callbacks.GameCreationListener;
import uk.co.thomasc.wordmaster.view.menu.MenuAdapter;

public class CreateGameFragment extends Fragment implements OnClickListener, OnItemClickListener {
	
	public PersonAdapter adapter;
	private GameCreationListener listener;
	private ResultCallback<LoadPeopleResult> peopleListener;
	String nextPage = null;

	public CreateGameFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.user_picker, container, false);

		rootView.setOnClickListener(this);
		rootView.findViewById(R.id.action_close).setOnClickListener(this);

		final ListView users = (ListView) rootView.findViewById(R.id.user_picker);
		adapter = new PersonAdapter(getActivity(), this);
		peopleListener = new ResultCallback<LoadPeopleResult>() {
			@Override
			public void onResult(LoadPeopleResult arg0) {
				Set<String> existingOpponents = new HashSet<String>();
				MenuAdapter menuAdapter = ((BaseGame) getActivity()).menuFragment.adapter;
				for (int i = 0; i < menuAdapter.getCount(); i++) {
					User opponent = menuAdapter.getItem(i).getOpponent();
					if (opponent != null) {
						existingOpponents.add(opponent.getPlusID());
					}
				}
				
				PersonBuffer personBuffer = arg0.getPersonBuffer();
				int count = 0;
				try {
					count = personBuffer.getCount();
					for (int i = 0; i < count; i++) {
						Person person = personBuffer.get(i).freeze();
						if (!existingOpponents.contains(person.getId())) {
							adapter.add(personBuffer.get(i).freeze());
						}
					}
				} finally {
					personBuffer.release();
					adapter.notifyDataSetChanged();
					nextPage = arg0.getNextPageToken();
				}
			}
		};
		Plus.PeopleApi.loadVisible(((BaseGame) getActivity()).getApiClient(), People.OrderBy.BEST, nextPage).setResultCallback(peopleListener);
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
		if (position == 0) {
			listener.onCreateGame(null);
		} else if (position == adapter.getCount() - 1 && nextPage != null) {
			Plus.PeopleApi.loadVisible(((BaseGame) getActivity()).getApiClient(), People.OrderBy.BEST, nextPage).setResultCallback(peopleListener);
			nextPage = null;
		} else {
			String oppID = adapter.getItem(position - 1).getId();
			listener.onCreateGame(oppID);
		}
	}

	public void setGameCreatedListener(GameCreationListener listener) {
		this.listener = listener;
	}

}
