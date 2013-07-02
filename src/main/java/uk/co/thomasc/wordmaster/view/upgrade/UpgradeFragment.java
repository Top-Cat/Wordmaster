package uk.co.thomasc.wordmaster.view.upgrade;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import uk.co.thomasc.wordmaster.BaseGame;
import uk.co.thomasc.wordmaster.R;
import uk.co.thomasc.wordmaster.iab.IabHelper.QueryInventoryFinishedListener;
import uk.co.thomasc.wordmaster.iab.IabResult;
import uk.co.thomasc.wordmaster.iab.Inventory;

public class UpgradeFragment extends Fragment implements OnClickListener, QueryInventoryFinishedListener {

	private View rootView;
	private boolean completed = false;
	private boolean iabAvailable = true;

	public UpgradeFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.upgrade_screen, container, false);

		rootView.setOnClickListener(this);
		rootView.findViewById(R.id.buy_upgrade).setOnClickListener(this);
		rootView.findViewById(R.id.cancel_upgrade).setOnClickListener(this);

		if (((BaseGame) getActivity()).isIabAvailable()) {
			((BaseGame) getActivity()).queryInventory(this);
		} else {
			iabAvailable = false;
		}

		return rootView;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.buy_upgrade) {
			if (completed) {
				getActivity().getSupportFragmentManager().popBackStack("upgrade", 1);
			} else if (iabAvailable) {
				((BaseGame) getActivity()).buyUpgrade();
			} else {
				upgradeFailed();
			}
		} else if (v.getId() == R.id.cancel_upgrade) {
			getActivity().getSupportFragmentManager().popBackStack("upgrade", 1);
		}
	}

	@Override
	public void onQueryInventoryFinished(IabResult result, Inventory inv) {
		if (!result.isFailure()) {
			Button button = (Button) rootView.findViewById(R.id.buy_upgrade);
			button.setText(button.getText() + " - " + inv.getSkuDetails(BaseGame.upgradeSKU).getPrice());
		}
	}

	public void upgradeComplete() {
		completed = true;
		rootView.findViewById(R.id.cancel_upgrade).setVisibility(View.GONE);
		((Button) rootView.findViewById(R.id.buy_upgrade)).setText(R.string.OK);
		((TextView) rootView.findViewById(R.id.why_upgrade)).setText(R.string.upgrade_thanks);
		((TextView) rootView.findViewById(R.id.why_upgrade_cont)).setText(R.string.upgrade_thanks_cont);
	}

	public void upgradeFailed() {
		completed = false;
		((Button) rootView.findViewById(R.id.buy_upgrade)).setText(R.string.try_again);
		((TextView) rootView.findViewById(R.id.why_upgrade)).setText(R.string.upgrade_error);
		((TextView) rootView.findViewById(R.id.why_upgrade_cont)).setText(R.string.upgrade_error_cont);
	}

}
