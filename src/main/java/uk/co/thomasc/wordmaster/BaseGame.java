package uk.co.thomasc.wordmaster;

import android.graphics.Typeface;

import com.google.android.gms.common.SignInButton;

import uk.co.thomasc.wordmaster.util.BaseGameActivity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class BaseGame extends BaseGameActivity implements OnClickListener {
	
	public static Typeface russo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		russo = Typeface.createFromAsset(getAssets(), "fonts/Russo_One.ttf");

		setContentView(R.layout.menu_screen);
		
		SignInButton button = (SignInButton) findViewById(R.id.button_sign_in);
		button.setOnClickListener(this);
		button.setSize(SignInButton.SIZE_WIDE); // I commend anyone who can do this in XML
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.button_sign_in) {
			//beginUserInitiatedSignIn(); For now we skip this
			onSignInSucceeded();
		} else if (v.getId() == R.id.refresh) {
			findViewById(R.id.refresh).setVisibility(View.GONE);
			findViewById(R.id.refresh_progress).setVisibility(View.VISIBLE);
			//TODO: Populate menu feed
		}
	}
	
	@Override
	public void onSignInFailed() {
		System.out.println("oh noes!");
	}
	
	@Override
	public void onSignInSucceeded() {
		findViewById(R.id.button_sign_in).setVisibility(View.GONE);
		findViewById(R.id.whysignin).setVisibility(View.GONE);
		findViewById(R.id.main_feed).setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 1F));
		
		// Enable UI
		findViewById(R.id.refresh).setOnClickListener(this);
		
		//TODO: Populate menu feed
		
		//signOut();
	}
	
	public String getUserId() {
		//return getGamesClient().getCurrentPlayer().getPlayerId();
		return "1";
	}
	
}
