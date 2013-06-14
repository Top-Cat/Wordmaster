package uk.co.thomasc.wordmaster.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class CapsLockLimiter implements TextWatcher {

	private EditText input;

	public CapsLockLimiter(EditText input) {
		this.input = input;
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
		}
		System.out.println(fin);
	}

}
