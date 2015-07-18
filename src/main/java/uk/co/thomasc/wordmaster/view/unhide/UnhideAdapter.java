package uk.co.thomasc.wordmaster.view.unhide;

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
import uk.co.thomasc.wordmaster.objects.callbacks.ImageLoadedListener;
import uk.co.thomasc.wordmaster.objects.callbacks.NameLoadedListener;

public class UnhideAdapter extends ArrayAdapter<Game> {

	private final Activity act;

	public UnhideAdapter(Activity act) {
		super(act, 0);

		this.act = act;
	}

	@Override
	public void add(Game object) {
		super.add(object);
	}

	private final Map<View, Game> checkList = new HashMap<View, Game>();

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rview = convertView;

		final Game item = getItem(position);

		if (rview == null) {
			LayoutInflater vi = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rview = vi.inflate(R.layout.person, parent, false);
		}

		final View view = rview;
		checkList.put(view, item);

		item.getOpponent().listenForLoad(new NameLoadedListener() {
			@Override
			public void onNameLoaded(final String name) {
				view.post(new Runnable() {
					@Override
					public void run() {
						if (item == checkList.get(view)) {
							((TextView) view.findViewById(R.id.playera)).setText(name);
						}
					}
				});
			}
		});
		item.getOpponent().listenForImage(new ImageLoadedListener() {
			@Override
			public void onImageLoaded(final Drawable image) {
				view.post(new Runnable() {
					@Override
					public void run() {
						if (item == checkList.get(view)) {
							((ImageView) view.findViewById(R.id.avatar)).setImageDrawable(image);
						}
					}
				});
			}
		});

		return view;
	}

}
