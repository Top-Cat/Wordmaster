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
import uk.co.thomasc.wordmaster.objects.callbacks.ImageLoadedListener;
import uk.co.thomasc.wordmaster.objects.callbacks.NameLoadedListener;
import uk.co.thomasc.wordmaster.view.TimeSinceText;

public class MenuAdapter extends ArrayAdapter<Game> {

	private Activity act;
	private String selectedGid = "";
	final private Comparator<Game> comp;

	public MenuAdapter(Activity act) {
		super(act, 0);

		this.act = act;

		comp = new Comparator<Game>() {
			@Override
			public int compare(Game e1, Game e2) {
				int e1v = (e1.isPlayersTurn() ? 1 : 0) | (e1.needsWord() ? 2 : 0);
				int e2v = (e2.isPlayersTurn() ? 1 : 0) | (e2.needsWord() ? 2 : 0);
				int r = e2v - e1v;
				if (r == 0) {
					return (int) (e2.getLastUpdateTimestamp() - e1.getLastUpdateTimestamp());
				}
				return r;
			}
		};
	}
	
	public void setSelectedGid(String selectedGid) {
		this.selectedGid = selectedGid;
		act.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				notifyDataSetChanged();
			}
		});
	}

	@Override
	public void add(Game object) {
		super.add(object);
		sort(comp);
	}

	@Override
	public boolean isEnabled(int position) {
		return getItem(position).getOpponent() != null;
	}
	
	@Override
	public int getViewTypeCount() {
		return 2;
	}
	
	@Override
	public int getItemViewType(int position) {
		return getItem(position).getID().equals(selectedGid) ? 0 : 1;
	}
	
	private Map<View, Game> checkList = new HashMap<View, Game>();

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rview = convertView;

		final Game item = getItem(position);

		if (rview == null) {
			LayoutInflater vi = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			int viewId = 0;
			switch (getItemViewType(position)) {
				case 0:
					viewId = R.layout.game_info_selected;
					break;
				case 1:
					viewId = R.layout.game_info;
					break;
			}
			
			rview = vi.inflate(viewId, null);
		}

		final View view = rview;

		checkList.put(view, item);

		if (item.getOpponent() == null) {
			((TextView) rview.findViewById(R.id.playera)).setText("Auto Match In Progress");
			((ImageView) view.findViewById(R.id.avatar)).setImageResource(R.drawable.games_matches_green);
		} else {
			((TextView) rview.findViewById(R.id.playera)).setText("Loading...");
			item.getOpponent().listenForLoad(new NameLoadedListener() {
				@Override
				public void onNameLoaded(final String name) {
					act.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if (item == checkList.get(view)) {
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
							if (item == checkList.get(view)) {
								((ImageView) view.findViewById(R.id.avatar)).setImageDrawable(image);
							}
						}
					});
				}
			});
		}

		long time = item.getLastUpdateTimestamp();
		if (time != 0) {
			view.findViewById(R.id.time).setVisibility(View.VISIBLE);
			((TimeSinceText) view.findViewById(R.id.time)).setTimestamp(time);
		} else {
			view.findViewById(R.id.time).setVisibility(View.GONE);
		}

		view.findViewById(R.id.turnindicator).setVisibility(item.isPlayersTurn() || item.needsWord() ? View.VISIBLE : View.GONE);

		return view;
	}

}
