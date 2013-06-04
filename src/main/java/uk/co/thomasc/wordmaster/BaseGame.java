package uk.co.thomasc.wordmaster;

import android.content.Context;
import android.graphics.Typeface;

import uk.co.thomasc.wordmaster.util.SystemUiHider;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class BaseGame extends Activity {
	
	public static Typeface russo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		russo = Typeface.createFromAsset(getAssets(), "fonts/Russo_One.ttf");

		setContentView(R.layout.activity_fullscreen);
		
		final EditText input = (EditText) findViewById(R.id.editText1);
		((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(input, InputMethodManager.SHOW_FORCED);
		getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		input.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			
			@Override
			public void afterTextChanged(Editable s) {
				char[] str = s.toString().toCharArray();
				String fin = "";
				for (char c : str) {
					if (c >= 97 && c <= 122) {
						c -= 32;
					}
					if (c >= 65 && c <= 90) {
						fin += c;
					}
				}
				if (!fin.equals(s.toString())) {
					input.setText(fin);
					input.setSelection(input.length());
				}
				System.out.println(fin);
			}
		});
	}
}
