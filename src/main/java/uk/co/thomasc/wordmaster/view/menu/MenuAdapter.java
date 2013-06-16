package uk.co.thomasc.wordmaster.view.menu;

import java.util.Comparator;

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
import uk.co.thomasc.wordmaster.util.TimeUtil;

public class MenuAdapter extends ArrayAdapter<Game> implements NameLoadedListener, ImageLoadedListener {

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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rview = convertView;

		if (rview == null) {
			LayoutInflater vi = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rview = vi.inflate(R.layout.game_info, null);
		}

		Game item = getItem(position);

		if (item.getOpponent().getName() == null) {
			((TextView) rview.findViewById(R.id.playera)).setText("Loading...");
			item.getOpponent().listenForLoad(this);
		} else {
			((TextView) rview.findViewById(R.id.playera)).setText("vs " + item.getOpponent().getName());
		}
		if (item.getOpponent().getAvatar() == null) {
			item.getOpponent().listenForImage(this);
		} else {
			((ImageView) rview.findViewById(R.id.avatar)).setImageDrawable(item.getOpponent().getAvatar());
		}

		long lastUpdate = item.getLastUpdateTimestamp();
		String mostRecentMove;
		if (lastUpdate > 0) {
			mostRecentMove = TimeUtil.timeSince(lastUpdate);
		} else {
			mostRecentMove = "";
		}
		((TextView) rview.findViewById(R.id.time)).setText(mostRecentMove);

		return rview;
	}

	@Override
	public void onNameLoaded(String name) {
		act.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				notifyDataSetChanged();
			}
		});
	}

	@Override
	public void onImageLoaded(Drawable image) {
		act.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				notifyDataSetChanged();
			}
		});
	}

}
