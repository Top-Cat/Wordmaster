package uk.co.thomasc.wordmaster.objects.callbacks;

import uk.co.thomasc.wordmaster.objects.User;

public interface UserListener {

	public void onNameLoaded(User user);

	public void onImageLoaded(User user);

}
