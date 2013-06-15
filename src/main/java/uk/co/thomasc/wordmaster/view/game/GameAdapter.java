package uk.co.thomasc.wordmaster.view.game;

import java.util.Comparator;

import uk.co.thomasc.wordmaster.BaseGame;
import uk.co.thomasc.wordmaster.R;
import uk.co.thomasc.wordmaster.objects.Game;
import uk.co.thomasc.wordmaster.objects.Turn;
import uk.co.thomasc.wordmaster.objects.User;
import uk.co.thomasc.wordmaster.util.TimeUtil;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GameAdapter extends ArrayAdapter<Turn> {

	private Activity act;
	final private Comparator<Turn> comp;
	
	private Game game;

	public GameAdapter(Activity act) {
		super(act, 0);

		this.act = act;

		comp = new Comparator<Turn>() {
			@Override
			public int compare(Turn e1, Turn e2) {
				return e1.getUnixTimestamp() == e2.getUnixTimestamp() ? 0 : e1.getUnixTimestamp() > e2.getUnixTimestamp() ? 1 : -1;
			}
		};
	}

	@Override
	public void add(Turn object) {
		super.add(object);
		sort(comp);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		
		Turn item = getItem(position);
		User user = item.getUser();
		boolean isPlayer = (user.getPlusID().equals(((BaseGame) act).getUserId()));
		boolean winningTurn = (item.getCorrectLetters() == 4);
		int viewId = isPlayer ? R.layout.game_row_big : R.layout.game_row_small;
		
		if (view == null || view.getId() != viewId) {
			LayoutInflater vi = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = vi.inflate(viewId, null);
		}
		
		if (isPlayer) {
			((TextView) view.findViewById(R.id.guess)).setText(item.getGuess());
		} else {
			String firstName = user.getName().substring(0, user.getName().indexOf(' '));
			((TextView) view.findViewById(R.id.guess)).setText(firstName + " guessed " + item.getGuess());
		}
		
		String timeSince = TimeUtil.timeSince(item.getUnixTimestamp());
		((TextView) view.findViewById(R.id.time)).setText(timeSince);
		
		int goldPegs = item.getCorrectLetters();
		int silverPegs = item.getDisplacedLetters();
		
		Resources res = act.getResources();
		Drawable gold = res.getDrawable(R.drawable.goldpeg);
		Drawable silver = res.getDrawable(R.drawable.silverpeg);
		Drawable white = res.getDrawable(R.drawable.whitepeg);
		
		for (int peg = R.id.peg0; peg <= R.id.peg3; peg++) {
			if (goldPegs > 0) {
				((ImageView) view.findViewById(peg)).setImageDrawable(gold);
				goldPegs --;
			} else if (silverPegs > 0) {
				((ImageView) view.findViewById(peg)).setImageDrawable(silver);
				silverPegs --;
			} else {
				((ImageView) view.findViewById(peg)).setImageDrawable(white);
			}
		}

		return view;
	}

}
