package uk.co.thomasc.wordmaster;

import uk.co.thomasc.wordmaster.util.BaseGameActivity;
import uk.co.thomasc.wordmaster.view.menu.MenuDetailFragment;

import android.os.Bundle;

public class PlayActivity extends BaseGameActivity {
	
	public String gid;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
				
		setContentView(R.layout.empty_screen);
		
		overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
		
		if (arg0 == null) {
			Bundle args = new Bundle();
			gid = getIntent().getStringExtra(MenuDetailFragment.ARG_ITEM_ID);
			args.putString(MenuDetailFragment.ARG_ITEM_ID, gid);
			MenuDetailFragment fragment = new MenuDetailFragment();
			fragment.setArguments(args);
			getSupportFragmentManager().beginTransaction().add(R.id.empty, fragment).commit();
		}
	}
	
	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.slide_left_2, R.anim.slide_right_2);
	}

	@Override
	public void onSignInFailed() {
		
	}

	@Override
	public void onSignInSucceeded() {
		
	}
	
}