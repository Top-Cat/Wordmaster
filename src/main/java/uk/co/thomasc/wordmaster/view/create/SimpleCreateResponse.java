package uk.co.thomasc.wordmaster.view.create;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import uk.co.thomasc.wordmaster.BaseGame;
import uk.co.thomasc.wordmaster.R;
import uk.co.thomasc.wordmaster.api.CreateResponse;
import uk.co.thomasc.wordmaster.objects.Game;
import uk.co.thomasc.wordmaster.objects.User;
import uk.co.thomasc.wordmaster.view.DialogPanel;
import uk.co.thomasc.wordmaster.view.Errors;
import uk.co.thomasc.wordmaster.view.upgrade.UpgradeFragment;

class SimpleCreateResponse extends CreateResponse {
	
	private Fragment fragment;

	public SimpleCreateResponse(Fragment f, User opp) {
		super(opp);
		this.fragment = f;
	}

	@Override
	public void onRequestFailed(final int errorCode) {
		if (errorCode == 4) {
			UpgradeFragment upgradeFragment = new UpgradeFragment();
			FragmentTransaction ft = fragment.getFragmentManager().beginTransaction();
			ft.setCustomAnimations(R.anim.fadein, 0, 0, R.anim.fadeout);
			ft.addToBackStack("upgrade")
				.add(R.id.outer, upgradeFragment, UpgradeFragment.TAG)
				.commit();
		} else {
			fragment.getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					DialogPanel netError = (DialogPanel) fragment.getView().findViewById(R.id.dialog_panel);
					if (errorCode == 5) {
						netError.show(Errors.MATCH);
					} else if (errorCode == 9) {
						netError.show(Errors.AUTOMATCH);
					} else {
						netError.show(Errors.SERVER);
					}
				}
			});
		}
	}

	@Override
	public void onRequestComplete(final Game game) {
		((BaseGame) fragment.getActivity()).goToGame(game.getID());
	}
};