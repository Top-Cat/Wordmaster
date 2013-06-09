package uk.co.thomasc.wordmaster.view.menu;

import uk.co.thomasc.wordmaster.PlayActivity;
import uk.co.thomasc.wordmaster.objects.Game;
import uk.co.thomasc.wordmaster.objects.User;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

public class MenuListFragment extends ListFragment {
	
	private Activity act;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		this.act = activity;
		
		MenuAdapter adapter = new MenuAdapter(activity);
		// TODO: Delete examples
		adapter.add(new Game("123", new User("123", "Josh", Uri.EMPTY), new User("124", "Adam", Uri.EMPTY)));
		adapter.add(new Game("123", new User("123", "Josh", Uri.EMPTY), new User("124", "Adam", Uri.EMPTY)));
		adapter.add(new Game("123", new User("123", "Josh", Uri.EMPTY), new User("124", "Adam", Uri.EMPTY)));
		setListAdapter(adapter);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		Intent detailIntent = new Intent(act, PlayActivity.class);
        detailIntent.putExtra(MenuDetailFragment.ARG_ITEM_ID, id);
        startActivity(detailIntent);
	}
	
}