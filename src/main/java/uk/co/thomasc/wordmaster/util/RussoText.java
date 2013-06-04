package uk.co.thomasc.wordmaster.util;

import uk.co.thomasc.wordmaster.BaseGame;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class RussoText extends TextView {
	
	public RussoText(Context context) {
		super(context);
		this.setTypeface(BaseGame.russo);
	}
	
	public RussoText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.setTypeface(BaseGame.russo);
	}
	
	public RussoText(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setTypeface(BaseGame.russo);
	}
	
}