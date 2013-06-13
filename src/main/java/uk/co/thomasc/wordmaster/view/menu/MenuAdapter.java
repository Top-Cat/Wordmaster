package uk.co.thomasc.wordmaster.view.menu;

import java.util.Comparator;

import uk.co.thomasc.wordmaster.R;
import uk.co.thomasc.wordmaster.objects.Game;
import uk.co.thomasc.wordmaster.objects.User;
import uk.co.thomasc.wordmaster.objects.UserLoadedListener;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MenuAdapter extends ArrayAdapter<Game> {

	private Activity act;
	final private Comparator<Game> comp;
	
	public MenuAdapter(Activity act) {
		super(act, 0);
		
		this.act = act;
		
		comp = new Comparator<Game>() {
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
		
		final View view = rview;
		final Game item = getItem(position);
		
		((TextView) rview.findViewById(R.id.playera)).setText("Loading...");
		item.getOpponent().getNameBlocking(new UserLoadedListener() {
			@Override
			public void onUserLoaded(final User user) {
				act.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						((TextView) view.findViewById(R.id.playera)).setText("vs " + user.getName());
					}
				});
			}
		});
		
		String mostRecentMove = "2m";
		((TextView) view.findViewById(R.id.time)).setText(mostRecentMove);
		
		return rview;
	}
	
}