package uk.co.thomasc.wordmaster.util;

import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings("static-method")
public class TimeUtilTest {

	@Test
	public void timeSinceTest() {
		long fiveSeconds = TimeUtil.now() - 5000;
		String timeString = TimeUtil.timeSince(fiveSeconds);
		Assert.assertEquals("5s", timeString);
		
		long sixMinutes = TimeUtil.now() - (6 * 60 * 1000);
		timeString = TimeUtil.timeSince(sixMinutes);
		Assert.assertEquals("6m", timeString);
	}
	
}
