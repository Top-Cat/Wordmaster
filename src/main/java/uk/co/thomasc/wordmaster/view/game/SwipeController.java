package uk.co.thomasc.wordmaster.view.game;

import java.util.Date;

import uk.co.thomasc.wordmaster.R;
import uk.co.thomasc.wordmaster.objects.Turn;
import uk.co.thomasc.wordmaster.objects.User;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class SwipeController extends FragmentPagerAdapter {

	public SwipeController(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int arg0) {
		Fragment fragment = new Pages();
		Bundle args = new Bundle();
		
		args.putBoolean(Pages.ARG_OBJECT, arg0 == 0);
		fragment.setArguments(args);
		
		return fragment;
	}

	@Override
	public int getCount() {
		return 2;
	}

	public static class Pages extends Fragment {
		public static final String ARG_OBJECT = "object";

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
				rootView = inflater.inflate(R.layout.alphabet, container, false);
			}
			return rootView;
		}
	}
	
}