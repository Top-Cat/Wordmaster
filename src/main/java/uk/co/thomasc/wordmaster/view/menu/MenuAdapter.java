package uk.co.thomasc.wordmaster.view.menu;

import java.util.Comparator;
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

import uk.co.thomasc.wordmaster.R;
import uk.co.thomasc.wordmaster.objects.Game;
import uk.co.thomasc.wordmaster.objects.User;
import uk.co.thomasc.wordmaster.objects.callbacks.ImageLoadedListener;
import uk.co.thomasc.wordmaster.objects.callbacks.NameLoadedListener;
import uk.co.thomasc.wordmaster.util.TimeUtil;

public class MenuAdapter extends ArrayAdapter<Game> {

	private Activity act;
	final private Comparator<Game> comp;

	public MenuAdapter(Activity act) {
		super(act, 0);

		this.act = act;

		comp = new Comparator<Game>() {
			@Override
			public int compare(Game e1, Game e2) {
				//TODO: Sort!
				return e1.hashCode() > e2.hashCode() ? 0 : 1;
			}
		};
	}

	@Override
	public void add(Game object) {
		super.add(object);
		sort(comp);
	}
	
	private Map<View, User> checkList = new HashMap<View, User>();

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rview = convertView;

		if (rview == null) {
			LayoutInflater vi = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rview = vi.inflate(R.layout.game_info, null);
		}

		final View view = rview;
		final Game item = getItem(position);
		checkList.put(view, item.getOpponent());

		((TextView) rview.findViewById(R.id.playera)).setText("Loading...");
		item.getOpponent().listenForLoad(new NameLoadedListener() {
			@Override
			public void onNameLoaded(final String name) {
				act.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (item.getOpponent() == checkList.get(view)) {
							((TextView) view.findViewById(R.id.playera)).setText("vs " + name);
						}
					}
				});
			}
		});
		item.getOpponent().listenForImage(new ImageLoadedListener() {
			@Override
			public void onImageLoaded(final Drawable image) {
				act.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (item.getOpponent() == checkList.get(view)) {
							((ImageView) view.findViewById(R.id.avatar)).setImageDrawable(image);
						}
					}
				});
			}
		});

		long lastUpdate = item.getLastUpdateTimestamp();
		String mostRecentMove;
		if (lastUpdate > 0) {
			mostRecentMove = TimeUtil.timeSince(lastUpdate);
		} else {
			mostRecentMove = "";
		}
		((TextView) view.findViewById(R.id.time)).setText(mostRecentMove);

		return view;
	}

}
