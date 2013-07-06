package uk.co.thomasc.wordmaster.view.create;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.plus.model.people.Person;

import uk.co.thomasc.wordmaster.R;
import uk.co.thomasc.wordmaster.objects.User;
import uk.co.thomasc.wordmaster.objects.callbacks.ImageLoadedListener;
import uk.co.thomasc.wordmaster.objects.callbacks.NameLoadedListener;
import uk.co.thomasc.wordmaster.util.BaseGameActivity;

public class PersonAdapter extends ArrayAdapter<Person> {

	public static String keySegment = "DB6Fpmlprf0yaYGbkfFh6XvisO25dvfq4mhyfNR5K15Xo9B6kfbnd1qQuO7zhB10ZCZaBZfRpJP5saK/jyRLWOzqi0vQIDAQAB";

	private Activity act;

	public PersonAdapter(Activity act) {
		super(act, 0);

		this.act = act;
	}

	@Override
	public void add(Person object) {
		super.add(object);
	}
	
	@Override
	public int getCount() {
		return super.getCount() + 1;
	}

	private Map<View, User> checkList = new HashMap<View, User>();

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rview = convertView;

		if (rview == null) {
			LayoutInflater vi = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rview = vi.inflate(R.layout.person, null);
		}
		
		final View view = rview;
		
		if (position == 0) {
			((ImageView) view.findViewById(R.id.avatar)).setImageResource(R.drawable.games_matches_green);
			((TextView) view.findViewById(R.id.playera)).setText("Auto Match");
			checkList.remove(view);
		} else {
			Person item = getItem(position - 1);
			
			final User user = User.getUser(item, (BaseGameActivity) act);
			checkList.put(view, user);
	
			user.listenForLoad(new NameLoadedListener() {
				@Override
				public void onNameLoaded(final String name) {
					act.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if (user == checkList.get(view)) {
								((TextView) view.findViewById(R.id.playera)).setText(name);
							}
						}
					});
				}
			});
			user.listenForImage(new ImageLoadedListener() {
				@Override
				public void onImageLoaded(final Drawable image) {
					act.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if (user == checkList.get(view)) {
								((ImageView) view.findViewById(R.id.avatar)).setImageDrawable(image);
							}
						}
					});
				}
			});
		}

		return view;
	}

}
