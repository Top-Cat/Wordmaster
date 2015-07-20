package uk.co.thomasc.wordmaster.api;

public abstract class SimpleResponse extends APIResponse {

	@Override
	public final void processResponse(Object obj) {
		onRequestComplete(null);
	}

}
