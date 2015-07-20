package uk.co.thomasc.wordmaster.view.create;

import java.util.HashMap;
import java.util.Map;

import com.google.android.gms.plus.model.people.Person;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import uk.co.thomasc.wordmaster.R;
import uk.co.thomasc.wordmaster.objects.User;
import uk.co.thomasc.wordmaster.objects.callbacks.UserListener;
import uk.co.thomasc.wordmaster.view.AvatarView;

public class PersonAdapter extends ArrayAdapter<Person> {

	public static String keySegment = "DB6Fpmlprf0yaYGbkfFh6XvisO25dvfq4mhyfNR5K15Xo9B6kfbnd1qQuO7zhB10ZCZaBZfRpJP5saK/jyRLWOzqi0vQIDAQAB";

	private final Context c;
	private final CreateGameFragment fragment;

	public PersonAdapter(Context c, CreateGameFragment fragment) {
		super(c, 0);

		this.c = c;
		this.fragment = fragment;
	}

	@Override
	public void add(Person object) {
		super.add(object);
	}

	@Override
	public int getCount() {
		return super.getCount() + (fragment.nextPage == null ? 1 : 2);
	}

	private final Map<View, User> checkList = new HashMap<View, User>();

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rview = convertView;

		if (rview == null) {
			LayoutInflater vi = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rview = vi.inflate(R.layout.person, parent, false);
		}

		final View view = rview;
		((ImageView) view.findViewById(R.id.avatar)).setVisibility(View.VISIBLE);

		if (position == 0) {
			((ImageView) view.findViewById(R.id.avatar)).setImageResource(R.drawable.games_matches_green);
			((TextView) view.findViewById(R.id.playera)).setText("Auto Match");
			checkList.remove(view);
		} else if (position == getCount() - 1 && fragment.nextPage != null) {
			((ImageView) view.findViewById(R.id.avatar)).setVisibility(View.INVISIBLE);
			((TextView) view.findViewById(R.id.playera)).setText("More...");
			checkList.remove(view);
		} else {
			Person item = getItem(position - 1);

			User user = User.getUser(item);
			checkList.put(view, user);

			user.addListener(new UserListener() {
				@Override
				public void onNameLoaded(final User user) {
					view.post(new Runnable() {
						@Override
						public void run() {
							if (user == checkList.get(view)) {
								((TextView) view.findViewById(R.id.playera)).setText(user.getName());
							}
						}
					});
				}

				@Override
				public void onImageLoaded(User user) {

				}
			});
			((AvatarView) view.findViewById(R.id.avatar)).setUser(user);
		}

		return view;
	}

}
