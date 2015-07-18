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
				int r = (e2.isTurn() ? 1 : 0) - (e1.isTurn() ? 1 : 0);
				return r != 0 ? r : ((e2.getLastUpdateTimestamp() - e1.getLastUpdateTimestamp()) > 0 ? 1 : -1) * (e1.isPlayersTurn() ? -1 : 1);
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
		return 1;
	}
	
	@Override
	public int getItemViewType(int position) {
		return 0;
	}
	
	private Map<View, Game> checkList = new HashMap<View, Game>();

	private View newView(int position, ViewGroup parent) {
		LayoutInflater vi = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = vi.inflate(R.layout.game_info, parent, false);
		((TextView) view.findViewById(R.id.playera)).setText("Loading...");
		
		return view;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Game item = getItem(position);
		final View view = convertView == null ? newView(position, parent) : convertView;

		checkList.put(view, item);
		
		if (item.getID().equals(selectedGid)) {
			view.setBackgroundResource(R.drawable.selectedbg);
			((TextView) view.findViewById(R.id.playera)).setTextColor(act.getResources().getColor(R.color.selectedtext));
			((TextView) view.findViewById(R.id.time)).setTextColor(act.getResources().getColor(R.color.selectedtext));
		} else {
			view.setBackgroundResource(R.drawable.itembg);
			((TextView) view.findViewById(R.id.playera)).setTextColor(act.getResources().getColor(R.color.maintext));
			((TextView) view.findViewById(R.id.time)).setTextColor(act.getResources().getColor(R.color.maintext));
		}

		if (item.getOpponent() == null) {
			((TextView) view.findViewById(R.id.playera)).setText("Auto Match In Progress");
			((ImageView) view.findViewById(R.id.avatar)).setImageResource(R.drawable.games_matches_green);
		} else {
			item.getOpponent().listenForLoad(new NameLoadedListener() {
				@Override
				public void onNameLoaded(final String name) {
					view.post(new Runnable() {
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
		}

		long time = item.getLastUpdateTimestamp();
		if (time != 0) {
			view.findViewById(R.id.time).setVisibility(View.VISIBLE);
			((TimeSinceText) view.findViewById(R.id.time)).setTimestamp(time);
		} else {
			view.findViewById(R.id.time).setVisibility(View.GONE);
		}

		view.findViewById(R.id.turnindicator).setVisibility(item.isTurn() ? View.VISIBLE : View.GONE);

		return view;
	}

}
