package uk.co.thomasc.wordmaster.view.upgrade;

import uk.co.thomasc.wordmaster.BaseGame;
import uk.co.thomasc.wordmaster.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class UpgradeFragment extends Fragment implements OnClickListener {

	public UpgradeFragment() {
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.upgrade_screen, container, false);
		
		rootView.setOnClickListener(this);
		rootView.findViewById(R.id.buy_upgrade).setOnClickListener(this);
		rootView.findViewById(R.id.cancel_upgrade).setOnClickListener(this);
				
		return rootView;
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.buy_upgrade) {
			((BaseGame) getActivity()).buyUpgrade();
		} else if (v.getId() == R.id.cancel_upgrade) {
			getActivity().getSupportFragmentManager().popBackStack("upgrade", 1);
		}
	}

}
