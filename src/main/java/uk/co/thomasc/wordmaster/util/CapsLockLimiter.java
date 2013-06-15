package uk.co.thomasc.wordmaster.util;

import uk.co.thomasc.wordmaster.R;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class CapsLockLimiter implements TextWatcher {

	private EditText input;
	private View rootView;

	public CapsLockLimiter(EditText input, View rootView) {
		this.input = input;
		this.rootView = rootView;
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}

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
		} else {
			for (int i = 0; i < 4; i++) {
				((TextView) rootView.findViewById(R.id.char0 + i)).setText(i >= fin.length() ? "" : fin.substring(i, i + 1));
			}
		}
		System.out.println(fin);
	}

}
