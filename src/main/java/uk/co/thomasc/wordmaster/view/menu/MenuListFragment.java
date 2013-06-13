package uk.co.thomasc.wordmaster.view.menu;

import uk.co.thomasc.wordmaster.BaseGame;
import uk.co.thomasc.wordmaster.PlayActivity;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

public class MenuListFragment extends ListFragment {
	
	private BaseGame act;
	private MenuAdapter adapter;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		MenuAdapter adapter = new MenuAdapter(activity);
		((BaseGame) activity).adapter = adapter;
		setListAdapter(adapter);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		Intent detailIntent = new Intent(act, PlayActivity.class);
        detailIntent.putExtra(MenuDetailFragment.ARG_ITEM_ID, adapter.getItem((int) id).getID());
        startActivity(detailIntent);
	}
	
}