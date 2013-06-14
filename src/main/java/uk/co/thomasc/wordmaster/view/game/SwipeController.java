package uk.co.thomasc.wordmaster.view.game;

import java.util.Date;

import uk.co.thomasc.wordmaster.R;
import uk.co.thomasc.wordmaster.objects.Turn;
import uk.co.thomasc.wordmaster.objects.User;
import uk.co.thomasc.wordmaster.util.RussoText;
import uk.co.thomasc.wordmaster.view.menu.MenuDetailFragment;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

public class SwipeController extends FragmentStatePagerAdapter {
	
	private String gid;
	
	public SwipeController(FragmentManager fm, String gameid) {
		super(fm);
		this.gid = gameid;
	}

	@Override
	public Fragment getItem(int arg0) {
		Fragment fragment = new Pages();
		Bundle args = new Bundle();
		
		args.putBoolean(Pages.ARG_OBJECT, arg0 == 0);
		args.putString(MenuDetailFragment.ARG_ITEM_ID, gid);
		fragment.setArguments(args);
		
		return fragment;
	}

	@Override
	public int getCount() {
		return 2;
	}

	public static class Pages extends Fragment {
		public static final String ARG_OBJECT = "object";
		private static final String SP_PREF = "WM_ALPHA_";
		private ToggleListener listener = new ToggleListener();
		private SharedPreferences alphaPref;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView;
			if (getArguments().getBoolean(ARG_OBJECT)) {
				rootView = new ListView(getActivity());
				GameAdapter gm = new GameAdapter(getActivity());
				
				gm.add(new Turn(0, new Date(), new User("124", "Adam", Uri.EMPTY), "MEOW", 1, 2));
				gm.add(new Turn(0, new Date(), new User("123", "Josh", Uri.EMPTY), "MEOW", 1, 2));
				
				((ListView) rootView).setAdapter(gm);
			} else {
				alphaPref = getActivity().getSharedPreferences(SP_PREF + getArguments().getString(MenuDetailFragment.ARG_ITEM_ID), 0);
				
				rootView = inflater.inflate(R.layout.alphabet, container, false);
				LinearLayout root = (LinearLayout) rootView;
				for (int i = 0; i < root.getChildCount(); i++) {
					LinearLayout child = (LinearLayout) root.getChildAt(i);
					for (int j = 0; j < child.getChildCount(); j++) {
						RussoText txt = (RussoText) child.getChildAt(j);
						txt.setOnClickListener(listener);
						boolean strike = alphaPref.getBoolean(txt.getText().toString(), false);
						txt.setTextColor(getResources().getColor(strike ? R.color.hiddenletter : R.color.maintext));
						txt.setStrike(strike);
					}
				}
			}
			return rootView;
		}
		
		private class ToggleListener implements OnClickListener {

			@Override
			public void onClick(View v) {
				RussoText txt = (RussoText) v;
				txt.setTextColor(getResources().getColor(txt.isStrike() ? R.color.maintext : R.color.hiddenletter));
				txt.setStrike(!txt.isStrike());
				Editor edit = alphaPref.edit();
				edit.putBoolean(txt.getText().toString(), txt.isStrike());
				edit.commit();
			}
			
		}
	}
	
}