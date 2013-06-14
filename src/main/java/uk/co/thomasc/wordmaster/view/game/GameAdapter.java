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
import android.sax.RootElement;
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
		boolean isPlayer = user.getPlusID().equals(((BaseGame) act).getUserId());
		
		if (view == null) {
			LayoutInflater vi = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (isPlayer) {
				view = vi.inflate(R.layout.game_row_big, null);
			} else {
				view = vi.inflate(R.layout.game_row_small, null);
			}
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
		
		if (goldPegs == 4) {
			((ImageView) view.findViewById(R.id.peg0)).setImageDrawable(gold);
			((ImageView) view.findViewById(R.id.peg1)).setImageDrawable(gold);
			((ImageView) view.findViewById(R.id.peg2)).setImageDrawable(gold);
			((ImageView) view.findViewById(R.id.peg3)).setImageDrawable(gold);
		} else if (goldPegs == 3) {
			((ImageView) view.findViewById(R.id.peg0)).setImageDrawable(gold);
			((ImageView) view.findViewById(R.id.peg1)).setImageDrawable(gold);
			((ImageView) view.findViewById(R.id.peg2)).setImageDrawable(gold);
			if (silverPegs == 1) {
				((ImageView) view.findViewById(R.id.peg3)).setImageDrawable(silver);
			} else {
				((ImageView) view.findViewById(R.id.peg3)).setImageDrawable(white);
			}
		} else if (goldPegs == 2) {
			((ImageView) view.findViewById(R.id.peg0)).setImageDrawable(gold);
			((ImageView) view.findViewById(R.id.peg1)).setImageDrawable(gold);
			if (silverPegs == 2) {
				((ImageView) view.findViewById(R.id.peg2)).setImageDrawable(silver);
				((ImageView) view.findViewById(R.id.peg3)).setImageDrawable(silver);
			} else if (silverPegs == 1) {
				((ImageView) view.findViewById(R.id.peg2)).setImageDrawable(silver);
				((ImageView) view.findViewById(R.id.peg3)).setImageDrawable(white);
			} else {
				((ImageView) view.findViewById(R.id.peg2)).setImageDrawable(white);
				((ImageView) view.findViewById(R.id.peg3)).setImageDrawable(silver);
			}
		} else if (goldPegs == 1) {
			((ImageView) view.findViewById(R.id.peg0)).setImageDrawable(gold);
			if (silverPegs == 3) {
				((ImageView) view.findViewById(R.id.peg1)).setImageDrawable(silver);
				((ImageView) view.findViewById(R.id.peg2)).setImageDrawable(silver);
				((ImageView) view.findViewById(R.id.peg3)).setImageDrawable(silver);
			} else if (silverPegs == 2) {
				((ImageView) view.findViewById(R.id.peg1)).setImageDrawable(silver);
				((ImageView) view.findViewById(R.id.peg2)).setImageDrawable(silver);
				((ImageView) view.findViewById(R.id.peg3)).setImageDrawable(white);
			} else if (silverPegs == 1) {
				((ImageView) view.findViewById(R.id.peg1)).setImageDrawable(silver);
				((ImageView) view.findViewById(R.id.peg2)).setImageDrawable(white);
				((ImageView) view.findViewById(R.id.peg3)).setImageDrawable(white);
			} else {
				((ImageView) view.findViewById(R.id.peg1)).setImageDrawable(white);
				((ImageView) view.findViewById(R.id.peg2)).setImageDrawable(white);
				((ImageView) view.findViewById(R.id.peg3)).setImageDrawable(white);
			}
		} else {
			((ImageView) view.findViewById(R.id.peg0)).setImageDrawable(white);
			((ImageView) view.findViewById(R.id.peg1)).setImageDrawable(white);
			((ImageView) view.findViewById(R.id.peg2)).setImageDrawable(white);
			((ImageView) view.findViewById(R.id.peg3)).setImageDrawable(white);
		}

		return view;
	}

}
