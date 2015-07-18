package uk.co.thomasc.wordmaster.view.game;

import java.util.Comparator;
import java.util.Locale;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import uk.co.thomasc.wordmaster.BaseGame;
import uk.co.thomasc.wordmaster.R;
import uk.co.thomasc.wordmaster.objects.Turn;
import uk.co.thomasc.wordmaster.objects.User;
import uk.co.thomasc.wordmaster.objects.callbacks.NameLoadedListener;
import uk.co.thomasc.wordmaster.view.TimeSinceText;

public class GameAdapter extends ArrayAdapter<Turn> {

	private BaseGame act;
	final private Comparator<Turn> comp;

	public GameAdapter(BaseGame act) {
		super(act, 0);

		this.act = act;

		comp = new Comparator<Turn>() {
			@Override
			public int compare(Turn e1, Turn e2) {
				return (int) (e1.getUnixTimestamp() - e2.getUnixTimestamp());
			}
		};
	}

	@Override
	public void add(Turn object) {
		super.add(object);
		sort(comp);
	}
	
	@Override
	public int getViewTypeCount() {
		return 5;
	}
	
	@Override
	public int getItemViewType(int position) {
		Turn item = getItem(position);
		if (item.getTurnNum() == 0) {
			return 0;
		} else {
			int v = User.getCurrentUser().equals(item.getUser()) ? 1 : 3;
			if (item.getCorrectLetters() < 4) {
				v++;
			}
			return v;
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;

		final Turn item = getItem(position);
		User user = item.getUser();
		boolean isPlayer = User.getCurrentUser().equals(user);
		final boolean winningTurn = (item.getCorrectLetters() == 4);

		if (view == null) {
			LayoutInflater vi = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			int viewId = 0;
			switch (getItemViewType(position)) {
				case 0:
					viewId = R.layout.game_row_new_round;
					break;
				case 1:
					viewId = R.layout.game_row_win;
					break;
				case 2:
					viewId = R.layout.game_row_big;
					break;
				case 3:
					viewId = R.layout.game_row_lose;
					break;
				case 4:
					viewId = R.layout.game_row_small;
					break;
			}
			
			view = vi.inflate(viewId, parent, false);
		}

		if (item.getTurnNum() == 0) {
			TextView guess = ((TextView) view.findViewById(R.id.guess));
			guess.setText(isPlayer ? "Your word is " + item.getGuess() : "");
			guess.setVisibility(isPlayer ? View.VISIBLE : View.GONE);
		} else {
			if (isPlayer) {
				((TextView) view.findViewById(R.id.guess)).setText(item.getGuess().toUpperCase(Locale.ENGLISH));
			} else {
				final TextView txtview = (TextView) view.findViewById(R.id.guess);
				user.listenForLoad(new NameLoadedListener() {
					@Override
					public void onNameLoaded(String name) {
						String firstName = name.substring(0, name.indexOf(' '));
						if (winningTurn) {
							txtview.setText(firstName + " guessed " + item.getGuess().toUpperCase(Locale.ENGLISH) +
									"\n" + firstName + "'s word was " + item.getOpponentWord().toUpperCase(Locale.ENGLISH));
						} else {
							txtview.setText(firstName + " guessed " + item.getGuess().toUpperCase(Locale.ENGLISH));
						}
					}
				});
			}

			((TimeSinceText) view.findViewById(R.id.time)).setTimestamp(item.getUnixTimestamp());

			int goldPegs = item.getCorrectLetters();
			int silverPegs = item.getDisplacedLetters();

			Drawable gold = ContextCompat.getDrawable(act, R.drawable.goldpeg);
			Drawable silver = ContextCompat.getDrawable(act, R.drawable.silverpeg);
			Drawable white = ContextCompat.getDrawable(act, R.drawable.whitepeg);

			if (!winningTurn) {
				int[] pegs = { R.id.peg0, R.id.peg1, R.id.peg2, R.id.peg3 };
				for (int peg : pegs) {
					if (goldPegs > 0) {
						((ImageView) view.findViewById(peg)).setImageDrawable(gold);
						goldPegs--;
					} else if (silverPegs > 0) {
						((ImageView) view.findViewById(peg)).setImageDrawable(silver);
						silverPegs--;
					} else {
						((ImageView) view.findViewById(peg)).setImageDrawable(white);
					}
				}
				view.setBackgroundResource(R.drawable.itembg);
			} else {
				view.setBackgroundResource(R.drawable.gameendbg);
			}
		}

		return view;
	}

}
